package com.benefitj.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 字节拷贝
 */
public interface ByteArrayCopy extends ArrayCopy<byte[]> {

  SingletonSupplier<ByteArrayCopy> single = SingletonSupplier.of(ByteArrayCopy::newBufCopy);

  static ByteArrayCopy get() {
    return single.get();
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
    int length = list.stream()
        .mapToInt(a -> a.length)
        .sum();
    try (final ByteArrayOutputStream os = new ByteArrayOutputStream(length);) {
      for (byte[] bytes : list) {
        os.write(bytes);
      }
      return os.toByteArray();
    } catch (IOException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 创建缓冲拷贝
   */
  static ByteArrayCopy newBufCopy() {
    return new BufCopy(byte[]::new, true, 0x00);
  }

  class BufCopy extends SimpleBufCopy<byte[]> implements ByteArrayCopy {

    public BufCopy(Function<Integer, byte[]> creator, boolean fill, Object fillValue) {
      super(creator, fill, fillValue);
    }
  }

}
