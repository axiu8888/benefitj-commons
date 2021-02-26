package com.benefitj.core;

import java.nio.ByteOrder;

/**
 * 16进制转换
 */
public class HexHelper {

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
   * 是否使用本地缓冲
   */
  private boolean local = false;
  /**
   * 缓冲
   */
  private final BufCopy bufCopy = BufCopy.newBufCopy();
  /**
   * 默认字节序，默认大端字节顺序（高位在前，低位在后）
   */
  private ByteOrder order = ByteOrder.BIG_ENDIAN;

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  public BufCopy getBufCopy() {
    return bufCopy;
  }

  public byte[] getCache(int size) {
    return getCache(size, isLocal());
  }

  public byte[] getCache(int size, boolean local) {
    return bufCopy.getCache(size, local);
  }

  public ByteOrder getOrder() {
    return order;
  }

  public void setOrder(ByteOrder order) {
    this.order = order;
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
    // 大端字节顺序：高位在前，低位在后
    // 小端字节顺序：低位在前，高位在后
    short value = 0;
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    // 正数的原码，高位为0，反码/补码均与原码相同；
    // 负数的原码：高位为1, 其他为正数的原码；反码是除符号位，其它按位取反；补码在反码的基础上 + 1
    if (bigEndian) {
      if (signed && ((bytes[0] & 0b10000000) >> 7) == 1) {
        for (byte b : bytes) {
          value <<= 8;
          value |= ~b & 0xFF;
        }
        value = (short) ((-value) - 1);
      } else {
        for (byte b : bytes) {
          value <<= 8;
          value |= b & 0xFF;
        }
      }
    } else {
      if (signed && ((bytes[bytes.length - 1] & 0b10000000) >> 7) == 1) {
        for (int i = bytes.length - 1; i >= 0; i--) {
          value <<= 8;
          value |= ~bytes[i] & 0xFF;
        }
        value = (short) ((-value) - 1);
      } else {
        for (int i = bytes.length - 1; i >= 0; i--) {
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
    // 大端字节顺序：高位在前，低位在后
    // 小端字节顺序：低位在前，高位在后
    int value = 0;
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    // 正数的原码，高位为0，反码/补码均与原码相同；
    // 负数的原码：高位为1, 其他为正数的原码；反码是除符号位，其它按位取反；补码在反码的基础上 + 1
    if (bigEndian) {
      if (signed && ((bytes[0] & 0b10000000) >> 7) == 1) {
        for (byte b : bytes) {
          value <<= 8;
          value |= ~b & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (byte b : bytes) {
          value <<= 8;
          value |= b & 0xFF;
        }
      }
    } else {
      if (signed && ((bytes[bytes.length - 1] & 0b10000000) >> 7) == 1) {
        for (int i = bytes.length - 1; i >= 0; i--) {
          value <<= 8;
          value |= ~bytes[i] & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (int i = bytes.length - 1; i >= 0; i--) {
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
    // 大端字节顺序：高位在前，低位在后
    // 小端字节顺序：低位在前，高位在后
    long value = 0;
    boolean bigEndian = (order == ByteOrder.BIG_ENDIAN);
    // 正数的原码，高位为0，反码/补码均与原码相同；
    // 负数的原码：高位为1, 其他为正数的原码；反码是除符号位，其它按位取反；补码在反码的基础上 + 1
    if (bigEndian) {
      if (signed && ((bytes[0] & 0b10000000) >> 7) == 1) {
        for (byte b : bytes) {
          value <<= 8;
          value |= ~b & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (byte b : bytes) {
          value <<= 8;
          value |= b & 0xFF;
        }
      }
    } else {
      if (signed && ((bytes[bytes.length - 1] & 0b10000000) >> 7) == 1) {
        for (int i = bytes.length - 1; i >= 0; i--) {
          value <<= 8;
          value |= ~bytes[i] & 0xFF;
        }
        value = (-value) - 1;
      } else {
        for (int i = bytes.length - 1; i >= 0; i--) {
          value <<= 8;
          value |= bytes[i] & 0xFF;
        }
      }
    }
    return value;
  }

  /**
   * 整形转换成16进制
   *
   * @param num 数值
   * @return 返回16进制字符串
   */
  public byte[] intToBytes2(int num) {
    return hexToBytes(intToHex(num));
  }

  /**
   * 整形转换成16进制
   *
   * @param num 数值
   * @return 返回16进制字符串
   */
  public String intToHex(int num) {
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
      byte b = bytes[i];
      // 高四位
      sb.append(BINARY_STR[(b & 0xF0) >> 4]);
      // 低四位
      sb.append(BINARY_STR[b & 0x0F]);
      // 分割
      if (split != null && i % len == 0) {
        sb.append(i > 0 && i < bytes.length - 1 ? split : "");
      }
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
      return null;
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
