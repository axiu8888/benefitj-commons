package com.benefitj.javastruct.convert;

import com.benefitj.core.BinaryHelper;
import com.benefitj.core.ByteArrayCopy;
import com.benefitj.javastruct.JavaStructField;
import com.benefitj.javastruct.PrimitiveType;
import com.benefitj.javastruct.StructField;

import java.lang.reflect.Field;

/**
 * 具有数据缓冲的转换器
 *
 * @param <T>
 */
public abstract class BufCopyConverter<T> implements Converter<T> {
  /**
   * 二进制工具
   */
  protected final BinaryHelper binary = BinaryHelper.BIG_ENDIAN;
  /**
   * 缓冲
   */
  private final ByteArrayCopy byteArrayCopy = ByteArrayCopy.newBufCopy();
  /**
   * 是否优先使用本地缓冲
   */
  private boolean local = true;

  public BufCopyConverter() {
  }

  public BufCopyConverter(boolean local) {
    this.local = local;
  }

  public BinaryHelper getBinary() {
    return binary;
  }

  /**
   * 是否支持的类型
   *
   * @param field 字段
   * @param jsf   字段的注解
   * @param pt    字段对应的基本类型
   * @return 返回是否支持
   */
  @Override
  public abstract boolean support(Field field, JavaStructField jsf, PrimitiveType pt);

  /**
   * 转换数据
   *
   * @param obj   对象
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  @Override
  public abstract byte[] convert(Object obj, StructField field, Object value);

  public ByteArrayCopy getBufCopy() {
    return byteArrayCopy;
  }

  public boolean isLocal() {
    return local;
  }

  public void setLocal(boolean local) {
    this.local = local;
  }

  public byte[] getCache(int size) {
    return getCache(size, isLocal());
  }

  public byte[] getCache(int size, boolean local) {
    return byteArrayCopy.getCache(size, local);
  }

  /**
   * 拷贝
   *
   * @param src    原数据
   * @param srcPos 原数据开始的位置
   * @return 返回拷贝的数据
   */
  public byte[] copy(byte[] src, int srcPos) {
    return copy(src, srcPos, src.length - srcPos);
  }

  /**
   * 拷贝
   *
   * @param src    原数据
   * @param srcPos 原数据开始的位置
   * @param len    长度
   * @return 返回拷贝的数据
   */
  public byte[] copy(byte[] src, int srcPos, int len) {
    byte[] buf = getCache(len);
    return copy(src, srcPos, buf, 0, len);
  }

  /**
   * 拷贝数据
   *
   * @param src  原数组
   * @param dest 目标数组
   * @return 返回拷贝后的目标数据
   */
  public byte[] copy(byte[] src, byte[] dest) {
    return copy(src, 0, dest, 0, Math.min(src.length, dest.length));
  }

  /**
   * 拷贝数据
   *
   * @param src  原数组
   * @param dest 目标数组
   * @param len  长度
   * @return 返回拷贝后的目标数据
   */
  public byte[] copy(byte[] src, byte[] dest, int len) {
    return copy(src, 0, dest, 0, len);
  }

  /**
   * 拷贝数据
   *
   * @param src     原数组
   * @param srcPos  原数据开始的位置
   * @param dest    目标数组
   * @param destPos 目标数据开始的位置
   * @return 返回拷贝后的目标数据
   */
  public byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos) {
    return copy(src, srcPos, dest, destPos, Math.min(src.length - srcPos, dest.length - destPos));
  }

  /**
   * 拷贝数据
   *
   * @param src     原数组
   * @param srcPos  原数据开始的位置
   * @param dest    目标数组
   * @param destPos 目标数据开始的位置
   * @param len     长度
   * @return 返回拷贝后的目标数据
   */
  public byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
    System.arraycopy(src, srcPos, dest, destPos, len);
    return dest;
  }
}
