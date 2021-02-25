package com.benefitj.javastruct.field;

import com.benefitj.core.ReflectUtils;
import com.benefitj.javastruct.convert.FieldConverter;

import java.util.LinkedList;
import java.util.List;

public class StructClass {

  /**
   * 类型
   */
  private final Class<?> type;
  /**
   * 字段
   */
  private final List<StructField> fields = new LinkedList<>();
  /**
   * 结构体长度
   */
  private int size;

  public StructClass(Class<?> type) {
    this.type = type;
  }

  public Class<?> getType() {
    return type;
  }

  public List<StructField> getFields() {
    return fields;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public byte[] convert(Object o) {
    byte[] data = new byte[getSize()];
    int index = 0;
    for (StructField field : getFields()) {
      FieldConverter fc = field.getConverter();
      Object value = ReflectUtils.getFieldValue(field.getField(), o);
      byte[] bytes = fc.convert(field, value);
      System.arraycopy(bytes, 0, data, index, bytes.length);
      index += field.size();
    }
    return data;
  }
}
