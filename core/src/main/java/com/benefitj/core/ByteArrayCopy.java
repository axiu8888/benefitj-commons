package com.benefitj.core;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 字节拷贝
 */
public interface ByteArrayCopy extends ArrayCopy<byte[]> {

  SingletonSupplier<ByteArrayCopy> singleton = SingletonSupplier.of(ByteArrayCopy::newBufCopy);

  static ByteArrayCopy get() {
    return singleton.get();
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
    return ArrayCopy.concat(list);
  }

  /**
   * 创建字节缓冲拷贝
   */
  static ByteArrayCopy newBufCopy() {
    return newBufCopy(false, (byte) 0x00);
  }

  /**
   * 创建缓冲拷贝
   */
  static ByteArrayCopy newBufCopy(boolean fill, byte fillValue) {
    return new SimpleBufCopy(byte[]::new, fill, fillValue);
  }

  class SimpleBufCopy extends ArrayCopy.SimpleBufCopy<byte[]> implements ByteArrayCopy {

    public SimpleBufCopy(boolean fill, Object fillValue) {
      this(byte[]::new, fill, fillValue);
    }

    public SimpleBufCopy(Function<Integer, byte[]> creator, boolean fill, Object fillValue) {
      super(creator, fill, fillValue);
    }
  }

}
