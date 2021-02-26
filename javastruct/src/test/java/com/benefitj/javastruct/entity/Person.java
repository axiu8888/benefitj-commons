package com.benefitj.javastruct.entity;

import com.benefitj.javastruct.convert.DateTimeFieldConverter;
import com.benefitj.javastruct.annotaion.JavaStructClass;
import com.benefitj.javastruct.annotaion.JavaStructField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@JavaStructClass
@Setter
@Getter
public class Person {

  /**
   * 姓名
   */
  @JavaStructField(size = 20)
  private String name;
  /**
   * 年龄
   */
  @JavaStructField(size = 1)
  private int age;
//  /**
//   * V5波形数组
//   */
//  @JavaStructField(size = 400)
//  private short[] V5;
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
//  /**
//   * 时间
//   */
//  @JavaStructField(size = 1024 * 3, charset = "UTF-8")
//  private String remarks;


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

//  public short[] getV5() {
//    return V5;
//  }
//
//  public void setV5(short[] v5) {
//    V5 = v5;
//  }

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
}
