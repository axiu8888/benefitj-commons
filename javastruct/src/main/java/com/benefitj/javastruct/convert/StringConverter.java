package com.benefitj.javastruct.convert;


import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;

/**
 * 字符串转换
 */
public class StringConverter extends AbstractConverter<String> {

  public StringConverter() {
  }

  public StringConverter(boolean local) {
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
      byte[] bytes;
      try {
        bytes = ((String) value).getBytes(field.getCharset());
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException("不支持的字符集【"
            + (field.getField().getDeclaringClass().getName())
            + "." + field.getField().getName() + "】：" + field.getCharset());
      }
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
    return new String(buf).trim();
  }

}
