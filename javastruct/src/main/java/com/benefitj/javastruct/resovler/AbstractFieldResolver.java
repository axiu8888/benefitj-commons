package com.benefitj.javastruct.resovler;

import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.function.Function;

public abstract class AbstractFieldResolver<T> extends BufCopyFieldResolver<T> {

  public AbstractFieldResolver() {
  }

  public AbstractFieldResolver(boolean local) {
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
  public abstract boolean support(Field field, JavaStructField jsf, PrimitiveType pt);

  /**
   * 转换数据
   *
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  @Override
  public abstract byte[] convert(StructField field, Object value);

  /**
   * 解析数据
   *
   * @param field    字节
   * @param data     数据
   * @param position 下表位置
   * @return 返回解析后的对象
   */
  @Override
  public abstract T parse(StructField field, byte[] data, int position);

  /**
   * 转换布尔类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  public byte[] convertBoolean(StructField field, Object value) {
    return convert(field, value, o -> {
      byte[] buf = getCache(1);
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
  public byte[] convertByte(StructField field, Object value) {
    return convert(field, value, o -> {
      byte[] buf = getCache(1);
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
  public byte[] convertShort(StructField field, Object value) {
    return convert(field, value, o -> HexUtils.shortToBytes(((Number) value).shortValue(), field.getByteOrder()));
  }

  /**
   * 转换整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  public byte[] convertInteger(StructField field, Object value) {
    return convert(field, value, o -> HexUtils.intToBytes(((Number) value).intValue(), field.getByteOrder()));
  }

  /**
   * 转换长整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  public byte[] convertLong(StructField field, Object value) {
    return convert(field, value, o -> HexUtils.longToBytes(((Number) value).longValue(), field.getByteOrder()));
  }

  /**
   * 转换单精度浮点数
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  public byte[] convertFloat(StructField field, Object value) {
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
  public byte[] convertDouble(StructField field, Object value) {
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
  public byte[] convertString(StructField field, Object value) {
    String str = (String) value;
    byte[] bytes = str.getBytes(Charset.forName(field.getCharset()));
    int size = field.size() > 0 ? field.size() : bytes.length;
    byte[] buf = getCache(size);
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
  public byte[] convert(StructField field, Object value, Function<Object, byte[]> func) {
    int size = field.size() > 0 ? field.size() : field.getPrimitiveType().getSize();
    byte[] bytes = func.apply(value);
    if (bytes.length == size) {
      return bytes;
    }

    byte[] buf = getCache(size);
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
  public byte[] convertBooleanArray(StructField field, Object value) {
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
        buf[0] = (byte) (Boolean.TRUE.equals(array[i]) ? 1 : 0);
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
  public byte[] convertByteArray(StructField field, Object value) {
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
  public byte[] convertShortArray(StructField field, Object value) {
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
  public byte[] convertIntegerArray(StructField field, Object value) {
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
  public byte[] convertLongArray(StructField field, Object value) {
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
  public byte[] convertFloatArray(StructField field, Object value) {
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
  public byte[] convertDoubleArray(StructField field, Object value) {
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
  public byte[] convertArray(StructField field, int length, ArrayConverterFunction func) {
    int ratio = Math.max(field.size() / length, 1);
    byte[] buf = getCache(field.size());
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

  public int srcPos(byte[] src, int size) {
    return src.length >= size ? src.length - size : 0;
  }

  public int destPos(byte[] src, int size) {
    return src.length >= size ? 0 : size - src.length;
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
  public Number parseNumber(StructField field, byte[] data, int position, boolean signed) {
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
  public short parseShort(StructField field, byte[] data, int position, boolean signed) {
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
  public int parseInt(StructField field, byte[] data, int position, boolean signed) {
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
  public long parseLong(StructField field, byte[] data, int position, boolean signed) {
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
  public float parseFloat(StructField field, byte[] data, int position, boolean signed) {
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
  public double parseDouble(StructField field, byte[] data, int position, boolean signed) {
    return Double.longBitsToDouble(parseLong(field, data, position, signed));
  }

  /**
   * 解析布尔数据
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseByteArray(StructField field, byte[] data, int start) {
    if (field.getType() == byte[].class) {
      return copy(data, start, getCache(field.size(), false), 0);
    } else {
      Byte[] array = new Byte[field.size()];
      return resolveArray(data, start, array, array.length, 1
          , (arr, index, buf) -> arr[index] = data[index + start]);
    }
  }

  /**
   * 解析短整型数据
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseShortArray(StructField field, byte[] data, int start) {
    int ratio = 2;
    if (field.getType() == short[].class) {
      short[] array = new short[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf, field.getByteOrder()));
    } else {
      Short[] array = new Short[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf, field.getByteOrder()));
    }
  }

  /**
   * 解析整型数据
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseIntegerArray(StructField field, byte[] data, int start) {
    int ratio = 4;
    if (field.getType() == int[].class) {
      int[] array = new int[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToInt(buf, field.getByteOrder()));
    } else {
      Integer[] array = new Integer[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToInt(buf, field.getByteOrder()));
    }
  }

  /**
   * 解析长整型数组
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseLongArray(StructField field, byte[] data, int start) {
    int ratio = 8;
    if (field.getType() == long[].class) {
      long[] array = new long[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToLong(buf, field.getByteOrder()));
    } else {
      Long[] array = new Long[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToLong(buf, field.getByteOrder()));
    }
  }

  /**
   * 解析单精度浮点数数组
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseFloatArray(StructField field, byte[] data, int start) {
    int ratio = 4;
    if (field.getType() == float[].class) {
      float[] array = new float[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = Float.intBitsToFloat(HexUtils.bytesToInt(buf, field.getByteOrder())));
    } else {
      Float[] array = new Float[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = Float.intBitsToFloat(HexUtils.bytesToInt(buf, field.getByteOrder())));
    }
  }

  /**
   * 解析双精度浮点数数组
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseDoubleArray(StructField field, byte[] data, int start) {
    int ratio = 8;
    if (field.getType() == double[].class) {
      double[] array = new double[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = Double.longBitsToDouble(HexUtils.bytesToLong(buf, field.getByteOrder())));
    } else {
      Double[] array = new Double[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = Double.longBitsToDouble(HexUtils.bytesToLong(buf, field.getByteOrder())));
    }
  }

  /**
   * 解析布尔数据
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseBooleanArray(StructField field, byte[] data, int start) {
    int ratio = 1;
    if (field.getType() == boolean[].class) {
      boolean[] array = new boolean[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf) > 0);
    } else {
      Boolean[] array = new Boolean[field.size() / ratio];
      return resolveArray(data, start, array, array.length, ratio
          , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf) > 0);
    }
  }

  public <R> R resolveArray(byte[] data, int start, R array, int arrayLength, int ratio, ArrayResolverFunction<R> func) {
    byte[] buf = getCache(ratio);
    for (int i = 0; i < arrayLength; i++) {
      copy(data, start + i * ratio, buf, 0, ratio);
      func.accept(array, i, buf);
    }
    return array;
  }

  public interface ArrayResolverFunction<T> {

    /**
     * 处理数据
     *
     * @param array 数组
     * @param index 索引
     * @param buf   读取的缓冲
     */
    void accept(T array, int index, byte[] buf);

  }


  interface ArrayConverterFunction {
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
