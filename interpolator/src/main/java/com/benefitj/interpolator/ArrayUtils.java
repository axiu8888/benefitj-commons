package com.benefitj.interpolator;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 数组工具
 */
public class ArrayUtils {

  public static final Map<Class<? extends Number>, Function<Number, ? extends Number>> FUNCTIONS = Collections.unmodifiableMap(new HashMap() {{
    put(Byte.class, (Function<Number, Byte>) v -> v instanceof Byte ? (Byte) v : v.byteValue());
    put(Short.class, (Function<Number, Short>) v -> v instanceof Short ? (Short) v : v.shortValue());
    put(Integer.class, (Function<Number, Integer>) v -> v instanceof Integer ? (Integer) v : v.intValue());
    put(Long.class, (Function<Number, Long>) v -> v instanceof Long ? (Long) v : v.longValue());
    put(Float.class, (Function<Number, Float>) v -> v instanceof Float ? (Float) v : v.floatValue());
    put(Double.class, (Function<Number, Double>) v -> v instanceof Double ? (Double) v : v.doubleValue());
    put(BigDecimal.class, (Function<Number, BigDecimal>) v -> v instanceof BigDecimal ? (BigDecimal) v : ofDecimal(v.doubleValue()));
  }});

  public static <T extends Number> Function<Number, T> obtainFunc(Class<T> cls) {
    return (Function<Number, T>) FUNCTIONS.get(cls);
  }

  public static <T extends Number> Function<Number, T> obtainFunc(Collection<? extends Number> c) {
    for (Number v : c) {
      return (Function<Number, T>) obtainFunc(v.getClass());
    }
    return null;
  }

  public static final BiFunction<BigDecimal, Number, BigDecimal> SUM = (v1, v2) -> v1.add(ofDecimal(obtainFunc(Double.class).apply(v2)));
  public static final BiFunction<BigDecimal, Number, BigDecimal> MAX = (v1, v2) -> ofDecimal(Math.max(obtainFunc(Double.class).apply(v2), obtainFunc(Double.class).apply(v1)));
  public static final BiFunction<BigDecimal, Number, BigDecimal> MIN = (v1, v2) -> ofDecimal(Math.min(obtainFunc(Double.class).apply(v2), obtainFunc(Double.class).apply(v1)));

  /**
   * 数组转换
   *
   * @param array    数组
   * @param consumer 处理函数
   */
  public static <T> void arrayTo(Object array, Consumer<T> consumer) {
    if (!array.getClass().isArray()) {
      throw new IllegalStateException("仅支持数组类型!");
    }

    int length = Array.getLength(array);
    for (int i = 0; i < length; i++) {
      consumer.accept((T) Array.get(array, i));
    }
  }

  /**
   * 数组转换成列表
   *
   * @param array          数组
   * @param mappedFunction 转换函数
   * @param <R>            类型
   * @return 返回列表
   */
  public static <T, R> List<R> arrayToList(Object array, Function<T, R> mappedFunction) {
    List<R> list = new ArrayList<>(Array.getLength(array));
    arrayTo(array, (Consumer<T>) element -> list.add(mappedFunction.apply(element)));
    return list;
  }

  /**
   * 数组转换成Short列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Short> arrayToShort(Object array) {
    return arrayToList(array, obtainFunc(Short.class));
  }

  /**
   * 数组转换成Integer列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Integer> arrayToInteger(Object array) {
    return arrayToList(array, obtainFunc(Integer.class));
  }

  /**
   * 数组转换成Long列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Long> arrayToLong(Object array) {
    return arrayToList(array, obtainFunc(Long.class));
  }

  /**
   * 数组转换成Float列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Float> arrayToFloat(Object array) {
    return arrayToList(array, obtainFunc(Float.class));
  }

  /**
   * 数组转换成Double列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Double> arrayToDouble(Object array) {
    return arrayToList(array, obtainFunc(Double.class));
  }

  /**
   * 计算
   *
   * @param c 集合
   * @return 返回计算的值
   */
  public static boolean isFloatNumber(Collection<? extends Number> c) {
    for (Number v : c) {
      if (v instanceof Float || v instanceof Double || v instanceof BigDecimal) {
        return true;
      }
    }
    return false;
  }

  /**
   * 计算
   *
   * @param c 集合
   * @return 返回计算的值
   */
  public static BigDecimal calculate(Collection<? extends Number> c,
                                     BiFunction<BigDecimal, Number, BigDecimal> handler) {
    BigDecimal result = ofDecimal(0.0);
    for (Number v : c) {
      result = handler.apply(result, v);
    }
    return result;
  }

  /**
   * 计算总和
   *
   * @param c 集合
   * @return 返回总和
   */
  public static BigDecimal sum(Collection<? extends Number> c) {
    return calculate(c, SUM);
  }

  /**
   * 计算平均值
   *
   * @param c 集合
   * @return 返回平均值
   */
  public static BigDecimal max(Collection<? extends Number> c) {
    return calculate(c, MAX);
  }

  /**
   * 计算平均值
   *
   * @param c 集合
   * @return 返回平均值
   */
  public static BigDecimal min(Collection<? extends Number> c) {
    return calculate(c, MIN);
  }

  /**
   * 计算平均值
   *
   * @param c 集合
   * @return 返回平均值
   */
  public static BigDecimal mean(Collection<? extends Number> c) {
    BigDecimal sum = sum(c);
    return sum.divide(ofDecimal(c.size()), RoundingMode.CEILING);
  }

  static final Map<Number, BigDecimal> CACHE_BIG_DECIMAL = new WeakHashMap<>();
  static final Function<Number, BigDecimal> CACHE_FUNC = o -> {
    if (o instanceof Integer) {
      return BigDecimal.valueOf(o.intValue());
    }
    if (o instanceof Long) {
      return BigDecimal.valueOf(o.longValue());
    }
    if (o instanceof Double) {
      return BigDecimal.valueOf(o.doubleValue());
    }
    if (o instanceof Float) {
      return BigDecimal.valueOf(o.floatValue());
    }
    if (o instanceof Short) {
      return BigDecimal.valueOf(o.shortValue());
    }
    if (o instanceof Byte) {
      return BigDecimal.valueOf(o.byteValue());
    }
    if (o instanceof BigDecimal) {
      return (BigDecimal) o;
    }
    return BigDecimal.valueOf(o.doubleValue());
  };

  public static BigDecimal ofDecimal(Number n) {
    BigDecimal decimal = CACHE_BIG_DECIMAL.get(n);
    if (decimal == null) {
      decimal = CACHE_BIG_DECIMAL.computeIfAbsent(n, CACHE_FUNC);
    }
    return decimal;
  }

}
