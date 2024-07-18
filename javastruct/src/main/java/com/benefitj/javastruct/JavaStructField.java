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
   * 开始的位置，如果指定了此参数，则使用此参数，没有就根据字段顺序
   */
  int startAt() default -1;

  /**
   * 单个元素的字节数
   * 如果是数组：int[]{20, 20, 45}，单个元素为4字节，单个长度(4) * 数组长度(3) = 总长度(12字节)
   */
  int size();

  /**
   * 如果是数组，可以指定数组长度
   * 当配合 {@link this#size()}
   */
  int arrayLength() default 0;

  /**
   * 字节顺序，对于一些数值类型，可能有大小端的问题
   */
  FieldByteOrder byteOrder() default FieldByteOrder.BIG_ENDIAN;

  /**
   * 是否为有符号位
   */
  boolean singed() default false;

  /**
   * 指定具体的转换器(自定义的需要注册)
   */
  Class<? extends Converter> converter() default Converter.class;

  /**
   * 如果是字符串，可以指定字节编码
   */
  String charset() default "";

}
