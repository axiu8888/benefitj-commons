package com.benefitj.netty.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 具有本地缓存的 Handler
 *
 * @param <T>
 */
public abstract class LocalCacheChannelInboundHandler<T> extends SimpleChannelInboundHandler<T> {

  private static final Function<Integer, byte[]> CREATOR = byte[]::new;

  private final ThreadLocal<Map<Integer, byte[]>> byteCache = ThreadLocal.withInitial(WeakHashMap::new);

  public LocalCacheChannelInboundHandler() {
  }

  public LocalCacheChannelInboundHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public LocalCacheChannelInboundHandler(Class<T> inboundMessageType) {
    super(inboundMessageType);
  }

  public LocalCacheChannelInboundHandler(Class<T> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
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
      return byteCache.get().computeIfAbsent(size, CREATOR);
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
