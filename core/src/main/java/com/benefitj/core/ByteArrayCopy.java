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
   * 创建缓冲拷贝
   */
  static ByteArrayCopy newBufCopy() {
    return new BufCopy(byte[]::new, true, (byte) 0x00);
  }

  class BufCopy extends SimpleBufCopy<byte[]> implements ByteArrayCopy {

    public BufCopy(Function<Integer, byte[]> creator, boolean fill, Object fillValue) {
      super(creator, fill, fillValue);
    }
  }

}
