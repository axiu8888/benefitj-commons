package com.benefitj.javastruct.convert;


import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.lang.reflect.Field;

/**
 * 时间戳转换器
 */
public class LongTimeConverter extends AbstractConverter<Long> {

  public LongTimeConverter() {
  }

  public LongTimeConverter(boolean local) {
    super(local);
  }

  @Override
  public boolean support(Field field, JavaStructField jsf, PrimitiveType pt) {
    return pt == PrimitiveType.LONG;
  }

  @Override
  public byte[] convert(Object obj, StructField field, Object value) {
    int size = field.getFieldSize();
    if (value != null) {
      byte[] bytes = getBinary().longToBytes((Long) value);
      if (bytes.length == size) {
        return bytes;
      }
      byte[] buf = getCache(size);
      return copy(bytes, srcPos(bytes, size), buf, 0, Math.min(bytes.length, buf.length));
    }
    return getCache(size);
  }

  @Override
  public Long parse(Object obj, StructField field, byte[] data, int position) {
    byte[] buf = copy(data, position, getCache(field.getFieldSize()), 0, field.getFieldSize());
    return getBinary().bytesToLong(buf) * (field.getFieldSize() == 4 ? 1000L : 1L);
  }

}
