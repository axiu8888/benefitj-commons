package com.benefitj.javastruct.resovler;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;

/**
 * 时间戳解析器
 */
public class DateTimeFieldResolver extends DefaultPrimitiveFieldResolver {

  public DateTimeFieldResolver() {
  }

  public DateTimeFieldResolver(boolean local) {
    super(local);
  }

  @Override
  public boolean support(Field field, JavaStructField jsf, PrimitiveType pt) {
    Class<?> type = field.getType();
    return type.isAssignableFrom(Date.class) || type.isAssignableFrom(Timestamp.class);
  }

  @Override
  public Object resolve(StructField field, byte[] data, int position) {
    long time;
    int size = field.size();
    if (size == 4) {
      byte[] buf = copy(data, position, getByteBuf(size), 0);
      time = HexUtils.bytesToLong(buf, field.getByteOrder()) * 1000;
    } else if (size == 6) {
      byte[] buf = copy(data, position, getByteBuf(4), 0, 4);
      time = HexUtils.bytesToLong(buf, field.getByteOrder());
      buf = copy(data, position + 4, getByteBuf(2), 0, 2);
      time += HexUtils.bytesToLong(buf, field.getByteOrder());
    } else {
      byte[] buf = copy(data, position, getByteBuf(size), 0);
      time = HexUtils.bytesToLong(buf);
    }

    Field f = field.getField();
    if (f.getType().isAssignableFrom(java.sql.Date.class)) {
      return new java.sql.Date(time);
    } else if (f.getType().isAssignableFrom(Date.class)) {
      return new Date(time);
    } else if (f.getType().isAssignableFrom(Timestamp.class)) {
      return new Timestamp(time);
    }
    throw new UnsupportedOperationException();
  }

}
