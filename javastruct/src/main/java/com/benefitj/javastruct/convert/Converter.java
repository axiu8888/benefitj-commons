package com.benefitj.javastruct.convert;

import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.lang.reflect.Field;

/**
 * 转换器
 *
 * @param <T>
 */
public interface Converter<T> {

  /**
   * 是否支持的类型
   *
   * @param field 字段
   * @param jsf   字段的注解
   * @param pt    字段对应的基本类型
   * @return 返回是否支持
   */
  boolean support(Field field, JavaStructField jsf, PrimitiveType pt);

  /**
   * 转换数据
   *
   * @param obj   对象
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  byte[] convert(Object obj, StructField field, Object value);

  /**
   * 解析数据
   *
   * @param obj      对象
   * @param field    字节
   * @param data     数据
   * @param position 下表位置
   * @return 返回解析后的对象
   */
  T parse(Object obj, StructField field, byte[] data, int position);

}
