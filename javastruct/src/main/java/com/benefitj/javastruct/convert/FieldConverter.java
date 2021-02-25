package com.benefitj.javastruct.convert;

import com.benefitj.javastruct.field.StructField;

/**
 * 字段转换器
 */
public interface FieldConverter {

  /**
   * 转换数据
   *
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  byte[] convert(StructField field, Object value);

}
