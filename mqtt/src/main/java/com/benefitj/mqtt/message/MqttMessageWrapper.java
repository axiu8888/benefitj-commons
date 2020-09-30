package com.benefitj.mqtt.message;

import com.benefitj.mqtt.MqttUtils;

/**
 * 控制报文包装器
 */
public interface MqttMessageWrapper<T extends MqttMessage> {

  /**
   * 原始报文数据
   */
  byte[] getRaw();

  /**
   * 设置原始报文数据
   *
   * @param raw 数据
   */
  void setRaw(byte[] raw);

  /**
   * 获取报文
   */
  T getMessage();

  /**
   * 设置报文
   *
   * @param message 报文
   */
  void setMessage(T message);

  /**
   * 获取控制报文类型
   */
  MqttMessageType getMessageType();

  /**
   * 设置控制报文类型
   *
   * @param messageType 报文类型
   */
  void setMessageType(MqttMessageType messageType);

  /**
   * 获取标志位
   */
  byte getFlags();

  /**
   * 设置标志位
   *
   * @param flags 标志位
   */
  void setFlags(byte flags);

  /**
   * 获取剩余长度
   */
  int getRemainingLength();

//  /**
//   * 控制报文的重复分发标志
//   */
//  boolean isDUP();
//
//  /**
//   * 报文的服务质量等级
//   */
//  byte getQoS();

  /**
   * 剩余长度解码
   *
   * @param raw 数据
   * @return 返回剩余长度
   */
  default int getRemainingLength(byte[] raw) {
    return getRemainingLength(raw, 0);
  }

  /**
   * 剩余长度解码
   *
   * @param raw   数据
   * @param start 开始的位置
   * @return 返回剩余长度
   */
  default int getRemainingLength(byte[] raw, int start) {
    return MqttUtils.remainingLengthDecode(raw, start);
  }

}
