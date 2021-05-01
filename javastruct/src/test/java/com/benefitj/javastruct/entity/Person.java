package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.JavaStructClass;
import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.convert.HexStringConverter;

import java.util.Date;

@JavaStructClass
public class Person {
  /**
   * 姓名
   */
  @JavaStructField(size = 10)
  private String name;
  /**
   * 年龄
   */
  @JavaStructField(size = 1)
  private int age;
  /**
   * 16进制字符串
   */
  @JavaStructField(size = 16, converter = HexStringConverter.class)
  private String hex;
  /**
   * V5波形数组: 200采样率
   */
  @JavaStructField(size = 2, arrayLength = 200)
  private short[] V5;
  /**
   * 时间
   */
  @JavaStructField(size = 8)
  private long time;
  /**
   * 时间
   */
  @JavaStructField(size = 4)
  private Date createTime;
  /**
   * 新添加的字段
   */
  @JavaStructField(size = 21)
  private String hello;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public short[] getV5() {
    return V5;
  }

  public void setV5(short[] v5) {
    V5 = v5;
  }

  public long getTime() {
    return time;
  }

  public void setTime(long time) {
    this.time = time;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getHex() {
    return hex;
  }

  public void setHex(String hex) {
    this.hex = hex;
  }

  public String getHello() {
    return hello;
  }

  public void setHello(String hello) {
    this.hello = hello;
  }
}
