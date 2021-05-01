package com.benefitj.javastruct.convert;

import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * 默认的基本数据类型转换器
 *
 * @author DINGXIUAN
 */
public class DefaultPrimitiveConverter extends AbstractConverter<Object> {

  public DefaultPrimitiveConverter() {
  }

  public DefaultPrimitiveConverter(boolean local) {
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
    return pt != null;
  }

  @Override
  public byte[] convert(StructField field, Object value) {
    if (value == null) {
      return getCache(field.getFieldSize());
    }

    PrimitiveType pt = field.getPrimitiveType();

    if (pt.isArray()) {
      int length = pt.arrayLength(value);
      if (length != field.getArrayLength()) {
        throw new IllegalStateException(String.format(
            "数组长度不匹配，期待长度%d，实际长度%d", field.getArrayLength(), length));
      }
    }

    switch (pt) {
      case BOOLEAN:
        return convertBoolean(field, value);
      case BYTE:
        return convertByte(field, value);
      case SHORT:
        return convertShort(field, value);
      case INTEGER:
        return convertInteger(field, value);
      case LONG:
        return convertLong(field, value);
      case FLOAT:
        return convertFloat(field, value);
      case DOUBLE:
        return convertDouble(field, value);
      case STRING:
        return convertString(field, value);
      case BOOLEAN_ARRAY:
        return convertBooleanArray(field, value);
      case BYTE_ARRAY:
        return convertByteArray(field, value);
      case SHORT_ARRAY:
        return convertShortArray(field, value);
      case INTEGER_ARRAY:
        return convertIntegerArray(field, value);
      case LONG_ARRAY:
        return convertLongArray(field, value);
      case FLOAT_ARRAY:
        return convertFloatArray(field, value);
      case DOUBLE_ARRAY:
        return convertDoubleArray(field, value);
      default:
        throw new UnsupportedOperationException("Unsupported !");
    }
  }

  @Override
  public Object parse(StructField field, byte[] data, int position) {
    if (field.isArray()) {
      PrimitiveType pt = field.getPrimitiveType();
      switch (pt) {
        case BYTE_ARRAY:
          return parseByteArray(field, data, position);
        case SHORT_ARRAY:
          return parseShortArray(field, data, position);
        case INTEGER_ARRAY:
          return parseIntegerArray(field, data, position);
        case LONG_ARRAY:
          return parseLongArray(field, data, position);
        case FLOAT_ARRAY:
          return parseFloatArray(field, data, position);
        case DOUBLE_ARRAY:
          return parseDoubleArray(field, data, position);
        case BOOLEAN_ARRAY:
          return parseBooleanArray(field, data, position);
        default:
      }
    } else {
      Class<?> type = field.getField().getType();
      if (type.isAssignableFrom(Number.class)) {
        return parseNumber(field, data, position, true);
      }
      // 基本数据类型
      if (type == boolean.class || type == Boolean.class) {
        return parseShort(field, data, position, true) >= 1;
      } else if (type == byte.class) {
        return (byte) parseShort(field, data, position, true);
      } else if (type == short.class) {
        return parseShort(field, data, position, true);
      } else if (type == int.class) {
        return parseInt(field, data, position, true);
      } else if (type == long.class) {
        return parseLong(field, data, position, true);
      } else if (type == String.class) {
        byte[] buf = getCache(field.getFieldSize());
        copy(data, position, buf, 0);
        return new String(buf, Charset.forName(field.getCharset())).trim();
      }
    }

    // ~bang
    throw new UnsupportedOperationException();
  }

}
