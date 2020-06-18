package com.benefitj.core;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 枚举的工具类
 */
public class EnumHelper {

  /**
   * 获取匹配的值
   *
   * @param enums 枚举对象数组
   * @param name  名称
   * @param <E>   枚举类型
   * @return 返回匹配的对象
   */
  @Nullable
  public static <E extends Enum> E nameOf(E[] enums, String name) {
    return nameOf(enums, name, true);
  }

  /**
   * 获取匹配的值
   *
   * @param enums      枚举对象数组
   * @param name       名称
   * @param ignoreCase 是否忽略大小写
   * @param <E>        枚举类型
   * @return 返回匹配的对象
   */
  @Nullable
  public static <E extends Enum> E nameOf(E[] enums, String name, boolean ignoreCase) {
    return apply(enums, e -> (ignoreCase ? e.name().equalsIgnoreCase(name) : e.name().equals(name)) ? e : null, null);
  }

  /**
   * 获取匹配的值
   *
   * @param enums 枚举对象数组
   * @param func  匹配函数
   * @param <E>   枚举类型
   * @return 返回匹配的对象
   */
  @Nullable
  public static <E extends Enum> E valueOf(E[] enums, Function<E, E> func) {
    return apply(enums, func, null);
  }

  /**
   * 获取匹配的值
   *
   * @param type 枚举类型
   * @param func 匹配函数
   * @param <E>  枚举类型
   * @return 返回匹配的对象
   */
  @Nullable
  public static <E extends Enum> E valueOf(Class<E> type, Function<E, E> func) {
    return valueOf(type.getEnumConstants(), func);
  }

  /**
   * 迭代枚举值
   *
   * @param enums    枚举对象数组
   * @param consumer 匹配函数
   */
  public static <E extends Enum<E>> void accept(E[] enums, Consumer<E> consumer) {
    for (E e : enums) {
      consumer.accept(e);
    }
  }

  /**
   * 迭代枚举值
   *
   * @param type     枚举类型
   * @param consumer 匹配函数
   */
  public static <E extends Enum<E>> void accept(Class<E> type, Consumer<E> consumer) {
    accept(type.getEnumConstants(), consumer);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param type 枚举类型
   * @param func 迭代函数
   * @param <E>  枚举类型
   * @param <R>  返回类型
   * @return 返回匹配的值或默认值
   */
  @Nullable
  public static <E extends Enum<E>, R> R apply(Class<E> type, Function<E, R> func) {
    return apply(type, func, null);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param type         枚举类型
   * @param func         迭代函数
   * @param defaultValue 默认值
   * @param <E>          枚举类型
   * @param <R>          返回类型
   * @return 返回匹配的值或默认值
   */
  public static <E extends Enum<E>, R> R apply(Class<E> type, Function<E, R> func, R defaultValue) {
    return apply(type.getEnumConstants(), func, defaultValue);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param enums 枚举类型
   * @param func  迭代函数
   * @param <E>   枚举类型
   * @param <R>   返回类型
   * @return 返回匹配的值或默认值
   */
  @Nullable
  public static <E, R> R apply(E[] enums, Function<E, R> func) {
    return apply(enums, func, null);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param enums        枚举类型
   * @param func         迭代函数
   * @param defaultValue 默认值
   * @param <E>          枚举类型
   * @param <R>          返回类型
   * @return 返回匹配的值或默认值
   */
  public static <E, R> R apply(E[] enums, Function<E, R> func, R defaultValue) {
    R r;
    for (E e : enums) {
      if ((r = func.apply(e)) != null) {
        return r;
      }
    }
    return defaultValue;
  }

}
