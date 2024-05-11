package com.benefitj.core;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 日期格式化
 */
public class DateFmtter {
  /**
   * UTC
   */
  public static final String _yMd_UTC = "yyyy-MM-dd";
  public static final String _yMdHm_UTC = "yyyy-MM-dd'T'HH:mm'Z'";
  public static final String _yMdHms_UTC = "yyyy-MM-dd'T'HH:mm:ss'Z'";
  public static final String _yMdHmsS_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
  public static final String _yMdHmsX_UTC = "yyyy-MM-dd'T'HH:mm:ssXXX";
  public static final String _yMdHmsSX_UTC = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'XXX";
  /**
   * 横线
   */
  public static final String _yMd = "yyyy-MM-dd";
  public static final String _ms = "mm:ss";
  public static final String _Hms = "HH:mm:ss";
  public static final String _yMdHm = "yyyy-MM-dd HH:mm";
  public static final String _yMdHms = "yyyy-MM-dd HH:mm:ss";
  public static final String _yMdHmsS = "yyyy-MM-dd HH:mm:ss.SSS";
  /**
   * 反斜杠
   */
  public static final String _yMd_B = "yyyy/MM/dd";
  public static final String _ms_CN_B = "mm分ss秒";
  public static final String _Hms_CN_B = "HH时mm分ss秒";
  public static final String _yMdHm_B = "yyyy/MM/dd HH:mm";
  public static final String _yMdHms_B = "yyyy/MM/dd HH:mm:ss";
  public static final String _yMdHmsS_B = "yyyy/MM/dd HH:mm:ss.SSS";
  /**
   * 没有空格
   */
  public static final String _yMd_NO = "yyyyMMdd";
  public static final String _ms_NO = "mm:ss";
  public static final String _HmsS = "HH:mm:ss.SSS";
  public static final String _yMdHm_NO = "yyyyMMddHHmm";
  public static final String _yMdHms_NO = "yyyyMMddHHmmss";
  public static final String _yMdHmsS_NO = "yyyyMMddHHmmssSSS";
  /**
   * 中文
   */
  public static final String _yMd_CN = "yyyy年MM月dd";
  public static final String _ms_CN = "mm分ss秒";
  public static final String _HmsS_CN = "HH时mm分ss秒.SSS";
  public static final String _yMdHm_CN = "yyyy年MM月dd HH时mm分";
  public static final String _yMdHms_CN = "yyyy年MM月dd HH时mm分ss秒";
  public static final String _yMdHmsS_CN = "yyyy年MM月dd HH时mm分ss秒.SSS";

  public static final long SECOND = 1000;
  public static final long MINUTE = 60 * SECOND;
  public static final long HOUR = 60 * MINUTE;
  public static final long DAY = 24 * HOUR;

  /**
   * 缓存时间格式化
   */
  private static final Map<Object, ThreadLocal<Map<String, SimpleDateFormat>>> CACHED_LOCAL = new ConcurrentHashMap<>();
  private static final Function<Object, ThreadLocal<Map<String, SimpleDateFormat>>> CREATOR = o -> ThreadLocal.withInitial(WeakHashMap::new);
  /**
   * 默认的Locale
   */
  private static volatile Locale DEFAULT_LOCALE = Locale.getDefault();
  private static volatile String DEFAULT_KEY = DEFAULT_LOCALE.toString();

  private static final Map<Object, String> KEY_CACHE = new WeakHashMap<>();
  private static final Function<Object, String> KEY_CREATOR = Object::toString;

  private DateFmtter() {
  }

  /**
   * 获取当前的时区偏移量
   */
  public static ZoneOffset currentZoneOffset() {
    return ZoneId.systemDefault().getRules().getOffset(Instant.now());
  }

  public static void setDefaultLocale(Locale defaultLocale) {
    DateFmtter.DEFAULT_LOCALE = defaultLocale;
    DateFmtter.DEFAULT_KEY = defaultLocale.toString();
  }

  private static Map<String, SimpleDateFormat> getMap(String key) {
    return CACHED_LOCAL.computeIfAbsent(key, CREATOR).get();
  }

  /**
   * 获取SimpleDateFormat
   */
  public static SimpleDateFormat getSdf(Locale locale, String pattern) {
    final Map<String, SimpleDateFormat> map = getMap(KEY_CACHE.computeIfAbsent(locale, KEY_CREATOR));
    SimpleDateFormat sdf = map.get(pattern);
    if (sdf == null) {
      map.put(pattern, sdf = new SimpleDateFormat(pattern, locale));
    }
    return sdf;
  }

  /**
   * 获取SimpleDateFormat
   */
  public static SimpleDateFormat getSdf(TimeZone timeZone, String pattern) {
    final Map<String, SimpleDateFormat> map = getMap(timeZone.getID());
    SimpleDateFormat sdf = map.get(pattern);
    if (sdf == null) {
      map.put(pattern, sdf = new SimpleDateFormat(pattern, DEFAULT_LOCALE));
      sdf.setTimeZone(timeZone);
    }
    return sdf;
  }

  /**
   * 获取默认的SimpleDateFormat
   */
  public static SimpleDateFormat getDefaultSdf(String pattern) {
    return getSdf(Locale.getDefault(), pattern);
  }

  /**
   * 获取UTC的SimpleDateFormat
   */
  public static SimpleDateFormat getUtcSdf(String pattern) {
    return getSdf(TimeZone.getTimeZone("UTC"), pattern);
  }

  /**
   * @return 当前时间
   */
  public static long now() {
    return System.currentTimeMillis();
  }

  /**
   * 解析字符串类型的时间
   *
   * @param time 时间
   * @param sdf  格式解析
   * @return 返回解析后的Date对象
   * @throws ParseException 解析异常
   */
  private static Date parseOrThrows(String time, SimpleDateFormat sdf) throws ParseException {
    if (time == null) {
      throw new ParseException("time is null", 0);
    }
    return sdf.parse(time);
  }

  /**
   * 解析字符串类型的时间
   *
   * @param time    时间
   * @param pattern 格式
   * @return 返回解析后的Date对象
   * @throws ParseException 解析异常
   */
  public static Date parseOrThrows(String time, String pattern) throws ParseException {
    return parseOrThrows(time, getDefaultSdf(pattern));
  }

  /**
   * 解析字符串类型的时间， yyyy-MM-dd HH:mm:ss
   *
   * @param time 时间
   * @return 返回解析后的Date对象
   */
  private static Date parse(String time) {
    return parse(time, _yMdHms);
  }

  /**
   * 解析字符串类型的时间， yyyy-MM-dd HH:mm:ss.SSS
   *
   * @param time 时间
   * @return 返回解析后的Date对象
   */
  private static Date parseS(String time) {
    return parse(time, _yMdHmsS);
  }

  /**
   * 解析字符串类型的时间
   *
   * @param time 时间
   * @param sdf  格式化对象
   * @return 返回解析后的Date对象
   */
  private static Date parse(String time, SimpleDateFormat sdf) {
    try {
      return parseOrThrows(time, sdf);
    } catch (ParseException e) {
      return null;
    }
  }

  /**
   * 解析字符串类型的时间
   *
   * @param time    时间
   * @param pattern 格式
   * @return 返回解析后的Date对象
   */
  public static Date parse(String time, String pattern) {
    return parse(time, getDefaultSdf(pattern));
  }

  /**
   * 解析字符串类型的时间: yyyy-MM-dd
   *
   * @param time    时间
   * @return 返回解析后的Date对象
   */
  public static Date parseDate(String time) {
    return parse(time, _yMd);
  }

  /**
   * 解析字符串类型的时间， yyyy-MM-dd HH:mm:ss
   *
   * @param time 时间
   * @return 返回解析后的Date对象
   */
  public static long parseToLong(String time) {
    return parseToLong(time, _yMdHms);
  }

  /**
   * 解析字符串类型的时间， yyyy-MM-dd HH:mm:ss.SSS
   *
   * @param time 时间
   * @return 返回解析后的Date对象
   */
  public static long parseToLongS(String time) {
    return parseToLong(time, _yMdHmsS);
  }

  /**
   * 获取long
   *
   * @param time    字符串时间
   * @param pattern 格式
   * @return 返回时间，如果格式有问题，返回 0
   */
  public static long parseToLong(String time, String pattern) {
    return parseToLong(time, pattern, 0L);
  }

  /**
   * 解析字符串类型的时间
   *
   * @param time    时间
   * @param pattern 格式
   * @return 返回解析后的Date对象
   */
  public static Date parseUtc(String time, String pattern) {
    return parse(time, getUtcSdf(pattern));
  }

  /**
   * 解析字符串类型的时间， yyyy-MM-dd HH:mm:ss
   *
   * @param time 时间
   * @return 返回解析后的Date对象
   */
  public static long parseUtcToLong(String time) {
    return parseUtcToLong(time, _yMdHms_UTC);
  }

  /**
   * 解析字符串类型的时间， yyyy-MM-dd HH:mm:ss.SSS
   *
   * @param time 时间
   * @return 返回解析后的Date对象
   */
  public static long parseUtcToLongS(String time) {
    return parseUtcToLong(time, _yMdHmsS_UTC);
  }

  /**
   * 获取long
   *
   * @param time    字符串时间
   * @param pattern 格式
   * @return 返回时间，如果格式有问题，返回 0
   */
  public static long parseUtcToLong(String time, String pattern) {
    return parseToLong(time, pattern, 0L, true);
  }

  /**
   * 获取long
   *
   * @param time         字符串时间
   * @param pattern      格式
   * @param defaultValue 默认值
   * @return 返回时间，如果格式有问题，返回默认值
   */
  public static long parseToLong(String time, String pattern, long defaultValue) {
    return parseToLong(time, pattern, defaultValue, false);
  }

  /**
   * 获取long
   *
   * @param time         字符串时间
   * @param pattern      格式
   * @param defaultValue 默认值
   * @param utc          是否为UTC格式
   * @return 返回时间，如果格式有问题，返回默认值
   */
  public static long parseToLong(String time, String pattern, long defaultValue, boolean utc) {
    Date date = utc ? parseUtc(time, pattern) : parse(time, pattern);
    return date != null ? date.getTime() : defaultValue;
  }

  /**
   * 格式化日期，{@link #_yMdHms}
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmt(Object time) {
    return fmt(time, _yMdHms);
  }

  /**
   * 格式化日期，{@link #_yMdHmsS}
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmtS(Object time) {
    return fmt(time, _yMdHmsS);
  }

  /**
   * 格式化日期，{@link #_yMdHms_UTC}
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmtUtc(Object time) {
    return fmtUtc(time, _yMdHms_UTC);
  }

  /**
   * 格式化日期，{@link #_yMdHmsS_UTC}
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmtUtcS(Object time) {
    return fmtUtc(time, _yMdHmsS_UTC);
  }

  /**
   * 格式化日期， yyyy-MM-dd HH:mm:ss
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmt(Object time, String pattern) {
    return getDefaultSdf(pattern).format(time);
  }

  /**
   * 格式化日期， yyyy-MM-dd HH:mm:ss
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmtUtc(Object time, String pattern) {
    return getUtcSdf(pattern).format(time);
  }

  /**
   * 格式化当前时间
   *
   * @param pattern 格式
   * @return 返回格式化好的时间字符串
   */
  public static String fmtNow(String pattern) {
    return fmt(now(), pattern);
  }

  /**
   * 格式化当前时间
   *
   * @param pattern 格式
   * @return 返回格式化好的时间字符串
   */
  public static String fmtNowUtc(String pattern) {
    return fmtUtc(now(), pattern);
  }

  /**
   * 格式化当前时间, {@link #_yMdHms}
   *
   * @return 返回格式化好的时间字符串
   */
  public static String fmtNow() {
    return fmtNow(_yMdHms);
  }

  /**
   * 格式化当前时间, {@link #_yMdHmsS}
   *
   * @return 返回格式化好的时间字符串
   */
  public static String fmtNowS() {
    return fmtNow(_yMdHmsS);
  }

  /**
   * 格式化当前时间, {@link #_yMdHms_UTC}
   *
   * @return 返回格式化好的时间字符串
   */
  public static String fmtNowUtc() {
    return fmtNowUtc(_yMdHms_UTC);
  }

  /**
   * 格式化当前时间, {@link #_yMdHmsS_UTC}
   *
   * @return 返回格式化好的时间字符串
   */
  public static String fmtNowUtcS() {
    return fmtNowUtc(_yMdHmsS_UTC);
  }

  /**
   * 格式化当前时间
   *
   * @return 返回格式化好的时间字符串
   */
  public static String fmtToday() {
    return fmtNow(_yMd);
  }

  /**
   * 格式化日期，{@link #_yMd}
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmtDate(Object time) {
    return fmt(time, _yMd);
  }

  /**
   * 格式化日期，{@link #_yMd_UTC}
   *
   * @param time 时间
   * @return 返回格式化后的对象
   */
  public static String fmtDateUtc(Object time) {
    return fmt(time, _yMd_UTC);
  }

  /**
   * 转换为另一种格式
   *
   * @param dateStr     时间戳字符串
   * @param srcPattern  原格式
   * @param destPattern 目标格式
   * @return 返回转换后的数据
   */
  public static String convertTo(String dateStr, String srcPattern, String destPattern) {
    try {
      Date date = parseOrThrows(dateStr, srcPattern);
      return fmt(date, destPattern);
    } catch (ParseException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  public static boolean isOffsetDate(String date) {

    try {
      OffsetDateTime.parse(date, DateTimeFormatter.ofPattern(_yMdHmsX_UTC));
      return true;
    } catch (Exception ignored) {}
    try {
      OffsetDateTime.parse(date, DateTimeFormatter.ofPattern(_yMdHmsSX_UTC));
      return true;
    } catch (Exception ignored) {}
    return false;
  }

  public static OffsetDateTime parseOffset(String date) {
    return parseOffset(date, _yMdHmsX_UTC);
  }

  public static OffsetDateTime parseOffsetS(String date) {
    return parseOffset(date, _yMdHmsSX_UTC);
  }

  public static OffsetDateTime parseOffset(String date, String pattern) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
    return OffsetDateTime.parse(date, formatter);
  }

}
