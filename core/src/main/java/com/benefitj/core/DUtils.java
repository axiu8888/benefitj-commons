package com.benefitj.core;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 单位
 */
public class DUtils {


  /**
   * 是否以检查的前缀开头
   *
   * @param str    字符串
   * @param prefix 前缀
   * @return 返回匹配结果
   */
  public static boolean isStartWiths(String str, String prefix) {
    return str != null && str.startsWith(prefix);
  }

  /**
   * 以要求的前缀结尾
   *
   * @param str    字符串
   * @param prefix 前缀
   * @return 返回拼接的字符串
   */
  public static String startWiths(String str, String prefix) {
    if (str != null) {
      if (!isStartWiths(str, prefix)) {
        return (prefix != null ? prefix : "") + str;
      }
      return str;
    }
    return prefix;
  }

  /**
   * 是否以检查的后缀开头
   *
   * @param str    字符串
   * @param suffix 后缀
   * @return 返回匹配结果
   */
  public static boolean isEndWiths(String str, String suffix) {
    return str != null && str.endsWith(suffix);
  }

  /**
   * 以要求的后缀结尾
   *
   * @param str    字符串
   * @param suffix 后缀
   * @return 返回拼接的字符串
   */
  public static String endWiths(String str, String suffix) {
    if (str != null) {
      if (!isEndWiths(str, suffix)) {
        return str + (suffix != null ? suffix : "");
      }
      return str;
    }
    return suffix;
  }

  /**
   * 以要求的前缀结尾
   *
   * @param str    字符串
   * @param prefix 前缀
   * @param suffix 后缀
   * @return 返回拼接的字符串
   */
  public static String withs(String str, String prefix, String suffix) {
    str = startWiths(str, prefix);
    str = endWiths(str, suffix);
    return str;
  }

  /**
   * 分割列表
   *
   * @param list    列表
   * @param subSize 子列表的大小
   * @param <T>     列表的数据
   * @return 返回分割后的列表
   */
  public static <T> List<List<T>> subList(List<T> list, int subSize) {
    List<List<T>> subList = new ArrayList<>((list.size() / subSize) + 1);
    for (int i = 0, j; i < list.size(); ) {
      j = Math.min(list.size() - i, subSize);
      subList.add(list.subList(i, i + j));
      i += j;
    }
    return subList;
  }

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

  /**
   * 秒(毫秒)
   *
   * @param delta 秒数
   * @return 返回时间
   */
  public static long ofSeconds(int delta) {
    return TimeUnit.SECONDS.toMillis(delta);
  }

  /**
   * 分钟(毫秒)
   *
   * @param delta 分钟数
   * @return 返回时间
   */
  public static long ofMinutes(int delta) {
    return TimeUnit.MINUTES.toMillis(delta);
  }

  /**
   * 小时(毫秒)
   *
   * @param delta 小时数
   * @return 返回时间
   */
  public static long ofHours(int delta) {
    return TimeUnit.HOURS.toMillis(delta);
  }

  /**
   * 天(毫秒)
   *
   * @param delta 天数
   * @return 返回时间
   */
  public static long ofDays(int delta) {
    return TimeUnit.DAYS.toMillis(delta);
  }

  /**
   * 获取当前时间
   */
  public static long now() {
    return System.currentTimeMillis();
  }

  /**
   * 和当前时间的差值
   *
   * @param delta 时间
   * @return 返回与当前时间的差
   */
  public static long diffNow(long delta) {
    return now() - delta;
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

  /**
   * 格式化 KB
   *
   * @param v       数值
   * @param pattern 格式: 0.00，保留小数点后2位
   * @return 返回格式化的结果
   */
  public static String fmtKB(double v, String pattern) {
    return fmt(v / KB, pattern);
  }

  /**
   * 格式化 MB
   *
   * @param v       数值
   * @param pattern 格式: 0.00，保留小数点后2位
   * @return 返回格式化的结果
   */
  public static String fmtMB(double v, String pattern) {
    return fmt(v / MB, pattern);
  }

  /**
   * 格式化 GB
   *
   * @param v       数值
   * @param pattern 格式: 0.00，保留小数点后2位
   * @return 返回格式化的结果
   */
  public static String fmtGB(double v, String pattern) {
    return fmt(v / GB, pattern);
  }

  /**
   * 格式化
   *
   * @param v       数值
   * @param pattern 格式: 0.00，保留小数点后2位
   * @return 返回格式化的结果
   */
  public static String fmt(double v, String pattern) {
    return new DecimalFormat(pattern).format(v);
  }

}
