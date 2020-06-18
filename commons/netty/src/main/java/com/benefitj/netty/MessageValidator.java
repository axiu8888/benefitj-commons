package com.benefitj.netty;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

/**
 * 消息验证器
 *
 * @param <T>
 */
public interface MessageValidator<T> {

  /**
   * 验证消息
   *
   * @param original 原始数据
   * @param msg      消息
   * @return 返回是否通过
   */
  boolean verify(@Nullable ByteBuf original, T msg);

}
