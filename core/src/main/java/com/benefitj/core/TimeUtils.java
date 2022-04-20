package com.benefitj.core;

import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 时间工具
 */
public class TimeUtils {

  public static final long SECOND = 1000;
  public static final long MINUTE = 60 * SECOND;
  public static final long HOUR = 60 * MINUTE;
  public static final long DAY = 24 * HOUR;

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

  /**
   * 判断是否超时
   *
   * @param start   开始时间
   * @param timeout 超时时长
   * @return 返回是否超时的判断
   */
  public static boolean isTimeout(long start, long timeout) {
    return now() - start > timeout;
  }

  /**
   * 获取某几天前的具体时间
   *
   * @param delta 时间
   * @param suffix 后缀
   * @return 返回具体时间
   */
  public static long getBeforeSpecialDay(long delta, String suffix) {
    return getSpecialDay(Math.abs(delta), suffix);
  }

  /**
   * 获取某几天后的具体时间
   *
   * @param delta 时间
   * @param suffix 后缀
   * @return 返回具体时间
   */
  public static long getAfterSpecialDay(long delta, String suffix) {
    return getSpecialDay(-Math.abs(delta), suffix);
  }

  /**
   * 获取某天的具体时间
   *
   * @param delta 时间
   * @param suffix 后缀
   * @return 返回具体时间
   */
  public static long getSpecialDay(long delta, String suffix) {
    String day = DateFmtter.fmtDate(now() + delta);
    return DateFmtter.parseToLong(day + (StringUtils.isNotBlank(suffix) ? (" " + suffix) : ""));
  }

  /**
   * 获取之前的时间
   *
   * @param delta 时间
   * @param unit 时间单位
   * @return 返回
   */
  public static long getBefore(long delta, TimeUnit unit) {
    return now() - unit.toMillis(delta);
  }

  /**
   * 获取之后的时间
   *
   * @param delta 时间
   * @param unit 时间单位
   * @return 返回
   */
  public static long getAfter(long delta, TimeUnit unit) {
    return now() + unit.toMillis(delta);
  }

  /**
   * 获取Calendar
   *
   * @param year   年
   * @param month  月 ==> 1~12
   * @param day    日
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @param millis 毫秒
   * @return 返回时间
   */
  public static Calendar newCalendar(
      int year, int month, int day, int hour, int minute, int second, int millis) {
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month - 1);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, second);
    calendar.set(Calendar.MILLISECOND, millis);
    return calendar;
  }

  /**
   * 获取Calendar
   *
   * @param date 日期
   * @return 返回时间
   */
  public static Calendar newCalendar(Date date) {
    return newCalendar(date.getTime());
  }

  /**
   * 获取Calendar
   *
   * @param date 日期
   * @return 返回时间
   */
  public static Calendar newCalendar(long date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(date);
    return calendar;
  }

  /**
   * 获取日期
   *
   * @param year  年
   * @param month 月 ==> 1~12
   * @param day   日
   * @return 返回日期
   */
  public static Date toDate(int year, int month, int day) {
    return toDate(year, month, day, 0, 0, 0, 0);
  }

  /**
   * 获取日期
   *
   * @param year   年
   * @param month  月 ==> 1~12
   * @param day    日
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @return 返回日期
   */
  public static Date toDate(int year, int month, int day, int hour, int minute, int second) {
    return toDate(year, month, day, hour, minute, second, 0);
  }

  /**
   * 获取日期
   *
   * @param year   年
   * @param month  月 ==> 1~12
   * @param day    日
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @param millis 毫秒
   * @return 返回日期
   */
  public static Date toDate(int year, int month, int day, int hour, int minute, int second, int millis) {
    return newCalendar(year, month, day, hour, minute, second, millis).getTime();
  }

  /**
   * 获取时间
   *
   * @param year  年
   * @param month 月 ==> 1~12
   * @param day   日
   * @return 返回时间
   */
  public static long toTime(int year, int month, int day) {
    return toTime(year, month, day, 0, 0, 0, 0);
  }

  /**
   * 获取时间
   *
   * @param year   年
   * @param month  月 ==> 1~12
   * @param day    日
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @return 返回时间
   */
  public static long toTime(int year, int month, int day, int hour, int minute, int second) {
    return toTime(year, month, day, hour, minute, second, 0);
  }

  /**
   * 获取时间
   *
   * @param year   年
   * @param month  月 ==> 1~12
   * @param day    日
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @param millis 毫秒
   * @return 返回时间
   */
  public static long toTime(int year, int month, int day, int hour, int minute, int second, int millis) {
    return toDate(year, month, day, hour, minute, second, millis).getTime();
  }

  /**
   * 获取今天的时间
   *
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @return 返回时间戳
   */
  public static long getToday(int hour, int minute, int second) {
    Calendar c = Calendar.getInstance();
    c.setTime(new Date());
    return toTime(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), hour, minute, second);
  }

  /**
   * 获取昨天的时间
   *
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @return 返回时间戳
   */
  public static long getYesterday(int hour, int minute, int second) {
    return getToday(hour, minute, second) - DAY;
  }

  /**
   * 获取明天的时间
   *
   * @param hour   时
   * @param minute 分
   * @param second 秒
   * @return 返回时间戳
   */
  public static long getTomorrow(int hour, int minute, int second) {
    return getToday(hour, minute, second) + DAY;
  }

  /**
   * 获取年龄
   *
   * @param birthday 出生日期
   * @return 返回年龄
   */
  public static int getAge(Date birthday) {
    Calendar cal = Calendar.getInstance();
    if (cal.before(birthday)) {
      // 出生日期晚于当前时间，无法计算
      throw new IllegalArgumentException("The birthday is before Now.It's unbelievable!");
    }
    int yearNow = cal.get(Calendar.YEAR);
    int monthNow = cal.get(Calendar.MONTH);
    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
    cal.setTime(birthday);
    int yearBirthday = cal.get(Calendar.YEAR);
    int monthBirthday = cal.get(Calendar.MONTH);
    int dayOfMonthBirthday = cal.get(Calendar.DAY_OF_MONTH);
    int age = yearNow - yearBirthday;   // 计算整岁数
    if (monthNow <= monthBirthday) {
      if (monthNow == monthBirthday) {
        if (dayOfMonthNow < dayOfMonthBirthday)
          age--; //当前日期在生日之前，年龄减一
      } else {
        age--; //当前月份在生日之前，年龄减一
      }
    }
    return age;
  }

}
