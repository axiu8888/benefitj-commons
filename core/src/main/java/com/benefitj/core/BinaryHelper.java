package com.benefitj.core;

import java.lang.reflect.Array;
import java.nio.ByteOrder;

/**
 * 二进制工具类
 */
public class BinaryHelper {

  public static final BinaryHelper BIG_ENDIAN = new BinaryHelper(false, ByteOrder.BIG_ENDIAN);
  public static final BinaryHelper LITTLE_ENDIAN = new BinaryHelper(false, ByteOrder.LITTLE_ENDIAN);

  /**
   * 获取二进制工具类对象
   *
   * @param order 字节顺序
   * @return 返回对象
   */
  public static BinaryHelper get(ByteOrder order) {
    return order == ByteOrder.LITTLE_ENDIAN
        ? BinaryHelper.LITTLE_ENDIAN
        : BinaryHelper.BIG_ENDIAN;
  }


  public static final byte[] EMPTY_BYTES = new byte[0];

  /**
   * 16进制和2进制转换
   */
  public static final String HEX_UPPER_CASE = "0123456789ABCDEF";
  public static final String HEX_LOWER_CASE = "0123456789abcdef";

  private static final String[] BINARY_STR = {
      "0000", "0001", "0010", "0011", "0100", "0101", "0110", "0111",
      "1000", "1001", "1010", "1011", "1100", "1101", "1110", "1111"
  };

  private static final char[] HEX_CHARS =
      new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


  private static byte[][] MASKS = new byte[][]{
      {0b00000001, 0b00000010, 0b00000100, 0b00001000, 0b00010000, 0b00100000, 0b01000000, (byte) 0b10000000},
      {0b00000011, 0b00000110, 0b00001100, 0b00011000, 0b00110000, 0b01100000, (byte) 0b11000000},
      {0b00000111, 0b00001110, 0b00011100, 0b00111000, 0b01110000, (byte) 0b11100000},
      {0b00001111, 0b00011110, 0b00111100, 0b01111000, (byte) 0b11110000},
      {0b00011111, 0b00111110, 0b01111100, (byte) 0b11111000},
      {0b00111111, 0b01111110, (byte) 0b11111100},
      {0b01111111, (byte) 0b11111110},
      {(byte) 0b11111111}
  };

  /**
   * 缓冲
   */
  private final ByteArrayCopy copy = ByteArrayCopy.newBufCopy();
  /**
   * 是否使用本地缓冲
   */
  private boolean local = false;
  /**
   * 默认字节序，默认大端字节顺序（高位在前，低位在后）
   */
  private ByteOrder order = ByteOrder.BIG_ENDIAN;

  public BinaryHelper() {
  }

  public BinaryHelper(boolean local, ByteOrder order) {
    this.local = local;
    this.order = order;
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  public ByteArrayCopy getBufCopy() {
    return copy;
  }

  public byte[] getCache(int size) {
    return getCache(size, isLocal());
  }

  public byte[] getCache(int size, boolean local) {
    return copy.getCache(size, local);
  }

  public ByteOrder getOrder() {
    return order;
  }

  public void setOrder(ByteOrder order) {
    this.order = order;
  }

  /**
   * 反转数组
   */
  public byte[] reverse(byte[] data) {
    byte[] copy = this.copy.copy(data);
    for (int i = 0; i < copy.length; i++) {
      data[copy.length - 1 - i] = copy[i];
    }
    return data;
  }

  /**
   * 取值
   *
   * @param bits     标志位
   * @param size     bit数量(1~8)
   * @param position bit位置(0~7)
   * @return 返回取值
   */
  public int mask(byte bits, int size, int position) {
    if (size <= 0 || size > 8) {
      throw new IllegalArgumentException("Required size between 1 and 8");
    }
    byte[] mask = MASKS[(size - 1) % 8];
    int i = Math.max((position % 8) + 1 - size, 0);
    return (bits & mask[i] & 0xFF) >>> i;
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @return 返回一个整数
   */
  public byte[] shortToBytes(short num) {
    return shortToBytes(num, order);
  }

  /**
   * 转换成整数
   *
   * @param num   数值
   * @param order 字节序
   * @return 返回一个整数
   */
  public byte[] shortToBytes(short num, ByteOrder order) {
    return shortToBytes(num, 16, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @param bit 位，根据位取几个字节
   * @return 返回一个整数
   */
  public byte[] shortToBytes(short num, int bit) {
    return shortToBytes(num, bit, order);
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param order 字节序
   * @return 返回转换后的字节数组
   */
  public byte[] shortToBytes(short num, int bit, ByteOrder order) {
    int size = bitSize(bit);
    byte[] bytes = getCache(size);
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    for (int i = 0; i < size; i++) {
      // 大端字节顺序：高位在前，低位在后
      // 小端字节顺序：低位在前，高位在后
      bytes[i] = (byte) (bigEndian ? (num >> ((bit - 8) - i * 8)) : (num >> (i * 8)));
    }
    return bytes;
  }

  /**
   * 将数字转换为16进制字符串
   *
   * @param value 值
   * @return 返回16进制的字符串
   */
  public String shortToHex(short value) {
    return bytesToHex(shortToBytes(value));
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @return 返回一个整数
   */
  public byte[] intToBytes(int num) {
    return intToBytes(num, order);
  }

  /**
   * 转换成整数
   *
   * @param num   数值
   * @param order 字节序
   * @return 返回一个整数
   */
  public byte[] intToBytes(int num, ByteOrder order) {
    return intToBytes(num, 32, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @param bit 位，根据位取几个字节
   * @return 返回一个整数
   */
  public byte[] intToBytes(int num, int bit) {
    return intToBytes(num, bit, order);
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param order 字节序
   * @return 返回转换后的字节数组
   */
  public byte[] intToBytes(int num, int bit, ByteOrder order) {
    int size = bitSize(bit);
    byte[] bytes = getCache(size);
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    for (int i = 0; i < size; i++) {
      // 大端字节顺序：高位在前，低位在后
      // 小端字节顺序：低位在前，高位在后
      bytes[i] = (byte) (bigEndian ? (num >> ((bit - 8) - i * 8)) : (num >> (i * 8)));
    }
    return bytes;
  }

  /**
   * 将数字转换为16进制字符串
   *
   * @param value 值
   * @return 返回16进制的字符串
   */
  public String intToHex(int value) {
    return bytesToHex(intToBytes(value));
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @return 返回一个整数
   */
  public byte[] longToBytes(long num) {
    return longToBytes(num, order);
  }

  /**
   * 转换成整数
   *
   * @param num   数值
   * @param order 字节序
   * @return 返回一个整数
   */
  public byte[] longToBytes(long num, ByteOrder order) {
    return longToBytes(num, 64, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @param bit 位，根据位取几个字节
   * @return 返回一个整数
   */
  public byte[] longToBytes(long num, int bit) {
    return longToBytes(num, bit, order);
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param order 字节序
   * @return 返回转换后的字节数组
   */
  public byte[] longToBytes(long num, int bit, ByteOrder order) {
    int size = bitSize(bit);
    byte[] bytes = getCache(size);
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    for (int i = 0; i < size; i++) {
      // 大端字节顺序：高位在前，低位在后  数值先高字节位移，后低字节
      // 小端字节顺序：低位在前，高位在后  数值先取低字节，后高字节依次右移
      bytes[i] = (byte) (bigEndian ? (num >> ((bit - 8) - i * 8)) : (num >> (i * 8)));
    }
    return bytes;
  }

  private int bitSize(int bit) {
    return bit / 8 + (bit % 8 != 0 ? 1 : 0);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节
   * @return 返回一个整数
   */
  public short bytesToShort(byte... bytes) {
    return bytesToShort(bytes, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param signed 是否为有符号整数
   * @return 返回一个整数
   */
  public short bytesToShort(byte[] bytes, boolean signed) {
    return bytesToShort(bytes, order, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节
   * @param order 字节序
   * @return 返回一个整数
   */
  public short bytesToShort(byte[] bytes, ByteOrder order) {
    return bytesToShort(bytes, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回一个整数
   */
  public short bytesToShort(byte[] bytes, ByteOrder order, boolean signed) {
    return bytesToShort(bytes, 0, bytes.length, order, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param offset 偏移量
   * @param len    长度
   * @return 返回一个整数
   */
  public short bytesToShort(byte[] bytes, int offset, int len) {
    return bytesToShort(bytes, offset, len, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param offset 偏移量
   * @param len    长度
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回一个整数
   */
  public short bytesToShort(byte[] bytes, int offset, int len, ByteOrder order, boolean signed) {
    // 大端字节顺序：高位在前，低位在后
    // 小端字节顺序：低位在前，高位在后
    short value = 0;
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    // 正数的原码，高位为0，反码/补码均与原码相同；
    // 负数的原码：高位为1, 其他为正数的原码；反码是除符号位，其它按位取反；补码在反码的基础上 + 1
    if (bigEndian) {
      if (signed && ((bytes[offset] & 0b10000000) >> 7) == 1) {
        for (int i = 0; i < len; i++) {
          value <<= 8;
          value |= ~bytes[offset + i] & 0xFF;
        }
        value = (short) ((-value) - 1);
      } else {
        for (int i = 0; i < len; i++) {
          value <<= 8;
          value |= bytes[offset + i] & 0xFF;
        }
      }
    } else {
      if (signed && ((bytes[offset + len - 1] & 0b10000000) >> 7) == 1) {
        for (int i = len - 1; i >= 0; i--) {
          value <<= 8;
          value |= ~bytes[offset + i] & 0xFF;
        }
        value = (short) ((-value) - 1);
      } else {
        for (int i = len - 1; i >= 0; i--) {
          value <<= 8;
          value |= bytes[i] & 0xFF;
        }
      }
    }
    return value;
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节数组
   * @return 返回整数值
   */
  public int bytesToInt(byte... bytes) {
    return bytesToInt(bytes, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param signed 是否为有符号整数
   * @return 返回整数值
   */
  public int bytesToInt(byte[] bytes, boolean signed) {
    return bytesToInt(bytes, order, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节数组
   * @param order 字节序
   * @return 返回整数值
   */
  public int bytesToInt(byte[] bytes, ByteOrder order) {
    return bytesToInt(bytes, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回整数值
   */
  public int bytesToInt(byte[] bytes, ByteOrder order, boolean signed) {
    return bytesToInt(bytes, 0, bytes.length, order, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param offset 偏移量
   * @param len    长度
   * @return 返回整数值
   */
  public int bytesToInt(byte[] bytes, int offset, int len) {
    return bytesToInt(bytes, offset, len, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param offset 偏移量
   * @param len    长度
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回整数值
   */
  public int bytesToInt(byte[] bytes, int offset, int len, ByteOrder order, boolean signed) {
    // 大端字节顺序：高位在前，低位在后
    // 小端字节顺序：低位在前，高位在后
    int value = 0;
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    // 正数的原码，高位为0，反码/补码均与原码相同；
    // 负数的原码：高位为1, 其他为正数的原码；反码是除符号位，其它按位取反；补码在反码的基础上 + 1
    if (bigEndian) {
      if (signed && ((bytes[offset] & 0b10000000) >> 7) == 1) {
        for (int i = 0; i < len; i++) {
          value <<= 8;
          value |= ~bytes[offset + i] & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (int i = 0; i < len; i++) {
          value <<= 8;
          value |= bytes[offset + i] & 0xFF;
        }
      }
    } else {
      if (signed && ((bytes[offset + len - 1] & 0b10000000) >> 7) == 1) {
        for (int i = len - 1; i >= 0; i--) {
          value <<= 8;
          value |= ~bytes[offset + i] & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (int i = len - 1; i >= 0; i--) {
          value <<= 8;
          value |= bytes[i] & 0xFF;
        }
      }
    }
    return value;
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @return 返回长整数值
   */
  public long bytesToLong(byte... bytes) {
    return bytesToLong(bytes, order);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes  字节数组
   * @param signed 是否为有符号整数
   * @return 返回长整数值
   */
  public long bytesToLong(byte[] bytes, boolean signed) {
    return bytesToLong(bytes, order, signed);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @param order 字节序
   * @return 返回长整数值
   */
  public long bytesToLong(byte[] bytes, ByteOrder order) {
    return bytesToLong(bytes, order, false);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes  字节数组
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回长整数值
   */
  public long bytesToLong(byte[] bytes, ByteOrder order, boolean signed) {
    return bytesToLong(bytes, 0, bytes.length, order, signed);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes  字节数组
   * @param offset 偏移量
   * @param len    长度
   * @return 返回长整数值
   */
  public long bytesToLong(byte[] bytes, int offset, int len) {
    return bytesToLong(bytes, offset, len, order, false);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes  字节数组
   * @param offset 偏移量
   * @param len    长度
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回长整数值
   */
  public long bytesToLong(byte[] bytes, int offset, int len, ByteOrder order, boolean signed) {
    // 大端字节顺序：高位在前，低位在后
    // 小端字节顺序：低位在前，高位在后
    long value = 0;
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    // 正数的原码，高位为0，反码/补码均与原码相同；
    // 负数的原码：高位为1, 其他为正数的原码；反码是除符号位，其它按位取反；补码在反码的基础上 + 1
    if (bigEndian) {
      if (signed && ((bytes[offset] & 0b10000000) >> 7) == 1) {
        for (int i = 0; i < len; i++) {
          value <<= 8;
          value |= ~bytes[offset + i] & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (int i = 0; i < len; i++) {
          value <<= 8;
          value |= bytes[offset + i] & 0xFF;
        }
      }
    } else {
      if (signed && ((bytes[offset + len - 1] & 0b10000000) >> 7) == 1) {
        for (int i = len - 1; i >= 0; i--) {
          value <<= 8;
          value |= ~bytes[offset + i] & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (int i = len - 1; i >= 0; i--) {
          value <<= 8;
          value |= bytes[i] & 0xFF;
        }
      }
    }
    return value;
  }

  /**
   * 将数字转换为16进制字符串
   *
   * @param value 值
   * @return 返回16进制的字符串
   */
  public String longToHex(long value) {
    return bytesToHex(longToBytes(value));
  }


  /**
   * 整形转换成16进制
   *
   * @param num 数值
   * @return 返回16进制字符串
   */
  public String intToHex2B(int num) {
    String hex = Integer.toHexString(num);
    return (hex.length() & 0x01) != 0 ? "0" + hex : hex;
  }

  /**
   * 转换成整数
   *
   * @param b 字节
   * @return 返回一个整数
   */
  public short byteToShort(byte b) {
    return (short) ((b & 0xFF) * 256 + (b & 0xFF));
  }

  /**
   * 取低字节
   */
  public int byteToIntLow(byte b) {
    return (b & 0xFF);
  }

  /**
   * 取高字节
   */
  public int byteToIntHigh(byte b) {
    return (b & 0xFF) * 256;
  }

  /**
   * 字节数组转换成二进制字符串
   *
   * @param bytes 字节数组
   * @return 返回二进制字符串
   */
  public String bytesToBinary(byte... bytes) {
    return bytesToBinary(bytes, " ", 1);
  }

  /**
   * 字节数组转换成二进制字符串
   *
   * @param bytes 字节数组
   * @param split 分隔符
   * @param len   分割的长度
   * @return 返回二进制字符串
   */
  public String bytesToBinary(byte[] bytes, String split, int len) {
    StringBuilder sb = new StringBuilder(bytes.length * 8);
    for (int i = 0; i < bytes.length; i++) {
      // 分割
      if (split != null && (i - 1) % len == 0) {
        sb.append(i > 0 && (i - 1) < bytes.length - 1 ? split : "");
      }
      byte b = bytes[i];
      // 高四位
      sb.append(BINARY_STR[(b & 0xF0) >> 4]);
      // 低四位
      sb.append(BINARY_STR[b & 0x0F]);
    }
    return sb.toString();
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin 二进制字节数组
   * @return 返回16进制字符串或空
   */
  public String byteToHex(byte bin) {
    return byteToHex(bin, false);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @return 返回16进制字符串或空
   */
  public String byteToHex(byte bin, boolean lowerCase) {
    String hex = lowerCase ? HEX_LOWER_CASE : HEX_UPPER_CASE;
    return String.valueOf(hex.charAt((bin & 0xF0) >> 4)) + hex.charAt(bin & 0x0F);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin 二进制字节数组
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin) {
    return bytesToHex(bin, false, null, 1);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin  二进制字节数组
   * @param fill 填充
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, String fill) {
    return bytesToHex(bin, false, fill, 1);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin    二进制字节数组
   * @param fill   填充
   * @param length 分割的长度
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, String fill, int length) {
    return bytesToHex(bin, false, fill, length);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, boolean lowerCase) {
    return bytesToHex(bin, lowerCase, null, 1);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @param fill      填充
   * @param length    分割的长度
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, boolean lowerCase, final String fill, int length) {
    String hex = lowerCase ? HEX_LOWER_CASE : HEX_UPPER_CASE;
    final int split = Math.max(length, 1);
    return bytesToHex(bin, (sb, b, index) -> {
      // 原16进制数据
      fillHex(hex, sb, b);
      // 填充
      if (fill != null && index < (bin.length - 1) && ((index + 1) % split == 0)) {
        sb.append(fill);
      }
    });
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin    二进制字节数组
   * @param prefix 前缀
   * @param suffix 后缀
   * @param length 分割的长度
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, final String prefix, String suffix, int length) {
    return bytesToHex(bin, false, prefix, suffix, length);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @param prefix    前缀
   * @param suffix    后缀
   * @param length    分割的长度
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, boolean lowerCase, final String prefix, String suffix, int length) {
    if (isEmpty(bin)) {
      return null;
    }
    String hex = lowerCase ? HEX_LOWER_CASE : HEX_UPPER_CASE;
    final int split = Math.max(length, 1);
    StringBuilder sb = new StringBuilder();
    for (int i = 0, j = 1; i < bin.length; i++, j++) {
      byte b = bin[i];
      // 填充前缀
      sb.append(prefix != null && i % split == 0 ? prefix : "");
      // 原16进制数据
      fillHex(hex, sb, b);
      // 填充后缀
      sb.append(suffix != null && i < (bin.length - 1) && (j % split == 0) ? suffix : "");
    }
    return sb.toString();
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin      二进制字节数组
   * @param consumer 返回
   * @return 返回16进制字符串或空
   */
  public String bytesToHex(byte[] bin, HexConsumer consumer) {
    if (isEmpty(bin)) {
      return "";
    }
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < bin.length; i++) {
      consumer.accept(sb, bin[i], i);
    }
    return sb.toString();
  }

  private void fillHex(String hex, StringBuilder sb, byte b) {
    // 字节高4位
    sb.append(hex.charAt((b & 0xF0) >> 4));
    // 字节低4位
    sb.append(hex.charAt(b & 0x0F));
  }

  /**
   * 16进制字符串转换成字节数组
   *
   * @param hex 字符串
   * @return 转换的字节数组
   */
  public byte[] hexToBytes(String hex) {
    return hexToBytes(hex, null);
  }

  /**
   * 16进制字符串转换成字节数组
   *
   * @param hex          字符串
   * @param defaultValue 默认值
   * @return 转换的字节数组
   */
  public byte[] hexToBytes(String hex, byte[] defaultValue) {
    if (isNotEmpty(hex)) {
      int length = hex.length() / 2;
      char[] ch = hex.toUpperCase().toCharArray();
      byte[] bin = getCache(length);

      char high;
      char low;
      for (int i = 0; i < length; ++i) {
        high = ch[i * 2];
        low = ch[i * 2 + 1];
        bin[i] = (byte) (charToByte(high) << 4 | charToByte(low));
      }
      return bin;
    }
    return defaultValue;
  }

  private byte charToByte(char c) {
    for (int i = 0; i < HEX_CHARS.length; i++) {
      if (HEX_CHARS[i] == c) {
        return (byte) i;
      }
    }
    return -1;
    // return (byte) "0123456789ABCDEF".indexOf(c);
  }

  /**
   * 16进制字符串转换成整数值
   *
   * @param hex 16进制
   * @return 返回转换后的数据
   */
  public int hexToInt(String hex) {
    return hexToInt(hex, order == ByteOrder.BIG_ENDIAN);
  }

  /**
   * 16进制字符串转换成整数值
   *
   * @param hex       16进制
   * @param bigEndian 是否为大端字节顺序
   * @return 返回转换后的数据
   */
  public int hexToInt(String hex, boolean bigEndian) {
    return bytesToInt(hexToBytes(hex), bigEndian);
  }

  /**
   * 是否相等
   *
   * @param src   原数组
   * @param start 开始的位置
   * @param flag  标志
   * @return 返回是否相等
   */
  public boolean isEquals(byte[] src, int start, byte flag) {
    for (int i = start; i < src.length; i++) {
      if (src[i] != flag) {
        return false;
      }
    }
    return true;
  }

  /**
   * 判断两个字节数组是否相等
   *
   * @param standard 原数组
   * @param dest     目标数组
   * @return 返回是否相等
   */
  public boolean isEquals(byte[] standard, byte[] dest) {
    return isEquals(standard, dest, 0);
  }

  /**
   * 判断两个字节数组是否相等
   *
   * @param standard 原数组
   * @param dest     目标数组
   * @param destPos  目标数组开始的位置
   * @return 返回是否相等
   */
  public boolean isEquals(byte[] standard, byte[] dest, int destPos) {
    return isEquals(standard, dest, destPos, standard.length);
  }

  /**
   * 判断两个字节数组是否相等
   *
   * @param standard 原数组
   * @param dest     目标数组
   * @param destPos  目标数组开始的位置
   * @param len      比较的长度
   * @return 返回是否相等
   */
  public boolean isEquals(byte[] standard, byte[] dest, int destPos, int len) {
    return isEquals(standard, 0, dest, destPos, len);
  }

  /**
   * 判断两个字节数组是否相等
   *
   * @param src     原数组
   * @param srcPos  原数组开始的位置
   * @param dest    目标数组
   * @param destPos 目标数组开始的位置
   * @param len     比较的长度
   * @return 返回是否相等
   */
  public boolean isEquals(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
    if ((src.length - srcPos >= len) && (dest.length - destPos >= len)) {
      for (int i = 0; i < len; i++) {
        if (src[srcPos + i] != dest[i + destPos]) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * 查找匹配的字节数组的开始位置
   *
   * @param src  原数据
   * @param find 被查找的字节
   * @return 返回找到的位置，如果未找到返回-1
   */
  public int indexOf(byte[] src, byte[] find) {
    return indexOf(src, 0, Math.max(src.length - find.length, 1), find);
  }

  /**
   * 查找匹配的字节数组的开始位置
   *
   * @param src   原数据
   * @param start 开始的位置
   * @param len   查找的长度
   * @param find  被查找的字节
   * @return 返回找到的位置，如果未找到返回-1
   */
  public int indexOf(byte[] src, int start, int len, byte[] find) {
    for (int i = 0; i < len; i++) {
      if (src[start + i] == find[0]) {
        int index = start + i;
        for (int j = 0; j < find.length - 1; j++) {
          if (src[start + i + j] != find[j]) {
            index = -1;
            break;
          }
        }
        if (index >= 0) {
          return index;
        }
      }
    }
    return -1;
  }

  /**
   * 解析整数数组
   *
   * @param data   数据
   * @param start  开始的位置
   * @param len    数据占字节的长度
   * @param size   字节长度
   * @param signed 是否为有符号数
   * @return 返回解析的数组
   */
  public short[] parseShortArray(byte[] data, int start, int len, int size, boolean signed) {
    short[] array = new short[len / size];
    for (int i = 0; i < array.length; i++) {
      array[i] = bytesToShort(copy.copy(data, start + i * size, size, true), signed);
    }
    return array;
  }

  /**
   * 解析整数数组
   *
   * @param data   数据
   * @param start  开始的位置
   * @param len    数据占字节的长度
   * @param size   字节长度
   * @param signed 是否为有符号数
   * @return 返回解析的数组
   */
  public int[] parseIntArray(byte[] data, int start, int len, int size, boolean signed) {
    int[] array = new int[len / size];
    for (int i = 0; i < array.length; i++) {
      array[i] = bytesToInt(copy.copy(data, start + i * size, size, true), signed);
    }
    return array;
  }

  /**
   * 解析整数数组
   *
   * @param data   数据
   * @param start  开始的位置
   * @param len    数据占字节的长度
   * @param size   字节长度
   * @param signed 是否为有符号数
   * @return 返回解析的数组
   */
  public long[] parseLongArray(byte[] data, int start, int len, int size, boolean signed) {
    long[] array = new long[len / size];
    for (int i = 0; i < array.length; i++) {
      array[i] = bytesToLong(copy.copy(data, start + i * size, size, true), signed);
    }
    return array;
  }

  /**
   * 数组转换为字节数组
   *
   * @param array   数组
   * @param bitSize 比特位数 8/16/32/64
   * @return 返回转换后的数组
   */
  public byte[] arrayToBytes(Object array, int bitSize) {
    return arrayToBytes(array, 0, Array.getLength(array), bitSize);
  }

  /**
   * 数组转换为字节数组
   *
   * @param array   数组
   * @param start   开始的位置
   * @param len     长度
   * @param bitSize 比特位数 8/16/32/64
   * @return 返回转换后的数组
   */
  public byte[] arrayToBytes(Object array, int start, int len, int bitSize) {
    if (array == null) throw new IllegalArgumentException("数组不能为null");
    if (array.getClass().isArray()) throw new IllegalArgumentException("传入的参数不是数组: " + array.getClass());
    if (Array.getLength(array) <= 0) return EMPTY_BYTES;

    NumberType type = NumberType.of((Number) Array.get(array, 0));
    if (type == null) throw new IllegalArgumentException("不支持的数组类型: " + array.getClass());
    //心电波形数据 1~50
    int byteCount = Math.max(1, bitSize / 8);
    byte[] buf = getCache(len * byteCount);
    for (int i = 0, j = 0; i < len; i++, j += byteCount) {
      Number v = (Number) Array.get(array, start + i);
      byte[] bytes = type.toBytes(v, getOrder());
      int srcPos = getOrder() == ByteOrder.BIG_ENDIAN ? Math.min(bytes.length, byteCount) : 0;//大端字节序需要拷贝后面的字节
      System.arraycopy(bytes, srcPos, buf, j, byteCount);
    }
    return buf;
  }

  private boolean isNotEmpty(String s) {
    return s != null && s.trim().length() > 0;
  }

  private boolean isEmpty(byte[] bytes) {
    return bytes == null || bytes.length <= 0;
  }


  public interface HexConsumer {

    /**
     * 处理
     *
     * @param sb    字符串拼接
     * @param raw   原字节
     * @param index 字节的索引
     */
    void accept(StringBuilder sb, byte raw, int index);

  }
}
