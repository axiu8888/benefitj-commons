package com.benefitj.javastruct.resovler;

import com.benefitj.core.BufCopy;
import com.benefitj.core.HexUtils;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;
import java.nio.charset.Charset;

/**
 * 默认的基本数据类型解析器
 *
 * @author DINGXIUAN
 */
public class DefaultPrimitiveFieldResolver implements PrimitiveFieldResolver<Object> {

  private final BufCopy bufCopy = BufCopy.newBufCopy();
  private boolean local = false;

  public DefaultPrimitiveFieldResolver() {
  }

  public DefaultPrimitiveFieldResolver(boolean local) {
    this.local = local;
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

  /**
   * 解析数据
   *
   * @param field    字节
   * @param data     数据
   * @param position 下表位置
   * @return 返回解析后的对象
   */
  @Override
  public Object resolve(StructField field, byte[] data, int position) {
    int size = field.size();
    if (field.isArray()) {
      // 处理数组
      Class<?> type = field.getField().getType();
      if (type == byte[].class || type == Byte.class) {
        if (type == byte[].class) {
          return copy(data, position, getByteBuf(size, false), 0);
        } else {
          Byte[] buf = new Byte[size];
          for (int i = 0; i < buf.length; i++) {
            buf[i] = data[position + i];
          }
          return buf;
        }
      } else if (type == short[].class || type == Short[].class) {
        int ratio = 2;
        if (type == short[].class) {
          short[] array = new short[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf, field.getByteOrder()));
        } else {
          Short[] array = new Short[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf, field.getByteOrder()));
        }
      } else if (type == int[].class || type == Integer[].class) {
        int ratio = 4;
        if (type == int[].class) {
          int[] array = new int[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToInt(buf, field.getByteOrder()));
        } else {
          Integer[] array = new Integer[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToInt(buf, field.getByteOrder()));
        }
      } else if (type == long[].class || type == Long[].class) {
        int ratio = 8;
        if (type == long[].class) {
          long[] array = new long[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToLong(buf, field.getByteOrder()));
        } else {
          Long[] array = new Long[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToLong(buf, field.getByteOrder()));
        }
      } else if (type == float[].class || type == Float[].class) {
        int ratio = 4;
        if (type == float[].class) {
          float[] array = new float[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = Float.intBitsToFloat(HexUtils.bytesToInt(buf, field.getByteOrder())));
        } else {
          Float[] array = new Float[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = Float.intBitsToFloat(HexUtils.bytesToInt(buf, field.getByteOrder())));
        }
      } else if (type == double[].class || type == Double[].class) {
        int ratio = 8;
        if (type == double[].class) {
          double[] array = new double[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = Double.longBitsToDouble(HexUtils.bytesToLong(buf, field.getByteOrder())));
        } else {
          Double[] array = new Double[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = Double.longBitsToDouble(HexUtils.bytesToLong(buf, field.getByteOrder())));
        }
      } else if (type == boolean[].class || type == Boolean[].class) {
        int ratio = 8;
        if (type == boolean[].class) {
          boolean[] array = new boolean[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf) > 0);
        } else {
          Boolean[] array = new Boolean[size / ratio];
          return resolveArray(data, position, array, array.length, ratio
              , (arr, index, buf) -> arr[index] = HexUtils.bytesToShort(buf) > 0);
        }
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
        byte[] buf = getByteBuf(size);
        copy(data, position, buf, 0);
        return new String(buf, Charset.forName(field.getCharset())).trim();
      }
    }

    // ~bang
    throw new UnsupportedOperationException();
  }

  public <T> T resolveArray(byte[] data, int start, T array, int arrayLength, int ratio, ArrayFunction<T> func) {
    byte[] buf = getByteBuf(ratio);
    for (int i = 0; i < arrayLength; i++) {
      copy(data, start + i * ratio, buf, 0, ratio);
      func.accept(array, i, buf);
    }
    return array;
  }

  public byte[] getByteBuf(int size) {
    return getByteBuf(size, isLocal());
  }

  @Override
  public byte[] getByteBuf(int size, boolean local) {
    return bufCopy.getCache(size, local);
  }

  public BufCopy getBufCopy() {
    return bufCopy;
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  public interface ArrayFunction<T> {

    /**
     * 处理数据
     *
     * @param array 数组
     * @param index 索引
     * @param buf   读取的缓冲
     */
    void accept(T array, int index, byte[] buf);

  }

}
