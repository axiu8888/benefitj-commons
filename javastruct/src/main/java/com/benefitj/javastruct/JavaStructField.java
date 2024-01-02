package com.benefitj.javastruct;

import com.benefitj.javastruct.convert.Converter;

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
   * 开始的位置，如果指定了此参数，则使用此参数
   */
  int startAt() default -1;
  /**
   * 单个元素的比例，如：
   * int[]{20, 20, 45}，单个元素为4字节，单个长度(4) * 数组长度(3) = 总长度(12字节)
   */
  int size();

  /**
   * 字节顺序
   */
  FieldByteOrder byteOrder() default FieldByteOrder.BIG_ENDIAN;

  /**
   * 是否为有符号位
   */
  boolean singed() default false;

  /**
   * 转换器
   */
  Class<? extends Converter> converter() default Converter.class;

  /**
   * 字节编码
   */
  String charset() default "";

  /**
   * 数组长度
   */
  int arrayLength() default 0;

}
