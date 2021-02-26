package com.benefitj.javastruct.convert;

import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;

/**
 * 字段转换器
 */
public interface FieldConverter {

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
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  byte[] convert(StructField field, Object value);

}
