package com.benefitj.netty.adapter;

import com.benefitj.netty.ByteBufCopy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 具有本地缓存的 Handler
 *
 * @param <T>
 */
public abstract class ByteBufCopyChannelInboundHandler<T> extends SimpleChannelInboundHandler<T> {

  private final ByteBufCopy byteBufCopy = new ByteBufCopy();

  public ByteBufCopyChannelInboundHandler() {
  }

  public ByteBufCopyChannelInboundHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public ByteBufCopyChannelInboundHandler(Class<T> inboundMessageType) {
    super(inboundMessageType);
  }

  public ByteBufCopyChannelInboundHandler(Class<T> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
  }

  /**
   * 获取缓冲
   *
   * @param size  缓冲区大小
   * @param local 是否使用本地缓冲
   * @return 返回缓存的字节数组
   */
  public byte[] getCache(int size, boolean local) {
    return byteBufCopy.getCache(size, local);
  }

  /**
   * 获取缓冲
   *
   * @param size 缓冲区大小
   * @return 返回缓存的字节数组
   */
  public byte[] getCache(int size) {
    return getCache(size, true);
  }

  /**
   * 读取数据
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] copy(ByteBuf data) {
    return copy(data, true);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  public byte[] copy(ByteBuf data, boolean local) {
    return copy(data, local, false);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @param reset 是否重置读取位置
   * @return 返回读取的数据
   */
  public byte[] copy(ByteBuf data, boolean local, boolean reset) {
    return copy(data, data.readableBytes(), local, reset);
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
  public byte[] copy(ByteBuf data, int size, boolean local, boolean reset) {
    byte[] buff = getCache(size, local);
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