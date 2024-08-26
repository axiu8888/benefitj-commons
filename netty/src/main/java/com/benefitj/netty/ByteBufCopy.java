package com.benefitj.netty;

import com.benefitj.core.ByteArrayCopy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

import java.util.function.Function;

/**
 * 读取 ByteBuf
 */
public interface ByteBufCopy extends ByteArrayCopy {

  /**
   * 读取数据
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  default byte[] copy(ByteBuf data) {
    return copy(data, true);
  }

  /**
   * 读取数据
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  default byte[] copy(DatagramPacket data) {
    return copy(data.content());
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] copy(ByteBuf data, boolean local) {
    return copy(data, local, false);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] copy(DatagramPacket data, boolean local) {
    return copy(data.content(), local);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(ByteBuf data) {
    return copyAndReset(data, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(DatagramPacket data) {
    return copyAndReset(data.content());
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(ByteBuf data, int size) {
    return copyAndReset(data, size, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(DatagramPacket data, int size) {
    return copyAndReset(data.content(), size);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(ByteBuf data, boolean local) {
    return copy(data, local, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(DatagramPacket data, boolean local) {
    return copyAndReset(data.content(), local);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(ByteBuf data, int size, boolean local) {
    return copy(data, size, local, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] copyAndReset(DatagramPacket data, int size, boolean local) {
    return copyAndReset(data.content(), size, local);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] minAndReset(DatagramPacket data, int min, boolean local) {
    final ByteBuf content = data.content();
    return copy(content, Math.min(min, content.readableBytes()), local, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  default byte[] minAndReset(ByteBuf data, int min, boolean local) {
    return copy(data, Math.min(min, data.readableBytes()), local, true);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @param reset 是否重置读取位置
   * @return 返回读取的数据
   */
  default byte[] copy(ByteBuf data, boolean local, boolean reset) {
    return copy(data, data.readableBytes(), local, reset);
  }

  /**
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @param reset 是否重置读取位置
   * @return 返回读取的数据
   */
  default byte[] copy(DatagramPacket data, boolean local, boolean reset) {
    final ByteBuf bb = data.content();
    return copy(bb, bb.readableBytes(), local, reset);
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
  default byte[] copy(ByteBuf data, int size, boolean local, boolean reset) {
    byte[] buf = getCache(size, local);
    if (reset) {
      data.markReaderIndex();
      data.readBytes(buf);
      data.resetReaderIndex();
    } else {
      data.readBytes(buf);
    }
    return buf;
  }

  /**
   * 创建字节缓冲拷贝
   */
  static ByteBufCopy newBufCopy() {
    return newBufCopy(false, (byte) 0x00);
  }

  /**
   * 创建字节缓冲拷贝
   */
  static ByteBufCopy newBufCopy(boolean fill, byte fillValue) {
    return new SimpleBufCopy(byte[]::new, fill, fillValue);
  }

  class SimpleBufCopy extends ByteArrayCopy.SimpleBufCopy implements ByteBufCopy {

    public SimpleBufCopy(boolean fill, Object fillValue) {
      this(byte[]::new, fill, fillValue);
    }

    public SimpleBufCopy(Function<Integer, byte[]> creator, boolean fill, Object fillValue) {
      super(creator, fill, fillValue);
    }

  }

}
