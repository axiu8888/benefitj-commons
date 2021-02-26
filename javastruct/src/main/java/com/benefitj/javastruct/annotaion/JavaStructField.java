package com.benefitj.javastruct.annotaion;

import com.benefitj.javastruct.convert.FieldConverter;
import com.benefitj.javastruct.field.FieldByteOrder;
import com.benefitj.javastruct.resovler.FieldResolver;

import java.lang.annotation.*;

/**
 * 字段注解
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface JavaStructField {

  /**
   * 单个元素的大小，如：
   * 1、int[]{20, 20, 45}，单个元素为4字节，总长度为 4 * 3 = 12字节
   * 2、String("ABCD")，字符长度为4，单个元素为 4或其他，如果少于4，则只会取前三个字符
   */
  int size();

  /**
   * 字节顺序
   */
  FieldByteOrder byteOrder() default FieldByteOrder.BIG_ENDIAN;

  /**
   * 转换器
   */
  Class<? extends FieldConverter> converter() default FieldConverter.class;

  /**
   * 解析器
   */
  Class<? extends FieldResolver> resolver() default FieldResolver.class;

  /**
   * 字节编码
   */
  String charset() default "";

}
