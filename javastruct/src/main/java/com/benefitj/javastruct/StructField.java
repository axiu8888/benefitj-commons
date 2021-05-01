package com.benefitj.javastruct;

import com.benefitj.javastruct.convert.Converter;

import java.lang.reflect.Field;
import java.nio.ByteOrder;

/**
 * 类字段
 */
public class StructField {

  /**
   * 字段
   */
  private Field field;
  /**
   * 基本数据类型
   */
  private PrimitiveType primitiveType;
  /**
   * 注解
   */
  private JavaStructField annotation;
  /**
   * 转换器
   */
  private Converter converter;
  /**
   * 字符串的编码
   */
  private String charset = "UTF-8";

  public StructField(Field field) {
    this.field = field;
  }

  public StructField(Field field, PrimitiveType primitiveType, JavaStructField annotation) {
    this.field = field;
    this.primitiveType = primitiveType;
    this.annotation = annotation;
  }

  /**
   * 字段声明类
   */
  public Class<?> getDeclaringClass() {
    return getField().getDeclaringClass();
  }

  public Field getField() {
    return field;
  }

  public void setField(Field field) {
    this.field = field;
  }

  public PrimitiveType getPrimitiveType() {
    return primitiveType;
  }

  public void setPrimitiveType(PrimitiveType primitiveType) {
    this.primitiveType = primitiveType;
  }

  public JavaStructField getAnnotation() {
    return annotation;
  }

  public void setAnnotation(JavaStructField annotation) {
    this.annotation = annotation;
  }

  public Converter getConverter() {
    return converter;
  }

  public void setConverter(Converter converter) {
    this.converter = converter;
  }

  public String getCharset() {
    return charset;
  }

  public void setCharset(String charset) {
    this.charset = charset;
  }

  public Class<?> getType() {
    return getField().getType();
  }

  /**
   * 是否为数组类型
   */
  public boolean isArray() {
    PrimitiveType pt = getPrimitiveType();
    return pt != null && pt.isArray();
  }

  /**
   * 字段单个元素的大小
   */
  public int getFieldSize() {
    return getAnnotation().size();
  }

  /**
   * 是否小端字节顺序
   */
  public boolean isLittleEndian() {
    return getAnnotation().byteOrder() == FieldByteOrder.LITTLE_ENDIAN;
  }

  public ByteOrder getByteOrder() {
    return getAnnotation().byteOrder().getOrder();
  }

  /**
   * 数组长度
   */
  public int getArrayLength() {
    return isArray() ? getAnnotation().arrayLength() : 0;
  }

  /**
   * 字段的字节长度
   */
  public int size() {
    PrimitiveType pt = getPrimitiveType();
    JavaStructField sf = getAnnotation();
    return pt != null && pt.isArray() ? sf.arrayLength() * sf.size() : sf.size();
  }

}
