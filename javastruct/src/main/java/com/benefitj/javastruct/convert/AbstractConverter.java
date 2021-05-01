package com.benefitj.javastruct.convert;

import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.function.Function;

/**
 * 抽象的转换器
 *
 * @param <T>
 */
public abstract class AbstractConverter<T> extends BufCopyConverter<T> {

  public AbstractConverter() {
  }

  public AbstractConverter(boolean local) {
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
    return convert(field, value, o -> getBinary().shortToBytes(((Number) value).shortValue(), field.getByteOrder()));
  }

  /**
   * 转换整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  public byte[] convertInteger(StructField field, Object value) {
    return convert(field, value, o -> getBinary().intToBytes(((Number) value).intValue(), field.getByteOrder()));
  }

  /**
   * 转换长整型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的数据
   */
  public byte[] convertLong(StructField field, Object value) {
    return convert(field, value, o -> getBinary().longToBytes(((Number) value).longValue(), field.getByteOrder()));
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
        getBinary().intToBytes(Float.floatToIntBits(((Number) value).floatValue()), field.getByteOrder()));
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
        getBinary().longToBytes(Double.doubleToLongBits(((Number) value).doubleValue()), field.getByteOrder()));
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
    int size = field.getFieldSize() > 0 ? field.getFieldSize() : bytes.length;
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
    int size = field.getFieldSize() > 0 ? field.getFieldSize() : field.getPrimitiveType().getSize();
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
      byte[] buf = new byte[field.size()];
      return convertArray(field, i -> {
        buf[0] = (byte) (array[i] ? 1 : 0);
        return buf;
      });
    } else {
      Boolean[] array = (Boolean[]) value;
      byte[] buf = new byte[1];
      return convertArray(field, i -> {
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
      PrimitiveType pt = field.getPrimitiveType();
      if (field.getArrayLength() == pt.arrayLength(value)) {
        return copy((byte[]) value, 0, field.getArrayLength());
      }
      byte[] array = (byte[]) value;
      byte[] buf = new byte[field.size()];
      return convertArray(field, i -> {
        buf[field.isLittleEndian() ? 0 : array.length - 1] = array[i];
        return buf;
      });
    } else {
      Byte[] array = (Byte[]) value;
      byte[] buf = new byte[1];
      return convertArray(field, i -> {
        buf[field.isLittleEndian() ? 0 : array.length - 1] = array[i] != null ? array[i] : 0;
        return buf;
      });
    }
  }

  /**
   * 转换布尔数组类型
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的字节数组
   */
  public byte[] convertShortArray(StructField field, Object value) {
    if (value.getClass() == short[].class) {
      short[] array = (short[]) value;
      return convertArray(field, i -> getBinary().shortToBytes(array[i]));
    } else {
      Short[] array = (Short[]) value;
      return convertArray(field, i -> array[i] != null ? getBinary().shortToBytes(array[i]) : null);
    }
  }

  /**
   * 转换整型数组
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的字节数组
   */
  public byte[] convertIntegerArray(StructField field, Object value) {
    if (value.getClass() == int[].class) {
      int[] array = (int[]) value;
      return convertArray(field, i -> getBinary().intToBytes(array[i]));
    } else {
      Integer[] array = (Integer[]) value;
      return convertArray(field, i -> array[i] != null ? getBinary().intToBytes(array[i]) : null);
    }
  }

  /**
   * 转换长整型数组
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的字节数组
   */
  public byte[] convertLongArray(StructField field, Object value) {
    if (value.getClass() == long[].class) {
      long[] array = (long[]) value;
      return convertArray(field, i -> getBinary().longToBytes(array[i]));
    } else {
      Long[] array = (Long[]) value;
      return convertArray(field, i -> array[i] != null ? getBinary().longToBytes(array[i]) : null);
    }
  }

  /**
   * 转换单精度浮点数数组
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的字节数组
   */
  public byte[] convertFloatArray(StructField field, Object value) {
    if (value.getClass() == float[].class) {
      float[] array = (float[]) value;
      return convertArray(field, i -> getBinary().intToBytes(Float.floatToIntBits(array[i])));
    } else {
      Float[] array = (Float[]) value;
      return convertArray(field, i ->
          array[i] != null ? getBinary().intToBytes(Float.floatToIntBits(array[i])) : null);
    }
  }

  /**
   * 转换双精度浮点数数组
   *
   * @param field 字段信息
   * @param value 值
   * @return 返回转换后的字节数组
   */
  public byte[] convertDoubleArray(StructField field, Object value) {
    if (value.getClass() == double[].class) {
      double[] array = (double[]) value;
      return convertArray(field, i -> getBinary().longToBytes(Double.doubleToLongBits(array[i])));
    } else {
      Double[] array = (Double[]) value;
      return convertArray(field, i ->
          array[i] != null ? getBinary().longToBytes(Double.doubleToLongBits(array[i])) : null);
    }
  }

  /**
   * 转换数组
   *
   * @param field 字段信息
   * @param func  数组处理的函数
   * @return 返回转换后的字节数组
   */
  public byte[] convertArray(StructField field, ArrayConverterFunction func) {
    int ratio = field.getFieldSize();
    byte[] buf = getCache(field.size());
    int arrayLength = field.getArrayLength();
    for (int i = 0, len; i < arrayLength; i++) {
      byte[] bytes = func.apply(i);
      if (bytes != null) {
        len = Math.min(bytes.length, ratio);
        if (field.isLittleEndian()) {
          copy(bytes, 0, buf, i * ratio, len);
        } else {
          copy(bytes, srcPos(bytes, ratio), buf, destPos(bytes, ratio) + i * ratio, len);
        }
      }
    }
    return buf;
  }

  public int srcPos(byte[] src, int ratio) {
    return src.length >= ratio ? src.length - ratio : 0;
  }

  public int destPos(byte[] dest, int ratio) {
    return dest.length >= ratio ? 0 : ratio - dest.length;
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
        return field.isLittleEndian() ? data[position] : data[position + field.getFieldSize() - 1];
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
   * 解析短整数
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的短整数
   */
  public short parseShort(StructField field, byte[] data, int position, boolean signed) {
    return getBinary().bytesToShort(copy(data, position, field.size()), field.getByteOrder(), signed);
  }

  /**
   * 解析整数
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的整数
   */
  public int parseInt(StructField field, byte[] data, int position, boolean signed) {
    return getBinary().bytesToInt(copy(data, position, field.size()), field.getByteOrder(), signed);
  }

  /**
   * 解析长整数
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的长整数
   */
  public long parseLong(StructField field, byte[] data, int position, boolean signed) {
    return getBinary().bytesToLong(copy(data, position, field.size()), field.getByteOrder(), signed);
  }

  /**
   * 解析单精度浮点数
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
   * 解析双精度浮点数
   *
   * @param field    字段
   * @param data     数据
   * @param position 开始的位置
   * @param signed   是否有符号
   * @return 返回转换的双精度浮点数
   */
  public double parseDouble(StructField field, byte[] data, int position, boolean signed) {
    return Double.longBitsToDouble(parseLong(field, data, position, signed));
  }

  /**
   * 解析字节数组
   *
   * @param field 字段
   * @param data  数据
   * @param start 开始的位置
   * @return 返回解析后的数组
   */
  public Object parseByteArray(StructField field, byte[] data, int start) {
    if (field.getType() == byte[].class) {
      byte[] array = getCache(field.getArrayLength(), false);
      return parseArray(field, data, start, array, (arr, index, buf) -> arr[index] = buf[0]);
    } else {
      Byte[] array = new Byte[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf) -> arr[index] = buf[0]);
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
    if (field.getType() == short[].class) {
      short[] array = new short[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = getBinary().bytesToShort(buf, field.getByteOrder()));
    } else {
      Short[] array = new Short[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = getBinary().bytesToShort(buf, field.getByteOrder()));
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
    if (field.getType() == int[].class) {
      int[] array = new int[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = getBinary().bytesToInt(buf, field.getByteOrder()));
    } else {
      Integer[] array = new Integer[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = getBinary().bytesToInt(buf, field.getByteOrder()));
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
    if (field.getType() == long[].class) {
      long[] array = new long[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = getBinary().bytesToLong(buf, field.getByteOrder()));
    } else {
      Long[] array = new Long[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = getBinary().bytesToLong(buf, field.getByteOrder()));
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
    if (field.getType() == float[].class) {
      float[] array = new float[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = Float.intBitsToFloat(getBinary().bytesToInt(buf, field.getByteOrder())));
    } else {
      Float[] array = new Float[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = Float.intBitsToFloat(getBinary().bytesToInt(buf, field.getByteOrder())));
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
    if (field.getType() == double[].class) {
      double[] array = new double[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = Double.longBitsToDouble(getBinary().bytesToLong(buf, field.getByteOrder())));
    } else {
      Double[] array = new Double[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = Double.longBitsToDouble(getBinary().bytesToLong(buf, field.getByteOrder())));
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
    if (field.getType() == boolean[].class) {
      boolean[] array = new boolean[field.getArrayLength()];
      return parseArray(field, data, start, array, (arr, index, buf)
          -> arr[index] = binary.bytesToShort(buf) > 0);
    } else {
      Boolean[] array = new Boolean[field.getArrayLength()];
      return parseArray(field, data, start, array
          , (arr, index, buf) -> arr[index] = binary.bytesToShort(buf) > 0);
    }
  }

  public <R> R parseArray(StructField field, byte[] data, int start, R array, ArrayParserFunction<R> func) {
    int ratio = field.getFieldSize();
    byte[] buf = getCache(ratio);
    int length = field.getArrayLength();
    for (int i = 0; i < length; i++) {
      copy(data, start + i * ratio, buf, 0, ratio);
      func.accept(array, i, buf);
    }
    return array;
  }

  public interface ArrayParserFunction<T> {

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
    byte[] apply(int index);
  }

}
