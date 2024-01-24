package com.benefitj.javastruct.convert;


import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.lang.reflect.Field;

/**
 * 16进制字符串转换
 */
public class LowerCaseHexStringConverter extends AbstractConverter<String> {

  public LowerCaseHexStringConverter() {
  }

  public LowerCaseHexStringConverter(boolean local) {
    super(local);
  }

  @Override
  public boolean support(Field field, JavaStructField jsf, PrimitiveType pt) {
    return pt == PrimitiveType.STRING;
  }

  @Override
  public byte[] convert(Object obj, StructField field, Object value) {
    int size = field.getFieldSize();
    if (value != null) {
      byte[] bytes = getBinary().hexToBytes((String) value);
      if (bytes.length == size) {
        return bytes;
      }
      byte[] buf = getCache(size);
      return copy(bytes, srcPos(bytes, size), buf, 0, Math.min(bytes.length, buf.length));
    }
    return getCache(size);
  }

  @Override
  public String parse(Object obj, StructField field, byte[] data, int position) {
    byte[] buf = copy(data, position, getCache(field.getFieldSize()), 0, field.getFieldSize());
    return getBinary().bytesToHex(buf, true);
  }

}
