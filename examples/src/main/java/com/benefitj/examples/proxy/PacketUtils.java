package com.benefitj.examples.proxy;

import com.benefitj.core.HexTools;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 采集器UDP包工具
 *
 * <p>包头：2字节，0x55, 0xAA
 * <p>长度：2字节，高位，低位
 * <p>设备ID：4字节
 * <p>包类型：1字节
 * <p>有效载荷：9 ~ n
 * <p>校验和：n + 1，1字节
 */
public class PacketUtils {

  private static final Map<Integer, String> DEVICE_ID_CACHE = new WeakHashMap<>();
  private static final Function<Integer, String> SAVE_FUNC =
      deviceCode -> byteToHex(HexTools.intToByte(deviceCode), true);
  private static final Map<Integer, byte[]> DEVICE_ID_BYTES_CACHE = new WeakHashMap<>();
  private static final Function<Integer, byte[]> SAVE_BYTES_FUNC = HexTools::intToByte;

  /**
   * 包头
   */
  public static final byte[] HEAD = new byte[]{0x55, (byte) 0xAA};
  /**
   * 数据类型的位置
   */
  public static final int TYPE = 8;
  /**
   * 设备ID开始的位置
   */
  public static final int START_DEVICE = 4;
  /**
   * 设备型号开始的位置
   */
  public static final int START_EQUIPMENT_TYPE = 9;
  /**
   * 硬件版本
   */
  public static final int START_HARDWARE_VERSION = 11;
  /**
   * 软件版本
   */
  public static final int START_SOFTWARE_VERSION = 12;
  /**
   * 包序号开始的位置
   */
  public static final int START_PACKAGE_SN = 9;

  /**
   * 自定义消息
   */
  public static final int CUSTOM_MSG = 0xFE;
  /**
   * 空设备
   */
  public static final byte[] EMPTY_DEVICE = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};

  /**
   * 字节数组转换成短整数
   *
   * @param bytes 字节数组
   * @return 返回短整数值
   */
  public static short byteToShort(byte... bytes) {
    return (short) byteToInt(bytes);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节数组
   * @return 返回整数值
   */
  public static int byteToInt(byte... bytes) {
    return byteToInt(bytes, 0, bytes.length);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节数组
   * @return 返回整数值
   */
  public static int byteToInt(byte[] bytes, int start, int len) {
    int value = 0;
    int end = start + len;
    for (int i = start; i < end; i++) {
      value <<= 8;
      value |= bytes[i] & 0xFF;
    }
    return value;
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @return 返回长整数值
   */
  public static long byteToLong(byte... bytes) {
    return byteToLong(bytes, 0, bytes.length);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @return 返回长整数值
   */
  public static long byteToLong(byte[] bytes, int start, int len) {
    long value = 0;
    int end = start + len;
    for (int i = start; i < end; i++) {
      value <<= 8;
      value |= bytes[i] & 0xFF;
    }
    return value;
  }


  /**
   * 获取16进制的字节数组
   *
   * @param hex 16进制
   * @return 返回16进制的字节数组
   */
  public static byte[] hexToByte(String hex) {
    return HexTools.hexToByte(hex);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @return 返回16进制字符串或空
   */
  public static String byteToHex(byte[] bin, boolean lowerCase) {
    return HexTools.byteToHex(bin, lowerCase);
  }

  /**
   * 是否为包头
   *
   * @param data 数据
   * @return 返回校验的结果，如果是返回true，否则返回false
   */
  public static boolean isHead(byte[] data) {
    return isHead(data, 0);
  }

  /**
   * 是否为包头
   *
   * @param data  数据
   * @param start 开始位置
   * @return 返回校验的结果，如果是返回true，否则返回false
   */
  public static boolean isHead(byte[] data, int start) {
    return (data[start] == HEAD[0]) && (data[start + 1] == HEAD[1]);
  }

  /**
   * 获取数据长度，数据的长度除去包头
   *
   * @param data 数据
   * @return 返回数据长度
   */
  public static int length(byte[] data) {
    return length(data, 2);
  }

  /**
   * 获取数据长度，数据的长度除去包头
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回数据的长度
   */
  public static int length(byte[] data, int start) {
    return (int) byteToLong(data[start], data[start + 1]);
  }

  /**
   * 是否匹配数据的长度，不包含包头的2字节
   *
   * @param data 数据
   * @return 返回是否匹配
   */
  public static boolean isLength(byte[] data) {
    return length(data) == (data.length - 2);
  }

  /**
   * 计算校验和，除最后一位外，所有字节顺序累加的结果
   *
   * @param data 数据
   * @return 返回计算的校验和
   */
  public static byte checkSum(byte[] data) {
    byte sum = 0;
    for (int i = 0; i < data.length - 1; i++) {
      sum += data[i];
    }
    return sum;
  }

  /**
   * 设置校验和
   *
   * @param data 数据
   * @return 返回设置校验和的数据
   */
  public static byte[] setCheckSum(byte[] data) {
    data[data.length - 1] = checkSum(data);
    return data;
  }

  /**
   * 验证校验和
   *
   * @param data 数据
   * @return 返回校验和是否正确
   */
  public static boolean isCheckSum(byte[] data) {
    return checkSum(data) == data[data.length - 1];
  }

  /**
   * 校验数据
   *
   * @param data 数据
   * @return 返回数据是否正确
   */
  public static boolean verify(byte[] data) {
    // 包头 && 长度 && 校验和
    return isHead(data) && isLength(data) && isCheckSum(data);
  }

  /**
   * 获取数据类型
   *
   * @param data 数据
   * @return 返回数据类型
   */
  public static int getType(byte[] data) {
    return getType(data, TYPE);
  }

  /**
   * 获取数据类型
   *
   * @param data 数据
   * @return 返回数据类型
   */
  public static int getType(byte[] data, int start) {
    return data[start] & 0xFF;
  }

  /**
   * 判断是否为某个类型
   *
   * @param data 数据
   * @param type 判断的类型
   * @return 返回是否匹配
   */
  public static boolean isType(byte[] data, byte type) {
    return getType(data) == (type & 0xFF);
  }

  /**
   * 获取数据类型
   *
   * @param data 数据
   * @return 返回数据类型
   */
  public static PacketType getPacketType(byte[] data) {
    return PacketType.valueOf(getType(data));
  }

  /**
   * 是否是数据包
   *
   * @param raw 数据
   * @return 返回是否为数据包
   */
  public static boolean isData(byte[] raw) {
    return isHead(raw) && PacketType.isData(getType(raw));
  }

  /**
   * 获取设备ID，小写的16进制
   *
   * @param data 数据
   * @return 返回16进制的设备ID
   */
  public static String getHexDeviceId(byte[] data) {
    //return getHexDeviceId(data, FLAG_DEVICE, true);
    return DEVICE_ID_CACHE.computeIfAbsent((int) byteToLong(data, START_DEVICE, 4), SAVE_FUNC);
  }

  /**
   * 获取设备ID
   *
   * @param data        数据
   * @param start       开始的位置
   * @param isLowerCase 是否小写
   * @return 返回16进制的设备ID
   */
  public static String getHexDeviceId(byte[] data, int start, boolean isLowerCase) {
    byte[] deviceId = getDeviceId(data, start);
    return byteToHex(deviceId, isLowerCase);
  }

  /**
   * 获取整形的设备ID
   *
   * @param data 数据
   * @return 返回整形设备ID
   */
  public static int getDeviceCode(byte[] data) {
    byte[] deviceId = getDeviceId(data);
    return byteToInt(deviceId);
  }

  /**
   * 获取整形的设备ID
   *
   * @param data  数据
   * @param start 开始位置
   * @return 返回整形设备ID
   */
  public static int getDeviceCode(byte[] data, int start) {
    byte[] deviceId = getDeviceId(data, start);
    return byteToInt(deviceId);
  }

  /**
   * 获取设备ID的字节数组
   *
   * @param data 数据
   * @return 返回设备ID的字节数组
   */
  public static byte[] getDeviceId(byte[] data) {
    return getDeviceId(data, START_DEVICE);
  }

  /**
   * 获取设备ID的字节数组
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回设备ID的字节数组
   */
  public static byte[] getDeviceId(byte[] data, int start) {
    // 4 ~ 7
    byte[] deviceId = new byte[4];
    System.arraycopy(data, start, deviceId, 0, deviceId.length);
    return deviceId;
  }

  /**
   * 获取设备ID的字节数组
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回设备ID的字节数组
   */
  public static byte[] getCachedDeviceId(byte[] data, int start) {
    return DEVICE_ID_BYTES_CACHE.computeIfAbsent((int) byteToLong(data, start, 4), SAVE_BYTES_FUNC);
  }

  /**
   * 设备型号，2个字节
   *
   * @param data 数据
   * @return 获取设备型号
   */
  public static int getEquipmentType(byte[] data) {
    return getEquipmentType(data, START_EQUIPMENT_TYPE);
  }

  /**
   * 设备型号，2个字节
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 获取设备型号
   */
  public static int getEquipmentType(byte[] data, int start) {
    return byteToInt(data[start], data[start + 1]);
  }

  /**
   * 获取硬件版本
   *
   * @param data 数据
   * @return 返回版本
   */
  public static String getHardwareVersion(byte[] data) {
    byte b = data[START_HARDWARE_VERSION];
    return (b & 0b11100000) + "." + (b & 0b00011100) + "." + (b & 0b00000011);
  }

  /**
   * 获取软件版本
   *
   * @param data 数据
   * @return 返回版本
   */
  public static String getSoftwareVersion(byte[] data) {
    byte b = data[START_SOFTWARE_VERSION];
    return (b & 0b11100000) + "." + (b & 0b00011100) + "." + (b & 0b00000011);
  }

  /**
   * 获取包序号，4个字节
   *
   * @param data 数据
   * @return 返回包序号
   */
  public static int getPacketSn(byte[] data) {
    return getPacketSn(data, START_PACKAGE_SN);
  }

  /**
   * 获取包序号，4个字节
   *
   * @param data  数据
   * @param start 开始的位置
   * @return 返回包序号
   */
  public static int getPacketSn(byte[] data, int start) {
    return byteToInt(data[start], data[start + 1], data[start + 2], data[start + 3]);
  }

  /**
   * 生成字节数组
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @param len      长度
   * @return 返回生成的字节数组
   */
  public static byte[] generate(String deviceId, byte type, int len) {
    byte[] deviceIdBytes = hexToByte(deviceId);
    return generate(deviceIdBytes, type, len);
  }

  /**
   * 生成字节数组
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @param len      长度
   * @return 返回生成的字节数组
   */
  public static byte[] generate(byte[] deviceId, byte type, int len) {
    byte[] data = new byte[len];
    data[0] = (byte) 0x55;
    data[1] = (byte) 0xAA;
    data[2] = (byte) ((len - 2) >> 8);
    data[3] = (byte) (len - 2);
    // 4 ~ 7
    System.arraycopy(deviceId, 0, data, 4, 4);
    data[8] = type;
    return data;
  }

  /**
   * 生成字节数组
   *
   * @param deviceId 设备ID
   * @param type     类型
   * @param raw      数据
   * @return 返回生成的字节数组
   */
  public static byte[] generate(byte[] deviceId, byte type, byte[] raw) {
    return generate(deviceId, type, raw, 0, raw.length);
  }

  /**
   * 生成字节数组
   *
   * @param deviceId  设备ID
   * @param type      类型
   * @param raw       数据
   * @param rawStart  数据开始的位置
   * @param rawLength 数据长度
   * @return 返回生成的字节数组
   */
  public static byte[] generate(byte[] deviceId, byte type, byte[] raw, int rawStart, int rawLength) {
    // 2(head) + 2(len) + 4(deviceId) + 1(type) + raw.length + 1(checkSum)
    byte[] data = generate(deviceId, type, 2 + 2 + 4 + 1 + raw.length + 1);
    System.arraycopy(raw, rawStart, data, 9, rawLength);
    return setCheckSum(data);
  }

  /**
   * 获取注册反馈，设备上线时，主动往服务端发送注册包，服务端需要给设备反馈
   *
   * @param data 数据
   * @return 返回反馈数据
   */
  public static byte[] getRegisterFeedback(byte[] data) {
    byte[] deviceId = getDeviceId(data);
    return getRegisterFeedback(deviceId, true);
  }

  /**
   * 获取注册反馈，设备上线时，主动往服务端发送注册包，服务端需要给设备反馈
   *
   * @param deviceId   设备ID
   * @param successful 是否成功
   * @return 返回反馈数据
   */
  public static byte[] getRegisterFeedback(byte[] deviceId, boolean successful) {
    return getRegisterFeedback(deviceId, nowS(), successful);
  }

  /**
   * 获取注册反馈，设备上线时，主动往服务端发送注册包，服务端需要给设备反馈
   *
   * @param deviceId   设备ID
   * @param time       时间(精确到秒)
   * @param successful 是否成功
   * @return 返回反馈数据
   */
  public static byte[] getRegisterFeedback(byte[] deviceId, long time, boolean successful) {
    // 反馈类型是 0x02
    byte[] feedback = generate(deviceId, (byte) 0x02, 15);
    feedback[9] = (byte) (successful ? 0xff : 0x00);
    // 设置时间
    setTime(feedback, time, 10);
    // 设置校验和，并返回数据
    return setCheckSum(feedback);
  }

  /**
   * 获取实时数据反馈数据包
   *
   * @param data 实时数据
   * @return 返回反馈字节数组
   */
  public static byte[] getRealtimeFeedback(byte[] data) {
    byte[] deviceId = getDeviceId(data);
    return getRealtimeFeedback(deviceId, (byte) 0x04, getPacketSn(data));
  }

  /**
   * 获取实时数据反馈数据包
   *
   * @param deviceId  设备ID
   * @param type      类型
   * @param packageSn 包号
   * @return 返回反馈字节数组
   */
  public static byte[] getRealtimeFeedback(byte[] deviceId, byte type, int packageSn) {
    return getRealtimeFeedback(deviceId, type, packageSn, true);
  }

  /**
   * 获取实时数据反馈数据包
   *
   * @param deviceId   设备ID
   * @param type       类型
   * @param packageSn  包号
   * @param successful 是否成功
   * @return 返回反馈字节数组
   */
  public static byte[] getRealtimeFeedback(
      byte[] deviceId, byte type, int packageSn, boolean successful) {
    byte[] feedback = generate(deviceId, type, 15);
    setPacketSn(feedback, packageSn, 9);
    feedback[13] = (byte) (successful ? 0x01 : 0x02);
    return setCheckSum(feedback);
  }

  /**
   * 获取删除日志文件的指令，服务端主动发指令删除设备中缓存的日志文件(CHE文件)
   *
   * @param deviceId 设备ID
   * @return 返回指令
   */
  public static byte[] getDeleteLogCmd(String deviceId) {
    // 包类型：0x0A
    byte[] cmd = generate(deviceId, (byte) 0x0A, 14);
    return setCheckSum(cmd);
  }

  /**
   * 是否成功删除日志，0xff 表示删除成功, 0x00 表示删除失败
   *
   * @param data 数据
   * @return 返回是否删除成功
   */
  public static boolean isDeleteLog(byte[] data) {
    return isSuccessful(data, (byte) 0x0A);
  }

  /**
   * 获取校准时间的指令
   *
   * @param deviceId 设备ID
   * @return 返回指令
   */
  public static byte[] getTimeCalibrationCmd(String deviceId) {
    // 包类型：0x09
    byte[] cmd = generate(deviceId, (byte) 0x09, 14);
    // 设置当前时间
    return copyTimeCalibrationCmd(cmd, nowS());
  }

  /**
   * 获取校准时间的指令
   *
   * @param cmd  时间校准指令
   * @param time 时间(精确到秒)
   * @return 返回指令
   */
  public static byte[] copyTimeCalibrationCmd(byte[] cmd, long time) {
    // 包类型：0x09
    // 设置当前时间
    setTime(cmd, time, 9);
    return setCheckSum(cmd);
  }

  /**
   * 获取注销设备的指令
   *
   * @param deviceId 设备ID
   * @return 返回指令
   */
  public static byte[] getUnregisterCmd(byte[] deviceId) {
    return getUnregisterCmd(deviceId, nowS());
  }

  /**
   * 获取注销设备的指令
   *
   * @param deviceId 设备ID
   * @param time     时间(秒)
   * @return 返回指令
   */
  public static byte[] getUnregisterCmd(byte[] deviceId, long time) {
    byte[] cmd = generate(deviceId, (byte) 0x0C, 14);
    setTime(cmd, time, 9);
    return setCheckSum(cmd);
  }

  /**
   * 是否注销成功
   *
   * @param data 数据
   * @return 返回是否注销
   */
  public static boolean isUnregisterSuccessful(byte[] data) {
    return isSuccessful(data, (byte) 0x0C);
  }

  /**
   * 获取开关指令
   *
   * @param deviceId 设备ID
   * @param status   开关状态，具体值请参考 {@link SwitchType#openSwitch(SwitchType...)}
   * @return 返回设置开关状态的指令
   */
  public static byte[] getSwitchCmd(String deviceId, byte status) {
    byte[] cmd = generate(deviceId, (byte) 0x0B, 14);
    cmd[9] = status;
    return setCheckSum(cmd);
  }

  /**
   * 开关是否改变
   *
   * @param data 数据
   * @return 返回是否改变
   */
  public static boolean isSwitchChanged(byte[] data) {
    return isSuccessful(data, (byte) 0x0B);
  }

  /**
   * 下发或获取蓝牙外设的MAC地址
   * <p>
   * 操作类型：0x01:下发； 0x02:查询
   * <p>
   * 设备类型： 0x00:体温计；0x01:血氧仪； 0x02:血压计； 0x03:流速仪
   *
   * @param deviceId    设备ID
   * @param mac         蓝牙外设的MAC
   * @param operateType 操作类型
   * @param deviceType  外设类型
   * @return 返回设置指令
   */
  public static byte[] getBluetoothMacCmd(String deviceId, String mac, byte operateType, byte deviceType) {
    byte[] macBytes;
    if (operateType == 0x01) {
      if (mac == null || mac.trim().isEmpty()) {
        throw new IllegalArgumentException("mac");
      }
      String address = mac.contains(":") ? mac.replaceAll(":", "") : mac;
      macBytes = hexToByte(address);
    } else {
      macBytes = null;
    }
    return getBluetoothMacCmd(deviceId, macBytes, operateType, deviceType);
  }

  /**
   * 下发或获取蓝牙外设的MAC地址
   * <p>
   * 操作类型：0x01:下发； 0x02:查询
   * <p>
   * 设备类型： 0x00:体温计；0x01:血氧仪； 0x02:血压计； 0x03:流速仪
   *
   * @param deviceId    设备ID
   * @param mac         蓝牙外设的MAC
   * @param operateType 操作类型
   * @param deviceType  外设类型
   * @return 返回设置指令
   */
  public static byte[] getBluetoothMacCmd(String deviceId, byte[] mac, byte operateType, byte deviceType) {
    if ((operateType != 0x01) && (operateType != 0x02)) {
      throw new IllegalArgumentException("不支持的操作类型");
    }
    if ((deviceType < 0x00) || (deviceType > 0x03)) {
      throw new IllegalArgumentException("不支持的设备类型");
    }
    byte[] cmd = generate(deviceId, (byte) 0x0D, 18);
    cmd[9] = operateType;
    cmd[10] = deviceType;
    if (operateType == 0x01 && mac != null) {
      System.arraycopy(mac, 0, cmd, 11, cmd.length);
    }
    return setCheckSum(cmd);
  }

  /**
   * 获取重传包指令
   *
   * @param deviceId 设备ID
   * @param packetSn 包序号
   * @param len      重传长度
   * @return 返回重传指令
   */
  public static byte[] getRetransmissionCmd(String deviceId, int packetSn, int len) {
    return getRetransmissionCmd(hexToByte(deviceId), packetSn, len);
  }

  /**
   * 获取重传包指令
   *
   * @param deviceId 设备ID
   * @param packetSn 包序号
   * @param len      重传长度(最多10个包)
   * @return 返回重传指令
   */
  public static byte[] getRetransmissionCmd(byte[] deviceId, int packetSn, int len) {
    byte[] cmd = generate(deviceId, (byte) 0x08, 15);
    cmd[9] = (byte) Math.min(10, Math.max(1, len));
    setPacketSn(cmd, packetSn, 10);
    return setCheckSum(cmd);
  }

  /**
   * 获取集中上传通用数据包指令
   *
   * @param deviceId 设备ID
   * @param first    第一个包的序号
   * @param last     最后一个包的序号
   * @return 返回上传通用数据包指令
   */
  public static byte[] getUploadCmd(String deviceId, int first, int last) {
    return getUploadCmd(hexToByte(deviceId), first, last);
  }

  /**
   * 获取集中上传通用数据包指令
   *
   * @param deviceId 设备ID
   * @param first    第一个包的序号
   * @param last     最后一个包的序号
   * @return 返回上传通用数据包指令
   */
  public static byte[] getUploadCmd(byte[] deviceId, int first, int last) {
    byte[] cmd = generate(deviceId, (byte) 0x10, 18);
    setPacketSn(cmd, first, 9);
    setPacketSn(cmd, last, 13);
    return setCheckSum(cmd);
  }

  /**
   * 设置包序号
   *
   * @param buff     字节数据
   * @param packetSn 包序号
   * @param start    开始的位置
   * @return 返回设置后的数据
   */
  private static byte[] setPacketSn(byte[] buff, int packetSn, int start) {
    buff[start] = (byte) ((packetSn >> 24) & 0xFF);
    buff[start + 1] = (byte) ((packetSn >> 16) & 0xFF);
    buff[start + 2] = (byte) ((packetSn >> 8) & 0xFF);
    buff[start + 3] = (byte) ((packetSn) & 0xff);
    return buff;
  }

  /**
   * 设置时间
   *
   * @param data  数据
   * @param time  时间（精确到秒）
   * @param start 开始的位置
   * @return 返回设置时间后的数据
   */
  public static byte[] setTime(byte[] data, long time, int start) {
    data[start] = (byte) ((time >> 24) & 0xFF);
    data[start + 1] = (byte) ((time >> 16) & 0xFF);
    data[start + 2] = (byte) ((time >> 8) & 0xFF);
    data[start + 3] = (byte) ((time) & 0xFF);
    return data;
  }

  /**
   * 获取时间戳
   *
   * @param data  数据
   * @param start 开始的位置
   * @param end   结束的位置
   * @return 返回时间戳，精确到毫秒
   */
  public static long getTime(byte[] data, int start, int end) {
    long date = byteToLong(data[start],
        data[start + 1],
        data[start + 2],
        data[start + 3]) * 1000;
    if ((end - start) <= 4) {
      return date;
    }
    return date + byteToLong(data[start + 4], data[start + 5]);
  }

  /**
   * @return 返回当前时间，精确到秒
   */
  private static long nowS() {
    return System.currentTimeMillis() / 1000;
  }

  private static boolean isSuccessful(byte[] data, byte flag) {
    return (data[TYPE] == flag) && ((data[9] & 0xFF) == 0xFF);
  }

  /**
   * 消息体的长度
   */
  public static int bodyLength(byte[] data) {
    return length(data) - 7;
  }

  /**
   * 生成自定义消息
   *
   * @param deviceId 设备ID
   * @param msg      消息
   * @return 返回数据
   */
  public static byte[] generateCustomMsg(String deviceId, byte[] msg) {
    return generateCustomMsg(hexToByte(deviceId), msg);
  }

  /**
   * 生成自定义消息
   *
   * @param deviceId 设备ID
   * @param msg      消息
   * @return 返回数据
   */
  public static byte[] generateCustomMsg(byte[] deviceId, byte[] msg) {
    return generate(deviceId, (byte) CUSTOM_MSG, msg);
  }

  /**
   * 生成自定义消息
   *
   * @param msg 消息
   * @return 返回数据
   */
  public static byte[] generateCustomMsg(byte[] msg) {
    return generate(EMPTY_DEVICE, (byte) CUSTOM_MSG, msg);
  }

  /**
   * 是否为自定义消息
   *
   * @param data 数据
   * @return 返回是否为自定义消息
   */
  public static boolean isCustomMsg(byte[] data) {
    return getType(data) == CUSTOM_MSG;
  }

  /**
   * 只读取消息体
   *
   * @param data 数据
   * @return 返回读取的消息体
   */
  public static byte[] readBody(byte[] data) {
    return readBody(data, new byte[data.length - 10]);
  }

  /**
   * 只读取消息体
   *
   * @param data 数据
   * @param body 消息体
   * @return 返回读取的消息体
   */
  public static byte[] readBody(byte[] data, byte[] body) {
    System.arraycopy(data, 9, body, 0, body.length);
    return body;
  }

}
