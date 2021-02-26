package com.benefitj.javastruct.field;

import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.convert.FieldConverter;

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
  private JavaStructField structField;
  /**
   * 转换器
   */
  private FieldConverter converter;

  public StructField(Field field) {
    this.field = field;
  }

  public StructField(Field field, PrimitiveType primitiveType, JavaStructField structField) {
    this.field = field;
    this.primitiveType = primitiveType;
    this.structField = structField;
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

  public JavaStructField getStructField() {
    return structField;
  }

  public void setStructField(JavaStructField structField) {
    this.structField = structField;
  }

  public void setConverter(FieldConverter converter) {
    this.converter = converter;
  }

  public FieldConverter getConverter() {
    return converter;
  }

  /**
   * 是否为数组类型
   */
  public boolean isArray() {
    return getPrimitiveType().isArray();
  }

  /**
   * 数据大小
   */
  public int size() {
    return getStructField().size();
  }

  /**
   * 是否小端字节顺序
   */
  public boolean isLittleEndian() {
    return getStructField().byteOrder() == FieldByteOrder.LITTLE_ENDIAN;
  }

  public ByteOrder getByteOrder() {
    return getStructField().byteOrder().getOrder();
  }

}
