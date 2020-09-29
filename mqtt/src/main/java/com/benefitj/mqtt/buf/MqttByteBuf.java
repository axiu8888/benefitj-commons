package com.benefitj.mqtt.buf;

import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;

public class MqttByteBuf extends ByteArrayBuf {

  public MqttByteBuf() {
  }

  public MqttByteBuf(int capacity) {
    super(capacity);
  }

  public MqttByteBuf(byte[] array) {
    super(array);
  }

  public MqttByteBuf(byte[] array, int readerIndex, int writerIndex) {
    super(array, readerIndex, writerIndex);
  }

  /**
   * 写入UTF-8编码的字符串
   *
   * @param s 数据
   * @return 返回字节缓冲
   */
  public ByteArrayBuf put(String s) {
    return put(s, null);
  }

  /**
   * 写入UTF-8编码的字符串
   *
   * @param str          数据
   * @param defaultValue 默认值
   * @return 返回字节缓冲
   */
  public ByteArrayBuf put(String str, String defaultValue) {
    str = isNotBlank(defaultValue) && isBlank(str) ? defaultValue : str;
    return put(isNotEmpty(str) ? str.getBytes(StandardCharsets.UTF_8) : null, true);
  }

  /**
   * 写入数据
   *
   * @param data      数据
   * @param hasLength 是否有长度
   * @return 返回字节缓冲
   */
  public ByteArrayBuf put(byte[] data, boolean hasLength) {
    if (data != null) {
      this.writeShort((short) data.length);
      this.write(data);
    } else {
      if (hasLength) {
        this.writeShort((short) 0);
      }
    }
    return this;
  }


  /**
   * 是否为空或null
   */
  public static boolean isBlank(CharSequence cs) {
    return StringUtils.isBlank(cs);
  }

  /**
   * 是否不为空或空字符串
   */
  public static boolean isNotBlank(CharSequence cs) {
    return StringUtils.isNotBlank(cs);
  }

  /**
   * 是否为空
   */
  public static boolean isEmpty(CharSequence cs) {
    return StringUtils.isEmpty(cs);
  }

  /**
   * 是否不为空
   */
  public static boolean isNotEmpty(CharSequence cs) {
    return StringUtils.isNotEmpty(cs);
  }

}
