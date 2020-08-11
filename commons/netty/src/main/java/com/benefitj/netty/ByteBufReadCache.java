package com.benefitj.netty;

import io.netty.buffer.ByteBuf;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 读取 ByteBuf
 */
public class ByteBufReadCache {

  private static final Function<Integer, byte[]> CREATOR = byte[]::new;

  private final ThreadLocal<Map<Integer, byte[]>> byteCache = ThreadLocal.withInitial(WeakHashMap::new);

  public ThreadLocal<Map<Integer, byte[]>> getCacheLocal() {
    return byteCache;
  }

  public Map<Integer, byte[]> getByteCache() {
    return getCacheLocal().get();
  }

  /**
   * 获取缓冲
   *
   * @param size  缓冲区大小
   * @param local 是否使用本地缓冲
   * @return 返回缓存的字节数组
   */
  public byte[] getBuff(int size, boolean local) {
    if (local) {
      return getByteCache().computeIfAbsent(size, CREATOR);
    }
    return new byte[size];
  }

  /**
   * 获取缓冲
   *
   * @param size 缓冲区大小
   * @return 返回缓存的字节数组
   */
  public byte[] getBuff(int size) {
    return getBuff(size, true);
  }

  /**
   * 读取数据
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] read(ByteBuf data) {
    return read(data, true);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  public byte[] read(ByteBuf data, boolean local) {
    return read(data, local, false);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] readReset(ByteBuf data) {
    return read(data, true, true);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @param reset 是否重置读取位置
   * @return 返回读取的数据
   */
  public byte[] read(ByteBuf data, boolean local, boolean reset) {
    return read(data, data.readableBytes(), local, reset);
  }

  /**
   * 读取数据，并重置读取位置
   *
   * @param data  数据
   * @param size  缓冲区大小
   * @param local 是否使用本地缓冲
   * @param reset 是否重置读取位置
   * @return 返回读取的字节
   */
  public byte[] read(ByteBuf data, int size, boolean local, boolean reset) {
    byte[] buff = getBuff(size, local);
    if (reset) {
      data.markReaderIndex();
      data.readBytes(buff);
      data.resetReaderIndex();
    } else {
      data.readBytes(buff);
    }
    return buff;
  }


}
