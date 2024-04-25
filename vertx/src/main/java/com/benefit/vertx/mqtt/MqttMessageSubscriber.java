package com.benefit.vertx.mqtt;


/**
 * 消息处理
 */
public interface MqttMessageSubscriber<T> {

  /**
   * 接收消息
   *
   * @param topicName topic
   * @param message   MQTT消息
   */
  void onMessage(String topicName, T message);

}
