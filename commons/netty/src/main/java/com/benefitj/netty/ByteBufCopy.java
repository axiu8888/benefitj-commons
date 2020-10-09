package com.benefitj.netty;

import com.benefitj.core.BufCopy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

/**
 * 读取 ByteBuf
 */
public class ByteBufCopy extends BufCopy {

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
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] copy(DatagramPacket data) {
    return copy(data.content());
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
   * @return 返回读取的数据
   */
  public byte[] copy(DatagramPacket data, boolean local) {
    return copy(data.content(), local);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(ByteBuf data) {
    return copyAdnReset(data, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(DatagramPacket data) {
    return copyAdnReset(data.content());
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(ByteBuf data, int size) {
    return copyAdnReset(data, size, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data 数据
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(DatagramPacket data, int size) {
    return copyAdnReset(data.content(), size);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(ByteBuf data, boolean local) {
    return copy(data, local, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(DatagramPacket data, boolean local) {
    return copyAdnReset(data.content(), local);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(ByteBuf data, int size, boolean local) {
    return copy(data, size, local, true);
  }

  /**
   * 读取数据，并重置读取标记
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @return 返回读取的数据
   */
  public byte[] copyAdnReset(DatagramPacket data, int size, boolean local) {
    return copyAdnReset(data.content(), size, local);
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
   * 读取数据
   *
   * @param data  数据
   * @param local 是否使用本地缓存
   * @param reset 是否重置读取位置
   * @return 返回读取的数据
   */
  public byte[] copy(DatagramPacket data, boolean local, boolean reset) {
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
