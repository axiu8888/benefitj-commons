package com.benefitj.javastruct.resovler;

import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.field.PrimitiveType;
import com.benefitj.javastruct.field.StructField;

import java.lang.reflect.Field;

public interface FieldResolver<T> {

  /**
   * 是否支持的类型
   *
   * @param field 字段
   * @param jsf   字段的注解
   * @param pt    字段对应的基本类型
   * @return 返回是否支持
   */
  boolean support(Field field, JavaStructField jsf, PrimitiveType pt);

  /**
   * 解析数据
   *
   * @param field    字节
   * @param data     数据
   * @param position 下表位置
   * @return 返回解析后的对象
   */
  T resolve(StructField field, byte[] data, int position);

  /**
   * 拷贝数据
   *
   * @param src  原数组
   * @param dest 目标数组
   * @return 返回拷贝后的目标数据
   */
  default byte[] copy(byte[] src, byte[] dest) {
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
  default byte[] copy(byte[] src, byte[] dest, int len) {
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
  default byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos) {
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
  default byte[] copy(byte[] src, int srcPos, byte[] dest, int destPos, int len) {
    System.arraycopy(src, srcPos, dest, destPos, len);
    return dest;
  }
}
