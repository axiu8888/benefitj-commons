package com.benefitj.core;

import com.benefitj.core.functions.MultiFilter;
import com.benefitj.core.functions.Pair;
import com.google.common.collect.Multimap;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 单位
 */
public class Utils {

  /**
   * 迭代数组
   *
   * @param array       数组
   * @param consumer    处理
   * @param obtainValue 是否获取数组的值
   */
  public static void arrayFor(Object array, BiConsumer<Integer, Object> consumer, boolean obtainValue) {
    Class<?> componentType = array.getClass().getComponentType();
    if (componentType != null && componentType.isPrimitive()) {
      if (componentType == byte.class) {
        byte[] tmp = ((byte[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == short.class) {
        short[] tmp = ((short[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == int.class) {
        int[] tmp = ((int[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == long.class) {
        long[] tmp = ((long[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == float.class) {
        float[] tmp = ((float[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == double.class) {
        double[] tmp = ((double[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == boolean.class) {
        boolean[] tmp = ((boolean[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      } else if (componentType == char.class) {
        char[] tmp = ((char[]) array);
        for (int i = 0; i < tmp.length; i++) {
          consumer.accept(i, obtainValue ? tmp[i] : null);
        }
      }
    } else {
      int length = Array.getLength(array);
      Object value;
      for (int i = 0; i < length; i++) {
        value = obtainValue ? Array.get(array, i) : null;
        consumer.accept(i, value);
      }
    }
  }

  /**
   * 转换成List
   *
   * @param itr 可迭代的对象
   * @param <T> 类型
   * @return 返回List
   */
  public static <T> List<T> toList(Iterable<T> itr) {
    return toList(itr, new ArrayList<>());
  }

  /**
   * 转换成List
   *
   * @param itr  可迭代的对象
   * @param list List
   * @param <T>  类型
   * @return 返回List
   */
  public static <T> List<T> toList(Iterable<T> itr, List<T> list) {
    return toList(itr.iterator(), list);
  }

  /**
   * 转换成List
   *
   * @param itr  可迭代的对象
   * @param list List
   * @param <T>  类型
   * @return 返回List
   */
  public static <T> List<T> toList(Iterator<T> itr, List<T> list) {
    while (itr.hasNext()) {
      list.add(itr.next());
    }
    return list;
  }

  /**
   * 转换成Map
   *
   * @param pairs 数据对
   * @return 返回Map
   */
  public static <K, V> Map<K, V> mapOf(Pair<K, V>... pairs) {
    return mapOf(new LinkedHashMap<>(), pairs);
  }

  /**
   * 转换成Map
   *
   * @param map   Map
   * @param pairs 数据对
   * @return 返回Map
   */
  public static <K, V> Map<K, V> mapOf(Map<K, V> map, Pair<K, V>... pairs) {
    for (Pair<K, V> pair : pairs) {
      map.put(pair.getKey(), pair.getValue());
    }
    return map;
  }

  /**
   * 将Enumeration转换为Map
   *
   * @param e    Enumeration
   * @param func 处理函数
   * @param <K>  键
   * @param <V>  值
   * @return 返回 Map
   */
  public static <K, V> Map<K, V> toMap(Enumeration<K> e, Function<K, V> func) {
    return toMap(new LinkedHashMap<>(), e, func);
  }

  /**
   * 将Enumeration转换为Map
   *
   * @param map  Map
   * @param e    Enumeration
   * @param func 处理函数
   * @param <K>  键
   * @param <V>  值
   * @return 返回 Map
   */
  public static <K, V> Map<K, V> toMap(Map<K, V> map, Enumeration<K> e, Function<K, V> func) {
    K k;
    V v;
    while (e.hasMoreElements()) {
      k = e.nextElement();
      v = func.apply(k);
      map.put(k, v);
    }
    return map;
  }

  /**
   * 转换成Map
   *
   * @param multimap Multimap
   * @param <K>      键
   * @param <V>值
   * @return 返回转换后的Map
   */
  public static <K, V> Map<K, V> toMap(Multimap<K, V> multimap) {
    return toMap(multimap, new LinkedHashMap<>());
  }

  /**
   * 转换成Map
   *
   * @param multimap Multimap
   * @param map      Map
   * @param <K>      键
   * @param <V>值
   * @return 返回转换后的Map
   */
  public static <K, V> Map<K, V> toMap(Multimap<K, V> multimap, Map<K, V> map) {
    return toMap(multimap, map, (k, v) -> v, (mp, k, v) -> true);
  }

  /**
   * 转换成Map
   *
   * @param multimap   Multimap
   * @param mappedFunc 转换器
   * @param <K>        键
   * @param <V>值
   * @return 返回转换后的Map
   */
  public static <K, V> Map<K, V> toMap(Multimap<K, V> multimap, BiFunction<K, V, V> mappedFunc) {
    return toMap(multimap, new LinkedHashMap<>(), mappedFunc, (map, k, v) -> true);
  }

  /**
   * 转换成Map
   *
   * @param multimap   Multimap
   * @param map        Map
   * @param mappedFunc 转换器
   * @param filter     过滤器
   * @param <K>        键
   * @param <V>值
   * @return 返回转换后的Map
   */
  public static <K, V> Map<K, V> toMap(Multimap<K, V> multimap,
                                       Map<K, V> map,
                                       BiFunction<K, V, V> mappedFunc,
                                       MultiFilter<Map<K, V>, K, V> filter) {
    multimap.forEach((k, v) -> {
      if (filter.test(map, k, v)) {
        map.put(k, mappedFunc.apply(k, v));
      }
    });
    return map;
  }

  /**
   * 获取后缀
   *
   * @param filename 文件名
   * @return 返回后缀
   */
  public static String getFileSuffix(String filename) {
    return getSuffix(filename, ".");
  }

  /**
   * 获取后缀
   *
   * @param str 字符串
   * @return 返回后缀
   */
  public static String getSuffix(String str, String symbol) {
    int index = str.lastIndexOf(symbol);
    return index > 0 ? str.substring(index) : "";
  }

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
   * 以某个连接符串拼接两个字符串
   *
   * @param prefix 前缀
   * @param suffix 后缀
   * @param joint  分隔符
   * @return 返回拼接的字符串
   */
  public static String joint(String prefix, String suffix, String joint) {
    prefix = endWiths(prefix, joint);
    suffix = isStartWiths(suffix, joint) ? suffix.substring(joint.length()) : suffix;
    return prefix + joint + suffix;
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

  /**
   * 保留小数点后几位
   *
   * @param v       值
   * @param bitSize 位数
   * @return 返回保留的数
   */
  public static double decimal(double v, int bitSize) {
    return decimal(v, bitSize, RoundingMode.UP);
  }

  /**
   * 保留小数点后几位
   *
   * @param v            值
   * @param bitSize      位数
   * @param roundingMode 取整模式
   * @return 返回保留的数
   */
  public static double decimal(double v, int bitSize, RoundingMode roundingMode) {
    return BigDecimal.valueOf(v).setScale(bitSize, roundingMode).doubleValue();
  }

}
