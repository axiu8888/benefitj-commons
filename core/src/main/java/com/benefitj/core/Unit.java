package com.benefitj.core;

/**
 * 单位
 */
public class Unit {

  /**
   * 计算出新的小数位
   *
   * @param v   值
   * @param num 小数位
   * @return 返回新的值
   */
  public static double digits(double v, int num) {
    if (num < 0) {
      return v;
    }
    if (num == 0) {
      return (long) v;
    }
    long dividend = 10;
    for (int i = 1; i < num; i++) {
      dividend *= 10;
    }
    return Math.round(v * dividend) * 1.0 / dividend;
  }


  public static final long KB = 1024L;
  public static final long MB = 1024L * KB;
  public static final long GB = 1024L * MB;
  public static final long TB = 1024L * GB;

  /**
   * 计算KB大小
   *
   * @param len 长度
   * @return 返回对应的KB
   */
  public static double ofKB(long len) {
    return ofKB(len, -1);
  }

  /**
   * 计算KB大小
   *
   * @param len    长度
   * @param digits 小数位
   * @return 返回对应的KB
   */
  public static double ofKB(long len, int digits) {
    return ofSize(len, digits, KB);
  }

  /**
   * 计算MB大小
   *
   * @param len 长度
   * @return 返回对应的MB
   */
  public static double ofMB(long len) {
    return ofMB(len, -1);
  }

  /**
   * 计算MB大小
   *
   * @param len    长度
   * @param digits 小数位
   * @return 返回对应的MB
   */
  public static double ofMB(long len, int digits) {
    return ofSize(len, digits, MB);
  }

  /**
   * 计算GB大小
   *
   * @param len 长度
   * @return 返回对应的GB
   */
  public static double ofGB(long len) {
    return ofGB(len, -1);
  }

  /**
   * 计算GB大小
   *
   * @param len    长度
   * @param digits 小数位
   * @return 返回对应的GB
   */
  public static double ofGB(long len, int digits) {
    return ofSize(len, digits, GB);
  }

  /**
   * 计算TB大小
   *
   * @param len 长度
   * @return 返回对应的TB
   */
  public static double ofTB(long len) {
    return ofTB(len, -1);
  }

  /**
   * 计算TB大小
   *
   * @param len    长度
   * @param digits 小数位
   * @return 返回对应的TB
   */
  public static double ofTB(long len, int digits) {
    return ofSize(len, digits, TB);
  }

  private static double ofSize(long len, int digits, long unit) {
    return digits((len * 1.0) / unit, digits);
  }

}
