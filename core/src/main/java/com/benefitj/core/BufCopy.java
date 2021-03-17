package com.benefitj.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 字节拷贝
 */
public interface BufCopy {

  /**
   * 获取缓存字节数组
   *
   * @param size  数组大小
   * @param local 是否为本地线程缓存数组
   * @return 返回字节数据
   */
  byte[] getCache(int size, boolean local);

  /**
   * 获取缓存字节数组
   *
   * @param size 数组大小
   * @return 返回字节数据
   */
  default byte[] getCache(int size) {
    return getCache(size, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @return 返回拷贝后的数据
   */
  default byte[] copy(byte[] src) {
    return copy(src, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @return 返回拷贝后的数据
   */
  default byte[] copy(byte[] src, boolean local) {
    return copy(src, 0, src.length, local);
  }

  /**
   * 拷贝
   *
   * @param src   元数据
   * @param start 开始位置
   * @param len   长度
   * @return 返回拷贝后的数据
   */
  default byte[] copy(byte[] src, int start, int len) {
    return copy(src, start, len, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @param len 长度
   * @return 返回拷贝后的数据
   */
  default byte[] copy(byte[] src, int len) {
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
  default byte[] copy(byte[] src, int len, boolean local) {
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
  default byte[] copy(byte[] src, int start, int len, boolean local) {
    byte[] dest = getCache(len, local);
    return copy(src, start, dest, 0, len);
  }

  /**
   * 拷贝
   *
   * @param src  原数据
   * @param dest 目标数据
   * @return 返回拷贝后的数据
   */
  default byte[] copy(byte[] src, byte[] dest) {
    return copy(src, 0, dest, 0, Math.min(src.length, dest.length));
  }

  /**
   * 拷贝
   *
   * @param src  原数据
   * @param dest 目标数据
   * @return 返回拷贝后的数据
   */
  default byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos) {
    return copy(src, srcPos, dest, destPos, Math.min(src.length - srcPos, dest.length - destPos));
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
  default byte[] copy(byte[] src, int start, byte[] dest, int destPos, int len) {
    System.arraycopy(src, start, dest, destPos, len);
    return dest;
  }

  /**
   * 拼接字节数组
   *
   * @param array 数组
   * @return 返回拼接好的数据
   */
  static byte[] concat(byte[]... array) {
    return concat(Arrays.asList(array));
  }

  /**
   * 拼接字节数组
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
      throw new IllegalStateException(e);
    }
  }

  /**
   * 创建缓冲拷贝
   */
  static BufCopy newBufCopy() {
    return new SimpleBufCopy();
  }

  class SimpleBufCopy implements BufCopy {

    private final ThreadLocal<Map<Integer, byte[]>> bytesCache = ThreadLocal.withInitial(WeakHashMap::new);
    private final Function<Integer, byte[]> creator = byte[]::new;

    /**
     * 获取缓存字节数组
     *
     * @param size  数组大小
     * @param local 是否为本地线程缓存数组
     * @return 返回字节数据
     */
    @Override
    public byte[] getCache(int size, boolean local) {
      if (local) {
        byte[] buff = bytesCache.get().computeIfAbsent(size, creator);
        Arrays.fill(buff, (byte) 0x00);
        return buff;
      }
      return new byte[size];
    }

  }

}
