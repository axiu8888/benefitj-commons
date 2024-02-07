package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.convert.UpperCaseHexStringConverter;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;


@SuperBuilder
@NoArgsConstructor
@Data
@JavaStructClass
public class Person {
  /**
   * 姓名
   */
  @JavaStructField(size = 10)
  String name;
  /**
   * 年龄
   */
  @JavaStructField(size = 1)
  int age;
  /**
   * 16进制字符串
   */
  @JavaStructField(size = 16, converter = UpperCaseHexStringConverter.class)
  String hex;
  /**
   * V5波形数组: 200采样率
   */
  @JavaStructField(size = 2, arrayLength = 200)
  short[] V5;
  /**
   * 时间
   */
  @JavaStructField(size = 8)
  long time;
  /**
   * 时间
   */
  @JavaStructField(size = 4)
  Date createTime;
  /**
   * 新添加的字段
   */
  @JavaStructField(size = 21)
  String hello;

}
