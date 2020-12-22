//package com.benefitj.netty.handler;
//
//import com.benefitj.netty.ByteBufCopy;
//import io.netty.buffer.ByteBuf;
//
//public interface ByteBufCopyHandler {
//
//  /**
//   * 字节缓冲
//   */
//  ByteBufCopy getBufCopy();
//
//  /**
//   * 获取缓冲
//   *
//   * @param size  缓冲区大小
//   * @param local 是否使用本地缓冲
//   * @return 返回缓存的字节数组
//   */
//  default byte[] getCache(int size, boolean local) {
//    return getBufCopy().getCache(size, local);
//  }
//
//  /**
//   * 获取缓冲
//   *
//   * @param size 缓冲区大小
//   * @return 返回缓存的字节数组
//   */
//  default byte[] getCache(int size) {
//    return getCache(size, true);
//  }
//
//  /**
//   * 读取数据
//   *
//   * @param data 数据
//   * @return 返回读取的数据
//   */
//  default byte[] copy(ByteBuf data) {
//    return copy(data, true);
//  }
//
//  /**
//   * 读取数据
//   *
//   * @param data  数据
//   * @param local 是否使用本地缓存
//   * @return 返回读取的数据
//   */
//  default byte[] copy(ByteBuf data, boolean local) {
//    return copy(data, local, false);
//  }
//
//  /**
//   * 读取数据
//   *
//   * @param data  数据
//   * @param local 是否使用本地缓存
//   * @param reset 是否重置读取位置
//   * @return 返回读取的数据
//   */
//  default byte[] copy(ByteBuf data, boolean local, boolean reset) {
//    return copy(data, data.readableBytes(), local, reset);
//  }
//
//  /**
//   * 读取数据，并重置读取位置
//   *
//   * @param data  数据
//   * @param size  缓冲区大小
//   * @param local 是否使用本地缓冲
//   * @param reset 是否重置读取位置
//   * @return 返回读取的字节
//   */
//  default byte[] copy(ByteBuf data, int size, boolean local, boolean reset) {
//    return getBufCopy().copy(data, size, local, reset);
//  }
//
//  /**
//   * 读取数据，并重置读取位置
//   *
//   * @param data  数据
//   * @param local 是否使用本地缓冲
//   * @return 返回读取的字节
//   */
//  default byte[] copyAndReset(ByteBuf data, boolean local) {
//    return copy(data, data.readableBytes(), local, true);
//  }
//
//  /**
//   * 读取数据，并重置读取位置
//   *
//   * @param data  数据
//   * @param size  缓冲区大小
//   * @param local 是否使用本地缓冲
//   * @return 返回读取的字节
//   */
//  default byte[] copyAndReset(ByteBuf data, int size, boolean local) {
//    return copy(data, size, local, true);
//  }
//
//  /**
//   * 读取数据，并重置读取位置
//   *
//   * @param data  数据
//   * @param size  缓冲区大小
//   * @param local 是否使用本地缓冲
//   * @return 返回读取的字节
//   */
//  default byte[] copy(byte[] data, int size, boolean local) {
//    return getBufCopy().copy(data, size, local);
//  }
//
//
//}
