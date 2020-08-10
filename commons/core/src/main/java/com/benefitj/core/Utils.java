package com.benefitj.core;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 常用工具
 */
public class Utils {

  public static final long KB = 1024;
  public static final long MB = 1024 << 10;
  public static final long GB = (1024 << 10) << 10;

  /**
   * 获取UUID
   */
  public static String uuid() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }

  /**
   * 获取当前时间戳
   */
  public static long now() {
    return System.currentTimeMillis();
  }

  /**
   * 获取当前时间的秒
   */
  public static long nowSecond() {
    return System.currentTimeMillis() / 1000;
  }

  /**
   * 线程Sleep
   *
   * @param time sleep时间
   * @throws IllegalStateException 被打断抛出的异常
   */
  public static void sleep(long time) throws IllegalStateException {
    sleep(time, TimeUnit.MILLISECONDS);
  }

  /**
   * 线程Sleep
   *
   * @param time sleep时间
   * @param unit 时间单位
   * @throws IllegalStateException 被打断抛出的异常
   */
  public static void sleep(long time, TimeUnit unit) throws IllegalStateException {
    try {
      unit.sleep(time);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 计算KB大小
   *
   * @param len 长度
   * @return 返回对应的KB
   */
  public static double ofKB(long len) {
    return (len * 1.0) / KB;
  }

  /**
   * 计算MB大小
   *
   * @param len 长度
   * @return 返回对应的MB
   */
  public static double ofMB(long len) {
    return (len * 1.0) / MB;
  }

  /**
   * 计算GB大小
   *
   * @param len 长度
   * @return 返回对应的GB
   */
  public static double ofGB(long len) {
    return (len * 1.0) / GB;
  }

  /**
   * 判断不为空
   *
   * @param cs 字符串
   * @return 返回判断的结果
   */
  public static boolean isNotEmpty(CharSequence cs) {
    return cs != null && cs.length() > 0;
  }

  /**
   * 判断不为空或空字符串
   *
   * @param cs 字符串
   * @return 返回判断的结果
   */
  public static boolean isNotBlank(CharSequence cs) {
    if (isNotEmpty(cs)) {
      for (int i = 0; i < cs.length(); i++) {
        if (cs.charAt(i) != ' ') {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 判断不为空
   *
   * @param c 集合
   * @return 返回判断的结果
   */
  public static <E> boolean isNotEmpty(Collection<E> c) {
    return c != null && !c.isEmpty();
  }

  /**
   * 判断不为空
   *
   * @param es 数组
   * @return 返回判断的结果
   */
  public static <E> boolean isNotEmpty(E... es) {
    return es != null && es.length > 0;
  }

}
