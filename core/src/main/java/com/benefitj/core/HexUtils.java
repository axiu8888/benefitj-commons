package com.benefitj.core;

import java.nio.ByteOrder;

/**
 * 16进制转换
 */
public class HexUtils {

  static final SingletonSupplier<BinaryHelper> singleton = SingletonSupplier.of(() -> new BinaryHelper(false, ByteOrder.BIG_ENDIAN));

  static BinaryHelper getHelper() {
    return singleton.get();
  }

  /**
   * 反转数组
   */
  public static byte[] reverse(byte[] data) {
    return getHelper().reverse(data);
  }

  /**
   * 取值
   *
   * @param bits     标志位
   * @param size     bit数量(1~8)
   * @param position bit位置(0~7)
   * @return 返回取值
   */
  public static int mask(byte bits, int size, int position) {
    return getHelper().mask(bits, size, position);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @return 返回一个整数
   */
  public static byte[] shortToBytes(short num) {
    return getHelper().shortToBytes(num);
  }

  /**
   * 转换成整数
   *
   * @param num   数值
   * @param order 字节序
   * @return 返回一个整数
   */
  public static byte[] shortToBytes(short num, ByteOrder order) {
    return getHelper().shortToBytes(num, 16, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @param bit 位，根据位取几个字节
   * @return 返回一个整数
   */
  public static byte[] shortToBytes(short num, int bit) {
    return getHelper().shortToBytes(num, bit);
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param order 字节序
   * @return 返回转换后的字节数组
   */
  public static byte[] shortToBytes(short num, int bit, ByteOrder order) {
    return getHelper().shortToBytes(num, bit, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @return 返回一个整数
   */
  public static byte[] intToBytes(int num) {
    return getHelper().intToBytes(num);
  }

  /**
   * 转换成整数
   *
   * @param num   数值
   * @param order 字节序
   * @return 返回一个整数
   */
  public static byte[] intToBytes(int num, ByteOrder order) {
    return getHelper().intToBytes(num, 32, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @param bit 位，根据位取几个字节
   * @return 返回一个整数
   */
  public static byte[] intToBytes(int num, int bit) {
    return getHelper().intToBytes(num, bit);
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param order 字节序
   * @return 返回转换后的字节数组
   */
  public static byte[] intToBytes(int num, int bit, ByteOrder order) {
    return getHelper().intToBytes(num, bit, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @return 返回一个整数
   */
  public static byte[] longToBytes(long num) {
    return getHelper().longToBytes(num);
  }

  /**
   * 转换成整数
   *
   * @param num   数值
   * @param order 字节序
   * @return 返回一个整数
   */
  public static byte[] longToBytes(long num, ByteOrder order) {
    return getHelper().longToBytes(num, 64, order);
  }

  /**
   * 转换成整数
   *
   * @param num 数值
   * @param bit 位，根据位取几个字节
   * @return 返回一个整数
   */
  public static byte[] longToBytes(long num, int bit) {
    return getHelper().longToBytes(num, bit);
  }

  /**
   * 整形数值转换成字节数组
   *
   * @param num   整形数值
   * @param bit   位，根据位取几个字节
   * @param order 字节序
   * @return 返回转换后的字节数组
   */
  public static byte[] longToBytes(long num, int bit, ByteOrder order) {
    return getHelper().longToBytes(num, bit, order);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节
   * @return 返回一个整数
   */
  public static short bytesToShort(byte... bytes) {
    return getHelper().bytesToShort(bytes);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param signed 是否为有符号整数
   * @return 返回一个整数
   */
  public static short bytesToShort(byte[] bytes, boolean signed) {
    return getHelper().bytesToShort(bytes, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节
   * @param order 字节序
   * @return 返回一个整数
   */
  public static short bytesToShort(byte[] bytes, ByteOrder order) {
    return getHelper().bytesToShort(bytes, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param offset 偏移量
   * @param len    长度
   * @return 返回一个整数
   */
  public static short bytesToShort(byte[] bytes, int offset, int len) {
    return getHelper().bytesToShort(bytes, offset, len);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回一个整数
   */
  public static short bytesToShort(byte[] bytes, ByteOrder order, boolean signed) {
    return getHelper().bytesToShort(bytes, order, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节数组
   * @return 返回整数值
   */
  public static int bytesToInt(byte... bytes) {
    return getHelper().bytesToInt(bytes, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param signed 是否为有符号整数
   * @return 返回整数值
   */
  public static int bytesToInt(byte[] bytes, boolean signed) {
    return getHelper().bytesToInt(bytes, signed);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes 字节数组
   * @param order 字节序
   * @return 返回整数值
   */
  public static int bytesToInt(byte[] bytes, ByteOrder order) {
    return getHelper().bytesToInt(bytes, order, false);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param offset 偏移量
   * @param len    长度
   * @return 返回整数值
   */
  public static int bytesToInt(byte[] bytes, int offset, int len) {
    return getHelper().bytesToInt(bytes, offset, len);
  }

  /**
   * 字节数组转换成整数
   *
   * @param bytes  字节数组
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回整数值
   */
  public static int bytesToInt(byte[] bytes, ByteOrder order, boolean signed) {
    return getHelper().bytesToInt(bytes, order, signed);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @return 返回长整数值
   */
  public static long bytesToLong(byte... bytes) {
    return getHelper().bytesToLong(bytes);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes  字节数组
   * @param signed 是否为有符号整数
   * @return 返回长整数值
   */
  public static long bytesToLong(byte[] bytes, boolean signed) {
    return getHelper().bytesToLong(bytes, signed);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes 字节数组
   * @param order 字节序
   * @return 返回长整数值
   */
  public static long bytesToLong(byte[] bytes, ByteOrder order) {
    return getHelper().bytesToLong(bytes, order, false);
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
    return getHelper().bytesToLong(bytes, offset, len);
  }

  /**
   * 字节数组转换成长整数
   *
   * @param bytes  字节数组
   * @param order  字节序
   * @param signed 是否为有符号整数
   * @return 返回长整数值
   */
  public static long bytesToLong(byte[] bytes, ByteOrder order, boolean signed) {
    return getHelper().bytesToLong(bytes, order, signed);
  }

  /**
   * 整形转换成16进制
   *
   * @param num 数值
   * @return 返回16进制字符串
   */
  public static byte[] intToBytes2(int num) {
    return getHelper().hexToBytes(intToHex(num));
  }

  /**
   * 整形转换成16进制
   *
   * @param num 数值
   * @return 返回16进制字符串
   */
  public static String intToHex(int num) {
    return getHelper().intToHex(num);
  }

  /**
   * 转换成整数
   *
   * @param b 字节
   * @return 返回一个整数
   */
  public static short byteToShort(byte b) {
    return getHelper().byteToShort(b);
  }

  /**
   * 取低字节
   */
  public static int byteToIntLow(byte b) {
    return getHelper().byteToIntLow(b);
  }

  /**
   * 取高字节
   */
  public static int byteToIntHigh(byte b) {
    return getHelper().byteToIntHigh(b);
  }

  /**
   * 字节数组转换成二进制字符串
   *
   * @param bytes 字节数组
   * @return 返回二进制字符串
   */
  public static String bytesToBinary(byte... bytes) {
    return getHelper().bytesToBinary(bytes, " ", 1);
  }

  /**
   * 字节数组转换成二进制字符串
   *
   * @param bytes 字节数组
   * @param split 分隔符
   * @param len   分割的长度
   * @return 返回二进制字符串
   */
  public static String bytesToBinary(byte[] bytes, String split, int len) {
    return getHelper().bytesToBinary(bytes, split, len);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin 二进制字节数组
   * @return 返回16进制字符串或空
   */
  public static String byteToHex(byte bin) {
    return getHelper().byteToHex(bin, false);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @return 返回16进制字符串或空
   */
  public static String byteToHex(byte bin, boolean lowerCase) {
    return getHelper().byteToHex(bin, lowerCase);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin 二进制字节数组
   * @return 返回16进制字符串或空
   */
  public static String bytesToHex(byte[] bin) {
    return getHelper().bytesToHex(bin, false, null, 1);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin  二进制字节数组
   * @param fill 填充
   * @return 返回16进制字符串或空
   */
  public static String bytesToHex(byte[] bin, String fill) {
    return getHelper().bytesToHex(bin, false, fill, 1);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin    二进制字节数组
   * @param fill   填充
   * @param length 分割的长度
   * @return 返回16进制字符串或空
   */
  public static String bytesToHex(byte[] bin, String fill, int length) {
    return getHelper().bytesToHex(bin, false, fill, length);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin       二进制字节数组
   * @param lowerCase 是否为小写字母
   * @return 返回16进制字符串或空
   */
  public static String bytesToHex(byte[] bin, boolean lowerCase) {
    return getHelper().bytesToHex(bin, lowerCase, null, 1);
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
  public static String bytesToHex(byte[] bin, boolean lowerCase, final String fill, int length) {
    return getHelper().bytesToHex(bin, lowerCase, fill, length);
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
  public static String bytesToHex(byte[] bin, final String prefix, String suffix, int length) {
    return getHelper().bytesToHex(bin, false, prefix, suffix, length);
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
  public static String bytesToHex(byte[] bin, boolean lowerCase, final String prefix, String suffix, int length) {
    return getHelper().bytesToHex(bin, lowerCase, prefix, suffix, length);
  }

  /**
   * 二进制转换成16进制字符串
   *
   * @param bin      二进制字节数组
   * @param consumer 返回
   * @return 返回16进制字符串或空
   */
  public static String bytesToHex(byte[] bin, BinaryHelper.HexConsumer consumer) {
    return getHelper().bytesToHex(bin, consumer);
  }

  /**
   * 16进制字符串转换成字节数组
   *
   * @param hex 字符串
   * @return 转换的字节数组
   */
  public static byte[] hexToBytes(String hex) {
    return getHelper().hexToBytes(hex, null);
  }

  /**
   * 16进制字符串转换成字节数组
   *
   * @param hex          字符串
   * @param defaultValue 默认值
   * @return 转换的字节数组
   */
  public static byte[] hexToBytes(String hex, byte[] defaultValue) {
    return getHelper().hexToBytes(hex, defaultValue);
  }

  /**
   * 16进制字符串转换成整数值
   *
   * @param hex 16进制
   * @return 返回转换后的数据
   */
  public static int hexToInt(String hex) {
    return getHelper().hexToInt(hex);
  }

  /**
   * 16进制字符串转换成整数值
   *
   * @param hex       16进制
   * @param bigEndian 是否为大端字节顺序
   * @return 返回转换后的数据
   */
  public static int hexToInt(String hex, boolean bigEndian) {
    return getHelper().hexToInt(hex, bigEndian);
  }

  /**
   * 是否相等
   *
   * @param src   原数组
   * @param start 开始的位置
   * @param flag  标志
   * @return 返回是否相等
   */
  public static boolean isEquals(byte[] src, int start, byte flag) {
    return getHelper().isEquals(src, start, flag);
  }

  /**
   * 判断两个字节数组是否相等
   *
   * @param standard 原数组
   * @param dest     目标数组
   * @return 返回是否相等
   */
  public static boolean isEquals(byte[] standard, byte[] dest) {
    return getHelper().isEquals(standard, dest, 0);
  }

  /**
   * 判断两个字节数组是否相等
   *
   * @param standard 原数组
   * @param dest     目标数组
   * @param destPos  目标数组开始的位置
   * @return 返回是否相等
   */
  public static boolean isEquals(byte[] standard, byte[] dest, int destPos) {
    return getHelper().isEquals(standard, dest, destPos, standard.length);
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
  public static boolean isEquals(byte[] standard, byte[] dest, int destPos, int len) {
    return getHelper().isEquals(standard, 0, dest, destPos, len);
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
  public static boolean isEquals(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
    return getHelper().isEquals(src, srcPos, dest, destPos, len);
  }

  /**
   * 查找匹配的字节数组的开始位置
   *
   * @param src  原数据
   * @param find 被查找的字节
   * @return 返回找到的位置，如果未找到返回-1
   */
  public static int indexOf(byte[] src, byte[] find) {
    return getHelper().indexOf(src, find);
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
  public static int indexOf(byte[] src, int start, int len, byte[] find) {
    return getHelper().indexOf(src, start, len, find);
  }

  /**
   * 解析整数数组
   *
   * @param data   数据
   * @param start  开始的位置
   * @param len    数据占字节的长度
   * @param size   字节长度
   * @param order  字节顺序
   * @param signed 是否为有符号数
   * @return 返回解析的数组
   */
  public static short[] parseShortArray(byte[] data, int start, int len, int size, ByteOrder order, boolean signed) {
    return BinaryHelper.get(order).parseShortArray(data, start, len, size, signed);
  }

  /**
   * 解析整数数组
   *
   * @param data   数据
   * @param start  开始的位置
   * @param len    数据占字节的长度
   * @param size   字节长度
   * @param order  字节顺序
   * @param signed 是否为有符号数
   * @return 返回解析的数组
   */
  public static int[] parseIntArray(byte[] data, int start, int len, int size, ByteOrder order, boolean signed) {
    return BinaryHelper.get(order).parseIntArray(data, start, len, size, signed);
  }

  /**
   * 解析整数数组
   *
   * @param data   数据
   * @param start  开始的位置
   * @param len    数据占字节的长度
   * @param size   字节长度
   * @param order  字节顺序
   * @param signed 是否为有符号数
   * @return 返回解析的数组
   */
  public static long[] parseLongArray(byte[] data, int start, int len, int size, ByteOrder order, boolean signed) {
    return BinaryHelper.get(order).parseLongArray(data, start, len, size, signed);
  }

}
