package com.benefitj.javastruct.resovler;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.field.StructField;

public interface PrimitiveFieldResolver<T> extends FieldResolver<T> {

  byte[] getByteBuf(int size, boolean local);

  /**
   * 解析
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的短整数
   */
  default Number parseNumber(StructField field, byte[] data, int position, boolean signed) {
    switch (field.getPrimitiveType()) {
      case BYTE:
        return field.isLittleEndian() ? data[position] : data[position + field.size() - 1];
      case SHORT:
        return parseShort(field, data, position, signed);
      case INTEGER:
        return parseInt(field, data, position, signed);
      case LONG:
        return parseLong(field, data, position, signed);
      case FLOAT:
        return parseFloat(field, data, position, signed);
      case DOUBLE:
        return parseDouble(field, data, position, signed);
      default:
        return null;
    }
  }

  /**
   * 解析
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的短整数
   */
  default short parseShort(StructField field, byte[] data, int position, boolean signed) {
    return HexUtils.bytesToShort(copy(data, position, field.size()), field.getByteOrder(), signed);
  }

  /**
   * 解析
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的整数
   */
  default int parseInt(StructField field, byte[] data, int position, boolean signed) {
    return HexUtils.bytesToInt(copy(data, position, field.size()), field.getByteOrder(), signed);
  }

  /**
   * 解析
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的长整数
   */
  default long parseLong(StructField field, byte[] data, int position, boolean signed) {
    return HexUtils.bytesToLong(copy(data, position, field.size()), field.getByteOrder(), signed);
  }

  /**
   * 解析
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的单精度浮点数
   */
  default float parseFloat(StructField field, byte[] data, int position, boolean signed) {
    return Float.floatToIntBits(parseInt(field, data, position, signed));
  }

  /**
   * 解析
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的单精度浮点数
   */
  default double parseDouble(StructField field, byte[] data, int position, boolean signed) {
    return Double.longBitsToDouble(parseLong(field, data, position, signed));
  }

  /**
   * 拷贝
   *
   * @param src    原数据
   * @param srcPos 原数据开始的位置
   * @param len    长度
   * @return 返回拷贝的数据
   */
  default byte[] copy(byte[] src, int srcPos, int len) {
    byte[] buf = getByteBuf(len, true);
    copy(src, srcPos, buf, 0, len);
    return buf;
  }

}
