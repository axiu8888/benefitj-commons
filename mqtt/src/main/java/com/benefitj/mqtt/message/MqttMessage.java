package com.benefitj.mqtt.message;

/**
 * 控制报文
 */
public interface MqttMessage {

  /**
   * 获取控制报文的类型
   */
  MqttMessageType getMessageType();

}
