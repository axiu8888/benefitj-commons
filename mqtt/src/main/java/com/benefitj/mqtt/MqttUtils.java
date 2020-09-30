package com.benefitj.mqtt;

import com.benefitj.core.BufCopy;
import com.benefitj.mqtt.buf.MqttByteBuf;
import com.benefitj.mqtt.message.CONNECT;
import com.benefitj.mqtt.message.MqttMessageType;
import org.apache.commons.lang3.StringUtils;

/**
 * MQTT工具
 * <p>
 * 报文格式：
 * Fixed Header   Variable Header    Payload
 * 固定报头     +   可变报头      +   有效载荷
 * <p>
 * 固定报头： 1个字节(报文类型 + 标志位) +  1 ~ 4 个字节的剩余长度
 * 可变报头： 参考控制报文
 * 有效载荷： ......
 */
public class MqttUtils {

  // 0(0x00)                              127(0x7F)
  // 128(0x00)                            16383(0xFF 0x7F)
  // 16384(0x80 0x80 0x01)                2097151(0xFF 0xFF 0x7F)
  // 2097152(0x80 0x80 0x80 0x01)         268435455(0xFF 0xFF 0xFF 0x7F)

  /**
   * 协议支持的最大长度, 128 * 128 * 128 * 128 - 1
   */
  private static final int MAX_LENGTH = 268435455;

  /**
   * 缓存剩余长度的字节
   */
  private static final BufCopy COPY = new BufCopy();


  /**
   * 获取缓存字节
   */
  private static byte[] getCache(int size) {
    return getCache(size, true);
  }

  /**
   * 获取软引用的缓存字节
   */
  private static byte[] getCache(int size, boolean local) {
    return COPY.getCache(size, local);
  }

  /**
   * 对剩余长度编码，最多4个字节
   *
   * @param length 长度
   * @return 返回剩余长度的字节
   */
  public static byte[] remainingLengthEncode(int length) {
    return remainingLengthEncode(length, false);
  }

  /**
   * 对剩余长度编码，最多4个字节
   *
   * @param length 长度
   * @param local  是否为本地缓存的字节数组
   * @return 返回剩余长度的字节
   */
  public static byte[] remainingLengthEncode(int length, boolean local) {
    if (length > MAX_LENGTH) {
      throw new IllegalArgumentException("Required max length " + MAX_LENGTH + ", current length: " + length);
    }
    // 每个字节的高位用于标识是否还有长度，低7位
    byte[] buff = getCache(4);
    int index = 0;
    int x = length;
    do {
      buff[index] = (byte) (x % 128);
      x = x / 128;
      // if there are more data to encode, set the top bit of this byte
      if (x > 0) {
        buff[index] = (byte) (buff[index] | 128);
      }
      index++;
    } while (x > 0 && index < 4);
    if (index != buff.length || !local) {
      byte[] remainLength = getCache(index, local);
      System.arraycopy(buff, 0, remainLength, 0, remainLength.length);
      return remainLength;
    }
    return buff;
  }

  /**
   * 解码的剩余长度
   *
   * @param remainLength 剩余长度字节
   * @return 返回解码后的剩余长度
   */
  public static int remainingLengthDecode(byte[] remainLength) {
    return remainingLengthDecode(remainLength, 0);
  }

  /**
   * 解码的剩余长度
   *
   * @param remainLength 剩余长度字节
   * @param start        开始的位置
   * @return 返回解码后的剩余长度
   */
  public static int remainingLengthDecode(byte[] remainLength, int start) {
    int multiplier = 1;
    int value = 0;
    byte encodedByte;
    for (int i = 0; i < 4; i++) {
      encodedByte = remainLength[i + start];
      value += (encodedByte & 127) * multiplier;
      if ((encodedByte & 128) == 0) {
        break;
      }
      multiplier *= 128;
      if (multiplier > MAX_LENGTH) {
        throw new IllegalArgumentException("Malformed Remaining Length");
      }
    }
    return value;
  }

  /**
   * 对连接报文编码
   *
   * @param connect 连接报文
   * @return 返回编码后的数据
   */
  public static byte[] connectEncode(CONNECT connect) {
    if (StringUtils.isEmpty(connect.getClientId())) {
      throw new IllegalArgumentException("client id is empty");
    }

    if (connect.isWillFlag()
        && StringUtils.isAnyBlank(connect.getWillTopic(), connect.getWillMessage())) {
      throw new IllegalStateException("连接标志为1，必须设置will topic和will message");
    }
    // 检查 will topic 和 will message

    final MqttByteBuf buf = new MqttByteBuf(1024 << 4);
    // 固定报头
    // 类型和标志位00010000
    MqttMessageType msgType = connect.getMessageType();
    // flags为0000
    buf.writeByte((byte) (msgType.getValue() << 4)); // fixed header
    // 剩余长度(待计算)
    //buf.write(0);

    // 可变报头: 协议名(2 + n)、协议等级(1)、连接标志(1)、保持连接(2)

    // 协议名
    buf.put(connect.getProtocolName(), "MQTT");
    // 协议等级
    buf.writeByte((byte) (connect.getProtocolLevel() & 0xFF));

    // 连接标志
    // Bit   7         6          5          4  3       2           1             0
    //  *  username  password  will Retain  will QoS  will Flag  Clean Session  Reserved
    byte connectFlags = 0b00000000;
    // 清理回话
    if (connect.isCleanSession()) {
      connectFlags |= 0b00000010;
    }
    // 遗嘱标志
    if (connect.isWillFlag()) {
      // Will Flag
      connectFlags |= 0b00000100;
      // Will QoS
      connectFlags |= ((byte) 0b00011000);
      // Will Retain
      if (connect.isWillRetain()) {
        connectFlags |= 0b00100000;
      }
    }
    if (StringUtils.isNotBlank(connect.getUsername())) {
      connectFlags |= 0b10000000;
      // 密码
      if (connect.getPassword() != null) {
        connectFlags |= 0b01000000;
      }
    }
    // 连接标志
    buf.writeByte(connectFlags);
    // 保持连接
    buf.writeShort((short) connect.getKeepAlive());

    // 有效载荷 ...

    // 客户端标识
    buf.put(connect.getClientId());
    // will topic
    buf.put(connect.getWillTopic());
    // will message
    buf.put(connect.getWillMessage());
    // username
    buf.put(connect.getUsername());
    // password
    buf.put(connect.getPassword(), true);

    return buf.readBytes();
  }


}
