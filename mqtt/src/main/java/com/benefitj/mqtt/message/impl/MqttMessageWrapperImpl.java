package com.benefitj.mqtt.message.impl;

import com.benefitj.mqtt.message.MqttMessage;
import com.benefitj.mqtt.message.MqttMessageType;
import com.benefitj.mqtt.message.MqttMessageWrapper;

/**
 * 控制报文包装器实现
 *
 * @param <T>
 */
public class MqttMessageWrapperImpl<T extends MqttMessage> implements MqttMessageWrapper<T> {

  /**
   * 原始包文
   */
  private byte[] raw;
  /**
   * 报文实体
   */
  private T packet;
  /**
   * 控制报文类型
   */
  private MqttMessageType packetType;
  /**
   * 标志位
   */
  private byte flags;

  public MqttMessageWrapperImpl(byte[] raw, T packet) {
    this.raw = raw;
    this.packet = packet;
  }

  /**
   * 原始报文数据
   */
  @Override
  public byte[] getRaw() {
    return this.raw;
  }

  /**
   * 设置原始报文数据
   *
   * @param raw 数据
   */
  @Override
  public void setRaw(byte[] raw) {
    this.raw = raw;
  }

  /**
   * 获取报文
   */
  @Override
  public T getMessage() {
    return this.packet;
  }

  /**
   * 设置报文
   *
   * @param message 报文
   */
  @Override
  public void setMessage(T message) {
    this.packet = message;
  }

  /**
   * 获取控制报文类型
   */
  @Override
  public MqttMessageType getMessageType() {
    return this.packetType;
  }

  /**
   * 设置控制报文类型
   *
   * @param messageType 报文类型
   */
  @Override
  public void setMessageType(MqttMessageType messageType) {
    this.packetType = messageType;
  }

  /**
   * 获取标志位
   */
  @Override
  public byte getFlags() {
    return this.flags;
  }

  /**
   * 设置标志位
   *
   * @param flags 标志位
   */
  @Override
  public void setFlags(byte flags) {
    this.flags = flags;
  }

  /**
   * 获取剩余长度
   */
  @Override
  public int getRemainingLength() {
    byte[] raw = getRaw();
    return getRemainingLength(raw);
  }
}
