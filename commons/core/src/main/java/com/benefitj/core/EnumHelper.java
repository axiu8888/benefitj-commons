package com.benefitj.core;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 枚举的工具类
 */
public class EnumHelper {

  /**
   * 判断是否为匹配的值
   *
   * @param src  枚举对象
   * @param name 名称
   * @param <E>  枚举类型
   * @return 返回是否匹配
   */
  public static <E extends Enum> boolean nameEquals(E src, String name) {
    return nameEquals(src, name, true);
  }

  /**
   * 判断是否为匹配的值
   *
   * @param src        枚举对象
   * @param name       名称
   * @param ignoreCase 是否忽略大小写
   * @param <E>        枚举类型
   * @return 返回是否匹配
   */
  public static <E extends Enum> boolean nameEquals(E src, String name, boolean ignoreCase) {
    return ignoreCase ? src.name().equalsIgnoreCase(name) : src.name().equals(name);
  }

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
    return valueOf(enums, e -> ignoreCase ? e.name().equalsIgnoreCase(name) : e.name().equals(name), null);
  }

  /**
   * 获取匹配的值，已过时，建议使用 {@link #valueOf(Object[], Predicate)}
   *
   * @param enums 枚举对象数组
   * @param func  匹配函数
   * @param <E>   枚举类型
   * @return 返回匹配的对象
   */
  @Deprecated
  @Nullable
  public static <E extends Enum> E valueOf(E[] enums, Function<E, E> func) {
    return apply(enums, func, null);
  }

  /**
   * 获取匹配的值，已过时，建议使用 {@link #valueOf(Object[], Predicate)}
   *
   * @param type 枚举类型
   * @param func 匹配函数
   * @param <E>  枚举类型
   * @return 返回匹配的对象
   */
  @Deprecated
  @Nullable
  public static <E extends Enum> E valueOf(Class<E> type, Function<E, E> func) {
    return valueOf(type.getEnumConstants(), func);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param type 枚举类型
   * @param func 迭代函数
   * @param <E>  枚举类型
   * @return 返回匹配的值或默认值
   */
  public static <E extends Enum<E>> E valueOf(Class<E> type, Predicate<E> func) {
    return valueOf(type.getEnumConstants(), func, null);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param type         枚举类型
   * @param func         迭代函数
   * @param defaultValue 默认值
   * @param <E>          枚举类型
   * @return 返回匹配的值或默认值
   */
  public static <E extends Enum<E>> E valueOf(Class<E> type, Predicate<E> func, E defaultValue) {
    return valueOf(type.getEnumConstants(), func, defaultValue);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param enums 枚举类型
   * @param func  迭代函数
   * @param <E>   枚举类型
   * @return 返回匹配的值或默认值
   */
  public static <E> E valueOf(E[] enums, Predicate<E> func) {
    return valueOf(enums, func, null);
  }

  /**
   * 迭代枚举值，并返回匹配值
   *
   * @param enums        枚举类型
   * @param func         迭代函数
   * @param defaultValue 默认值
   * @param <E>          枚举类型
   * @return 返回匹配的值或默认值
   */
  public static <E> E valueOf(E[] enums, Predicate<E> func, E defaultValue) {
    for (E e : enums) {
      if (func.test(e)) {
        return e;
      }
    }
    return defaultValue;
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
