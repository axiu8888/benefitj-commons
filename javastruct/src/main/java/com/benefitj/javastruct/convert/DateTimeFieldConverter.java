package com.benefitj.javastruct.convert;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.util.Date;

public class DateTimeFieldConverter extends DefaultPrimitiveFieldConverter {

  public DateTimeFieldConverter() {
  }

  public DateTimeFieldConverter(boolean local) {
    super(local);
  }

  /**
   * 是否支持的类型
   *
   * @param field 字段
   * @param jsf   字段的注解
   * @param pt    字段对应的基本类型
   * @return 返回是否支持
   */
  @Override
  public boolean support(Field field, JavaStructField jsf, PrimitiveType pt) {
    Class<?> type = field.getType();
    return type.isAssignableFrom(Date.class) || type.isAssignableFrom(Timestamp.class);
  }

  /**
   * 转换数据
   *
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  @Override
  public byte[] convert(StructField field, Object value) {
    long time;
    if (value instanceof Timestamp) {
      time = ((Timestamp) value).getTime();
    } else {
      time = ((Date) value).getTime();
    }
    int size = field.size();
    byte[] bytes;
    switch (size) {
      case 4:
        bytes = HexUtils.intToBytes((int) (time / 1000), field.getByteOrder());
        break;
      case 6:
        byte[] buf = getByteBuf(6);
        bytes = HexUtils.longToBytes((int) (time / 1000), field.getByteOrder());
        copy(bytes, 0, buf, 0);
        bytes = HexUtils.intToBytes((int) time % 1000, field.getByteOrder());
        return copy(bytes, 0, buf, 4, 2);
      case 8:
      default:
        bytes = HexUtils.longToBytes(time, field.getByteOrder());
        break;
    }
    return copy(bytes, srcPos(bytes, size), getByteBuf(size), destPos(bytes, size));
  }

}
