package com.benefitj.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.socket.DatagramPacket;

import java.util.Arrays;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 读取 ByteBuf
 */
public class ByteBufCopy {

  private final ThreadLocal<Map<Integer, byte[]>> bytesCache = ThreadLocal.withInitial(WeakHashMap::new);
  private final Function<Integer, byte[]> creator = byte[]::new;

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
      byte[] buff = bytesCache.get().computeIfAbsent(size, creator);
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
