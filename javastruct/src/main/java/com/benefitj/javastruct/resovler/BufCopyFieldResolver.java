package com.benefitj.javastruct.resovler;

import com.benefitj.core.BufCopy;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;

/**
 * 具有数据缓冲的解析器
 *
 * @param <T>
 */
public abstract class BufCopyFieldResolver<T> implements FieldResolver<T> {

  /**
   * 缓冲
   */
  private final BufCopy bufCopy = BufCopy.newBufCopy();
  /**
   * 是否优先使用本地缓冲
   */
  private boolean local = true;

  public BufCopyFieldResolver() {
  }

  public BufCopyFieldResolver(boolean local) {
    this.local = local;
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
   * @param field 类字段信息
   * @param value 字段值
   * @return 返回转换后的字节数组
   */
  @Override
  public abstract byte[] convert(StructField field, Object value);

  public BufCopy getBufCopy() {
    return bufCopy;
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
    return bufCopy.getCache(size, local);
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
    copy(src, srcPos, buf, 0, len);
    return buf;
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
