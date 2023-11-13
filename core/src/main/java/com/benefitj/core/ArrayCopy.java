package com.benefitj.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 数组拷贝
 */
public interface ArrayCopy<T> {

  /**
   * 获取缓存数组
   *
   * @param size  数组大小
   * @param local 是否为本地线程缓存数组
   * @return 返回数组
   */
  T getCache(int size, boolean local);

  /**
   * 获取缓存数组
   *
   * @param size 数组大小
   * @return 返回数组
   */
  default T getCache(int size) {
    return getCache(size, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @return 返回拷贝后的数据
   */
  default T copy(T src) {
    return copy(src, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @return 返回拷贝后的数据
   */
  default T copy(T src, boolean local) {
    return copy(src, 0, len(src), local);
  }

  /**
   * 拷贝
   *
   * @param src   元数据
   * @param start 开始位置
   * @param len   长度
   * @return 返回拷贝后的数据
   */
  default T copy(T src, int start, int len) {
    return copy(src, start, len, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @param len 长度
   * @return 返回拷贝后的数据
   */
  default T copy(T src, int len) {
    return copy(src, len, true);
  }

  /**
   * 拷贝
   *
   * @param src   元数据
   * @param len   长度
   * @param local 是否为本地线程缓存数组
   * @return 返回拷贝后的数据
   */
  default T copy(T src, int len, boolean local) {
    return copy(src, 0, len, local);
  }

  /**
   * 拷贝
   *
   * @param src   元数据
   * @param start 开始位置
   * @param len   长度
   * @param local 是否为本地线程缓存数组
   * @return 返回拷贝后的数据
   */
  default T copy(T src, int start, int len, boolean local) {
    T dest = getCache(len, local);
    return copy(src, start, dest, 0, len);
  }

  /**
   * 拷贝
   *
   * @param src  原数据
   * @param dest 目标数据
   * @return 返回拷贝后的数据
   */
  default T copy(T src, T dest) {
    return copy(src, 0, dest, 0, Math.min(len(src), len(dest)));
  }

  /**
   * 拷贝
   *
   * @param src  原数据
   * @param dest 目标数据
   * @return 返回拷贝后的数据
   */
  default T copy(T src, int srcPos, T dest, int destPos) {
    return copy(src, srcPos, dest, destPos, Math.min(len(src) - srcPos, len(dest) - destPos));
  }

  /**
   * 拷贝
   *
   * @param src     原数据
   * @param start   开始位置
   * @param dest    目标数据
   * @param destPos 目标开始的位置
   * @param len     长度
   * @return 返回拷贝后的数据
   */
  default T copy(T src, int start, T dest, int destPos, int len) {
    System.arraycopy(src, start, dest, destPos, len);
    return dest;
  }

  /**
   * 拼接数组
   *
   * @param array 数组
   * @return 返回拼接好的数据
   */
  static byte[] concat(byte[]... array) {
    return concat(Arrays.asList(array));
  }

  /**
   * 拼接数组
   *
   * @param list 字节列表
   * @return 返回拼接好的数据
   */
  static byte[] concat(List<byte[]> list) {
    int length = list.stream()
        .mapToInt(a -> a.length)
        .sum();
    try (final ByteArrayOutputStream os = new ByteArrayOutputStream(length);) {
      for (byte[] bytes : list) {
        os.write(bytes);
      }
      return os.toByteArray();
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  static ArrayCopy<byte[]> newByteArrayCopy() {
    return new SimpleBufCopy<>(byte[]::new, false, 0);
  }

  static ArrayCopy<short[]> newShortArrayCopy() {
    return new SimpleBufCopy<>(short[]::new, false, 0);
  }

  static ArrayCopy<int[]> newIntArrayCopy() {
    return new SimpleBufCopy<>(int[]::new, false, 0);
  }

  static ArrayCopy<long[]> newLongArrayCopy() {
    return new SimpleBufCopy<>(long[]::new, false, 0);
  }

  static ArrayCopy<float[]> newFloatArrayCopy() {
    return new SimpleBufCopy<>(float[]::new, false, 0);
  }

  static ArrayCopy<double[]> newDoubleArrayCopy() {
    return new SimpleBufCopy<>(double[]::new, false, 0);
  }

  class SimpleBufCopy<T> implements ArrayCopy<T> {

    private ThreadLocal<Map<Integer, T>> bytesCache = ThreadLocal.withInitial(WeakHashMap::new);
    private Function<Integer, T> creator;

    private boolean fill;
    private Object fillValue;


    public SimpleBufCopy(Function<Integer, T> creator, boolean fill, Object fillValue) {
      this.creator = creator;
      this.fill = fill;
      this.fillValue = fillValue;
    }

    /**
     * 获取缓存数组
     *
     * @param size  数组大小
     * @param local 是否为本地线程缓存数组
     * @return 返回数组
     */
    @Override
    public T getCache(int size, boolean local) {
      T buf;
      if (local) {
        buf = bytesCache.get().computeIfAbsent(size, creator);
      } else {
        buf = creator.apply(size);
      }
      if (fill) {
        Arrays.fill((Object[]) buf, fillValue);
      }
      return buf;
    }

  }

  static int len(Object src) {
    return Array.getLength(src);
  }

}
