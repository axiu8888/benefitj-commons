package com.benefitj.javastruct.convert;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.StructField;

import javax.annotation.Nullable;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * 基本数据类型的字段转换器
 */
public interface PrimitiveFieldConverter extends FieldConverter {

  /**
   * @param field 类字段信息
   * @param value 字段值
   * @return
   */
  @Override
  byte[] convert(StructField field, Object value);

  /**
   * 是否为本地
   */
  boolean isLocal();

  /**
   * 获取字节缓冲
   *
   * @param size 字节大小
   * @return 返回字节数组
   */
  byte[] getByteBuf(int size);

  /**
   * 拷贝数据
   *
   * @param src  原数组
   * @param dest 目标数组
   * @return 返回拷贝后的目标数据
   */
  default byte[] copy(byte[] src, byte[] dest) {
    return copy(src, 0, dest, 0, Math.min(src.length, dest.length));
  }

  /**
   * 拷贝数据
   *
   * @param src  原数组
   * @param dest 目标数组
   * @param len  长度
   * @return 返回拷贝后的目标数据
   */
  default byte[] copy(byte[] src, byte[] dest, int len) {
    return copy(src, 0, dest, 0, len);
  }

  /**
   * 拷贝数据
   *
   * @param src     原数组
   * @param srcPos  原数据开始的位置
   * @param dest    目标数组
   * @param destPos 目标数据开始的位置
   * @return 返回拷贝后的目标数据
   */
  default byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos) {
    return copy(src, srcPos, dest, destPos, Math.min(src.length - srcPos, dest.length - destPos));
  }

  /**
   * 拷贝数据
   *
   * @param src     原数组
   * @param srcPos  原数据开始的位置
   * @param dest    目标数组
   * @param destPos 目标数据开始的位置
   * @param len     长度
   * @return 返回拷贝后的目标数据
   */
  default byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
    System.arraycopy(src, srcPos, dest, destPos, len);
    return dest;
  }

  /**
   * 转换布尔类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertBoolean(StructField field, Object value) {
    return convert(field, value, o -> {
      byte[] buf = getByteBuf(1);
      buf[0] = (byte) (Boolean.TRUE.equals(o) ? 1 : 0);
      return buf;
    });
  }

  /**
   * 转换字节类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertByte(StructField field, Object value) {
    return convert(field, value, o -> {
      byte[] buf = getByteBuf(1);
      buf[0] = (byte) o;
      return buf;
    });
  }

  /**
   * 转换短整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertShort(StructField field, Object value) {
    return convert(field, value, o -> HexUtils.shortToBytes(((Number) value).shortValue(), field.getByteOrder()));
  }

  /**
   * 转换整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertInteger(StructField field, Object value) {
    return convert(field, value, o -> HexUtils.intToBytes(((Number) value).intValue(), field.getByteOrder()));
  }

  /**
   * 转换长整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertLong(StructField field, Object value) {
    return convert(field, value, o -> HexUtils.longToBytes(((Number) value).longValue(), field.getByteOrder()));
  }

  /**
   * 转换单精度浮点数
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertFloat(StructField field, Object value) {
    return convert(field, value, o ->
        HexUtils.intToBytes(Float.floatToIntBits(((Number) value).floatValue()), field.getByteOrder()));
  }

  /**
   * 转换双精度浮点数
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertDouble(StructField field, Object value) {
    return convert(field, value, o ->
        HexUtils.longToBytes(Double.doubleToLongBits(((Number) value).doubleValue()), field.getByteOrder()));
  }

  /**
   * 转换字符串
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertString(StructField field, Object value) {
    String str = (String) value;
    JavaStructField sf = field.getStructField();
    String name = sf.charset().trim();
    Charset charset = Charset.forName(!name.isEmpty() ? name : "UTF-8");
    byte[] bytes = str.getBytes(charset);
    int size = field.size() > 0 ? field.size() : bytes.length;
    byte[] buf = getByteBuf(size);
    return copy(bytes, 0, buf, 0, Math.min(buf.length, bytes.length));
  }

  /**
   * 转换
   *
   * @param field 字段
   * @param value 值
   * @param func  转换函数
   * @return 返回转换后的字节数据
   */
  default byte[] convert(StructField field, Object value, Function<Object, byte[]> func) {
    int size = field.size() > 0 ? field.size() : field.getPrimitiveType().getSize();
    byte[] bytes = func.apply(value);
    if (isLocal() && bytes.length == size) {
      return bytes;
    }

    byte[] buf = getByteBuf(size);
    if (field.isLittleEndian()) {
      return copy(bytes, buf);
    }
    int srcPos = bytes.length > size ? bytes.length - size : size - bytes.length;
    return copy(bytes, srcPos, buf, 0, Math.min(bytes.length, size));
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertBooleanArray(StructField field, Object value) {
    if (value.getClass() == boolean[].class) {
      boolean[] array = (boolean[]) value;
      byte[] buf = new byte[1];
      return convertArray(field, array.length, i -> {
        buf[0] = (byte) (array[i] ? 1 : 0);
        return buf;
      });
    } else {
      Boolean[] array = (Boolean[]) value;
      byte[] buf = new byte[1];
      return convertArray(field, array.length, i -> {
        buf[0] = (byte) (array[i] ? 1 : 0);
        return buf;
      });
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertByteArray(StructField field, Object value) {
    if (value.getClass() == byte[].class) {
      byte[] array = (byte[]) value;
      byte[] buf = new byte[1];
      return convertArray(field, array.length, i -> {
        buf[0] = array[i];
        return buf;
      });
    } else {
      Byte[] array = (Byte[]) value;
      byte[] buf = new byte[1];
      return convertArray(field, array.length, i -> {
        buf[0] = array[i] != null ? array[i] : 0;
        return buf;
      });
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertShortArray(StructField field, Object value) {
    if (value.getClass() == short[].class) {
      short[] array = (short[]) value;
      return convertArray(field, array.length, i -> HexUtils.shortToBytes(array[i]));
    } else {
      Short[] array = (Short[]) value;
      return convertArray(field, array.length, i ->
          array[i] != null ? HexUtils.shortToBytes(array[i]) : null);
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertIntegerArray(StructField field, Object value) {
    if (value.getClass() == int[].class) {
      int[] array = (int[]) value;
      return convertArray(field, array.length, i -> HexUtils.intToBytes(array[i]));
    } else {
      Integer[] array = (Integer[]) value;
      return convertArray(field, array.length, i ->
          array[i] != null ? HexUtils.intToBytes(array[i]) : null);
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertLongArray(StructField field, Object value) {
    if (value.getClass() == long[].class) {
      long[] array = (long[]) value;
      return convertArray(field, array.length, i -> HexUtils.longToBytes(array[i]));
    } else {
      Long[] array = (Long[]) value;
      return convertArray(field, array.length, i ->
          array[i] != null ? HexUtils.longToBytes(array[i]) : null);
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertFloatArray(StructField field, Object value) {
    if (value.getClass() == float[].class) {
      float[] array = (float[]) value;
      return convertArray(field, array.length, i -> HexUtils.intToBytes(Float.floatToIntBits(array[i])));
    } else {
      Float[] array = (Float[]) value;
      return convertArray(field, array.length, i ->
          array[i] != null ? HexUtils.intToBytes(Float.floatToIntBits(array[i])) : null);
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  default byte[] convertDoubleArray(StructField field, Object value) {
    if (value.getClass() == double[].class) {
      double[] array = (double[]) value;
      return convertArray(field, array.length, i -> HexUtils.longToBytes(Double.doubleToLongBits(array[i])));
    } else {
      Double[] array = (Double[]) value;
      return convertArray(field, array.length, i ->
          array[i] != null ? HexUtils.longToBytes(Double.doubleToLongBits(array[i])) : null);
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field  字段信息
   * @param length 数组长度
   * @param func   数组处理的函数
   * @return 返回转换后的数据
   */
  default byte[] convertArray(StructField field, int length, ArrayFunction func) {
    int ratio = field.size() / length;
    byte[] buf = getByteBuf(field.size());
    for (int i = 0; i < length; i++) {
      byte[] bytes = func.apply(i);
      if (bytes != null) {
        if (field.isLittleEndian()) {
          copy(bytes, 0, buf, i * ratio, Math.min(bytes.length, ratio));
        } else {
          copy(bytes, srcPos(bytes, ratio), buf, destPos(bytes, ratio) + i * ratio, Math.min(bytes.length, ratio));
        }
      }
    }
    return buf;
  }

  default int srcPos(byte[] src, int size) {
    return src.length > size ? src.length - size : size - src.length;
  }

  default int destPos(byte[] src, int size) {
    return src.length > size ? 0 : size - src.length;
  }

  interface ArrayFunction {
    /**
     * 获取元素对应的字节数组
     *
     * @param index 数组的索引
     * @return 返回元素对应的字节
     */
    @Nullable
    byte[] apply(int index);
  }

}
