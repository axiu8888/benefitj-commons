package com.benefitj.interpolator;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 数组工具
 */
public class ArrayUtils {

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
    return arrayToList(array, Number::shortValue);
  }

  /**
   * 数组转换成Integer列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Integer> arrayToInteger(Object array) {
    return arrayToList(array, Number::intValue);
  }

  /**
   * 数组转换成Long列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Long> arrayToLong(Object array) {
    return arrayToList(array, Number::longValue);
  }

  /**
   * 数组转换成Float列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Float> arrayToFloat(Object array) {
    return arrayToList(array, Number::floatValue);
  }

  /**
   * 数组转换成Double列表
   *
   * @param array 数组
   * @return 返回转换后的列表
   */
  public static List<Double> arrayToDouble(Object array) {
    return arrayToList(array, Number::doubleValue);
  }

}
