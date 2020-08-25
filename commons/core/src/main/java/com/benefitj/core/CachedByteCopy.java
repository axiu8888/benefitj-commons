package com.benefitj.core;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 字节拷贝
 */
public class CachedByteCopy {

  private final ThreadLocal<Map<Integer, byte[]>> bytesCache = ThreadLocal.withInitial(WeakHashMap::new);
  private final Function<Integer, byte[]> creator = byte[]::new;

  private byte[] getLocalBuff(int size) {
    return bytesCache.get().computeIfAbsent(size, creator);
  }

  /**
   * 获取缓存字节数组
   *
   * @param size 数组大小
   * @return 返回字节数据
   */
  public byte[] getCache(int size) {
    return getCache(size, true);
  }

  /**
   * 获取缓存字节数组
   *
   * @param size  数组大小
   * @param local 是否为本地线程缓存数组
   * @return 返回字节数据
   */
  public byte[] getCache(int size, boolean local) {
    if (local) {
      byte[] buff = getLocalBuff(size);
      Arrays.fill(buff, (byte) 0x00);
      return buff;
    }
    return new byte[size];
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @return 返回拷贝后的数据
   */
  public byte[] copy(byte[] src) {
    return copy(src, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @return 返回拷贝后的数据
   */
  public byte[] copy(byte[] src, boolean local) {
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
  public byte[] copy(byte[] src, int start, int len) {
    return copy(src, start, len, true);
  }

  /**
   * 拷贝
   *
   * @param src 元数据
   * @param len 长度
   * @return 返回拷贝后的数据
   */
  public byte[] copy(byte[] src, int len) {
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
  public byte[] copy(byte[] src, int len, boolean local) {
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
  public byte[] copy(byte[] src, int start, int len, boolean local) {
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
  public byte[] copy(byte[] src, byte[] dest) {
    return copy(src, 0, dest, 0, dest.length);
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
  public byte[] copy(byte[] src, int start, byte[] dest, int destPos, int len) {
    System.arraycopy(src, start, dest, destPos, len);
    return dest;
  }

}
