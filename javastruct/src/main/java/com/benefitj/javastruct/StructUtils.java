//package com.benefitj.javastruct;
//
//import com.benefitj.core.ReflectUtils;
//
//import java.lang.reflect.Field;
//import java.util.function.Consumer;
//import java.util.function.Predicate;
//
//public class StructUtils {
//
//
//  /**
//   * 迭代 field
//   *
//   * @param type        类
//   * @param filter      过滤器
//   * @param consumer    消费者
//   * @param interceptor 拦截器
//   * @param superclass  是否继续迭代父类
//   */
//  public static void foreachField(Class<?> type,
//                                  Predicate<Field> filter,
//                                  Consumer<Field> consumer,
//                                  Predicate<Field> interceptor,
//                                  boolean superclass) {
//    ReflectUtils.foreach(type, Class::getDeclaredFields, filter, consumer, interceptor, superclass);
//  }
//
//
//  /**
//   * 获取字段的值
//   *
//   * @param field 字段
//   * @param obj   原对象
//   * @param <V>   值类型
//   * @return 返回获取到的值
//   */
//  public static <V> V getFieldValue(Field field, Object obj) {
//    return ReflectUtils.getFieldValue(field, obj);
//  }
//
//  /**
//   * 设置字段的值
//   *
//   * @param field 字段
//   * @param obj   对象
//   * @param value 值
//   * @return 返回是否设置成功
//   */
//  public static boolean setFieldValue(Field field, Object obj, Object value) {
//    return ReflectUtils.setFieldValue(field, obj, value);
//  }
//
//}
