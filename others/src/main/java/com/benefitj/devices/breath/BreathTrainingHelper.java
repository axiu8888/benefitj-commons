package com.benefitj.devices.breath;

import com.benefitj.core.BufCopy;
import com.benefitj.core.ByteArrayBuf;
import com.benefitj.core.HexUtils;
import com.benefitj.core.SingletonSupplier;

import java.nio.ByteOrder;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BreathTrainingHelper {

  public static SingletonSupplier<BreathTrainingHelper> singleton = SingletonSupplier.of(BreathTrainingHelper::new);

  public static BreathTrainingHelper get() {
    return singleton.get();
  }

  /**
   * 拷贝字节
   */
  final BufCopy copy = new BufCopy.SimpleBufCopy();
  /**
   * 缓存数据
   */
  final ByteArrayBuf buf = new ByteArrayBuf(1024 * 1024);

  /**
   * 写入，并返回可解析的数据
   *
   * @param bytes 字节
   * @return 返回可解析的数据
   */
  public byte[] append(byte[] bytes) {
    buf.write(bytes);
    while (buf.readableBytes() > 3) {
      buf.markReaderIndex();
      byte[] tmp = buf.readBytes(3);
      buf.resetReaderIndex();
      if ((tmp[0] & 0xFF) != (0x7B & 0xFF)) {
        // 不是 { 开头，继续循环
        buf.skipBytes(1);
        continue;
      }

      if (buf.readableBytes() >= ((tmp[2] & 0xFF) + 8)) {
        buf.markReaderIndex();
        byte[] data = buf.readBytes((tmp[2] & 0xFF) + 8);
        buf.resetReaderIndex();

        // 校验数据的正确性，如果不正确，就逐个丢弃
        if (!verify(data)) {
          for (byte b : data) {
            if ((b & 0xFF) != (0x7B & 0xFF)) {
              buf.skipBytes(1);
              break;
            }
          }
          continue;
        }
        buf.skipBytes(data.length);
        return data;
      } else {
        return null;
      }
    }
    return null;
  }

  /**
   * 计算校验和
   *
   * @param data 数据
   * @return 返回校验和，2字节
   */
  public byte[] checkSum(byte[] data) {
    return CRC16(data, 0, data.length - 5);
  }

  /**
   * 读取指令
   *
   * @param cmd 指令类型
   * @return 返回包装后的指令
   */
  public byte[] readCmd(Cmd cmd) {
    return cmd(0x03, cmd.readAddress, HexUtils.shortToBytes((short) cmd.readPayload));
  }

  /**
   * 写入指令
   *
   * @param cmd 指令类型
   * @return 返回包装后的指令
   */
  public byte[] writeCmd(Cmd cmd) {
    return cmd(0x06, cmd.writeAddress, HexUtils.shortToBytes((short) cmd.writePayload));
  }

  /**
   * [数据长度8 bit)+命令(8 bit)+数据地址(16 bit)+ 数据数量16 bit)+CRC 校验(16bit\r\n，
   * 数据皆为 BIN16 进制。“{”为开始标志 (0x7B),“}\r\n”为结束标志(0x7D 0x0D 0x0A)
   *
   * @param type    命令: 3(读)、6(写)、10(多字节或浮点数 写)
   * @param address 指令地址
   * @param payload 数据
   * @return 返回包装后的指令
   */
  public byte[] cmd(int type, int address, byte... payload) {
    // {}\r\n =>: 1字节{ + 命令(1) + payload.length  + CRC(2) + 3字节的}\r\n
    byte[] data = new byte[1 + 1 + 2 + payload.length + 2 + 3];
    data[0] = 0x7B;
    data[1] = (byte) type;
    byte[] addressBytes = HexUtils.shortToBytes((short) address);
    data[2] = addressBytes[0];
    data[3] = addressBytes[1];
    System.arraycopy(payload, 0, data, 4, payload.length);
    // CRC，第0个开始，到校验和之前
    byte[] crc = CRC16(data, 0, data.length - 5);
    data[data.length - 5] = crc[0];
    data[data.length - 4] = crc[1];
    // }\r\n
    data[data.length - 3] = 0x7D;
    data[data.length - 2] = 0x0D;
    data[data.length - 1] = 0x0A;
    return data;
  }

  /**
   * 验证数据
   *
   * @param data 数据
   * @return 返回验证结果
   */
  public boolean verify(byte[] data) {
    byte[] sum = checkSum(data);
    return data[0] == 0x7B
        && data[data.length - 5] == sum[0]
        && data[data.length - 4] == sum[1]
        && data[data.length - 3] == 0x7D
        && data[data.length - 2] == 0x0D
        && data[data.length - 1] == 0x0A;
  }

  /**
   * 解析数据
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析的数据
   */
  public Map<Type, DataValue> parse(byte[] data, Type start) {
    Type[] types = Type.values();
    byte[] payload = getPayload(data, true);
    if (start == null) {
      return Collections.emptyMap();
    }
    Map<Type, DataValue> values = new LinkedHashMap<>();
    for (int i = 0; i < payload.length; ) {
      if (start.getLen() + i > payload.length) {
        break;
      }
      DataValue v = new DataValue();
      v.setType(start);
      v.setDescriptor(start.getDescriptor());
      byte[] valueBuf = this.copy.copy(payload, i, start.getLen(), false);
      v.setHex(HexUtils.bytesToHex(valueBuf));
      Class<?> klass = start.getKlass();
      if (klass == int.class) {
        v.setValue(HexUtils.bytesToInt(valueBuf));
      } else if (klass == float.class) {
        v.setValue(Float.intBitsToFloat(HexUtils.bytesToInt(valueBuf)));
      } else if (klass == long.class) {
        if (start == Type.COMPILE_TIME) {
          v.setValue(getTime(payload, 0)); // 程序编译时间
        }
      } else if (klass == String.class) {
        if (start == Type.SOFTWARE_VERSION) {
          v.setValue(String.valueOf(getSoftwareVersion(valueBuf, 0)));
        } else {
          v.setValue(HexUtils.bytesToHex(valueBuf));
        }
      }
      values.put(start, v);
      i += start.getLen();
      if (types.length > start.ordinal() + 1) {
        start = types[start.ordinal() + 1];
      } else {
        break;
      }
    }
    return values;
  }

  /**
   * 获取时间，{@link Type#COMPILE_TIME}
   *
   * @param data 数据，4个字节
   * @return 返回解析后的时间戳
   */
  public long getTime(byte[] data, int start) {
    Calendar c = Calendar.getInstance();
    // 011111100110 0100
    //    2022       4
    //     年        月
    c.set(Calendar.YEAR, HexUtils.bytesToInt(data[start], data[start + 1]) >>> 4);
    c.set(Calendar.MONTH, (data[start + 1] & 0b00001111) - 1);
    // 11110 10000 001000
    //  30     16     8
    //  日     时     分
    c.set(Calendar.DAY_OF_MONTH, (data[start + 2] & 0xFF) >>> 3);
    c.set(Calendar.HOUR_OF_DAY, HexUtils.bytesToInt((byte) (data[start + 2] & 0b00000111), (byte) (data[start + 3] & 0b11000000)) >>> 6);
    c.set(Calendar.MINUTE, data[start + 3] & 0b00111111);
    c.set(Calendar.SECOND, 0);
    return c.getTime().getTime();
  }

  /**
   * 获取版本，{@link Type#SOFTWARE_VERSION}
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回计算后的结果
   */
  public float getSoftwareVersion(byte[] data, int start) {
    return Float.intBitsToFloat(HexUtils.bytesToInt(data[start], data[start + 1], data[start + 2], data[start + 3]));
  }

  /**
   * 拷贝数据
   *
   * @param data 数据
   * @return 返回拷贝的数据
   */
  public byte[] getPayload(byte[] data, boolean local) {
    return copy.copy(data, 3, data[2] & 0xFF, local);
  }

  /**
   * 获取验证码byte数组，基于Modbus CRC16的校验算法
   */
  public static byte[] CRC16(byte[] data, int start, int len) {
    // 预置 1 个 16 位的寄存器为十六进制FFFF, 称此寄存器为 CRC寄存器。
    int crc = 0xFFFF;
    for (int i = 0; i < len; i++) {
      byte b = data[start + i];
      // 把第一个 8 位二进制数据 与 16 位的 CRC寄存器的低 8 位相异或, 把结果放于 CRC寄存器
      crc = ((crc & 0xFF00) | (crc & 0x00FF) ^ (b & 0xFF));
      for (int j = 0; j < 8; j++) {
        // 把 CRC 寄存器的内容右移一位( 朝低位)用 0 填补最高位, 并检查右移后的移出位
        if ((crc & 0x0001) > 0) {
          // 如果移出位为 1, CRC寄存器与多项式A001进行异或
          crc = crc >> 1;
          crc = crc ^ 0xA001;
        } else {
          // 如果移出位为 0,再次右移一位
          crc = crc >> 1;
        }
      }
    }
    return HexUtils.shortToBytes((short) crc, ByteOrder.LITTLE_ENDIAN);
  }

}
