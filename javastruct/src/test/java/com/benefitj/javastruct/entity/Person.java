package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.field.JavaStructClass;
import com.benefitj.javastruct.field.JavaStructField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JavaStructClass()
@Setter
@Getter
public class Person {

  /**
   * 姓名
   */
  @JavaStructField(size = 30)
  private String name;
  /**
   * 年龄
   */
  @JavaStructField(size = 1)
  private byte age;
  /**
   * V5波形数组
   */
  @JavaStructField(size = 400)
  private short[] V5;
  /**
   * 时间
   */
  @JavaStructField(size = 8)
  private long time;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte getAge() {
    return age;
  }

  public void setAge(byte age) {
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
}
