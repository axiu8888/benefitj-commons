package com.benefitj.javastruct.convert;

import com.benefitj.core.BufCopy;
import com.benefitj.javastruct.field.PrimitiveFieldType;
import com.benefitj.javastruct.field.StructField;

/**
 * 默认的基本数据类型的字段转换器
 */
public class DefaultPrimitiveFieldConverter implements PrimitiveFieldConverter {

  private final BufCopy bufCopy = BufCopy.newBufCopy();
  /**
   * 是否优先使用本地缓冲
   */
  private boolean local = true;

  public DefaultPrimitiveFieldConverter() {
  }

  public DefaultPrimitiveFieldConverter(boolean local) {
    this.local = local;
  }

  @Override
  public byte[] convert(StructField field, Object value) {
    if (value == null) {
      return getByteBuf(field.size());
    }
    PrimitiveFieldType type = PrimitiveFieldType.getFieldType(value.getClass());
    switch (type) {
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
        //case STRING_ARRAY:
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
  public BufCopy getBufCopy() {
    return bufCopy;
  }

  @Override
  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

}
