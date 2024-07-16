package com.benefitj.mqtt;


import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


/**
 * MQTT发送
 */
public interface IMqttPublisher {

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publish(String topic, String payload) {
    publish(topic, payload, 0);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publishAsync(String topic, String payload) {
    publishAsync(topic, payload, 0);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publish(String topic, byte[] payload) {
    publish(topic, payload, 0);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  default void publishAsync(String topic, byte[] payload) {
    publishAsync(topic, payload, 0);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publish(String[] topics, String payload) {
    publish(topics, payload, 0);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publishAsync(String[] topics, String payload) {
    publishAsync(topics, payload, 0);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publish(String[] topics, byte[] payload) {
    publish(topics, payload, 0);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   */
  default void publishAsync(String[] topics, byte[] payload) {
    publishAsync(topics, payload, 0);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String topic, String payload, int qos) {
    publish(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String topic, String payload, int qos) {
    publishAsync(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String topic, byte[] payload, int qos) {
    publish(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String topic, byte[] payload, int qos) {
    publishAsync(topic, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String[] topics, String payload, int qos) {
    publish(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String[] topics, String payload, int qos) {
    publishAsync(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publish(String[] topics, byte[] payload, int qos) {
    publish(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topics  主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  default void publishAsync(String[] topics, byte[] payload, int qos) {
    publishAsync(topics, payload, qos, false);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String topic, String payload, int qos, boolean retained) {
    publish(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String topic, String payload, int qos, boolean retained) {
    publishAsync(topic, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String topic, byte[] payload, int qos, boolean retained) {
    publish(topic, wrap(payload, qos, retained));
  }

  /**
   * 发送
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String topic, byte[] payload, int qos, boolean retained) {
    publishAsync(topic, wrap(payload, qos, retained));
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String[] topics, String payload, int qos, boolean retained) {
    publish(topics, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String[] topics, String payload, int qos, boolean retained) {
    publishAsync(topics, payload.getBytes(StandardCharsets.UTF_8), qos, retained);
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publish(String[] topics, byte[] payload, int qos, boolean retained) {
    publish(topics, wrap(payload, qos, retained));
  }

  /**
   * 发送
   *
   * @param topics   主题
   * @param payload  有效载荷
   * @param qos      服务质量
   * @param retained 是否保留
   */
  default void publishAsync(String[] topics, byte[] payload, int qos, boolean retained) {
    publishAsync(topics, wrap(payload, qos, retained));
  }

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  default void publish(String topic, MqttMessage msg) {
    publish(Collections.singletonList(topic), msg);
  }

  /**
   * 发送
   *
   * @param topic 主题
   * @param msg   消息
   */
  default void publishAsync(String topic, MqttMessage msg) {
    publishAsync(Collections.singletonList(topic), msg);
  }

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  default void publish(String[] topics, MqttMessage msg) {
    publish(Arrays.asList(topics), msg);
  }

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  default void publishAsync(String[] topics, MqttMessage msg) {
    publishAsync(Arrays.asList(topics), msg);
  }

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  void publish(Collection<String> topics, MqttMessage msg);

  /**
   * 发送
   *
   * @param topics 主题
   * @param msg    消息
   */
  void publishAsync(Collection<String> topics, MqttMessage msg);

  //===================================================================================================================

  default MqttMessage wrap(byte[] payload, int qos, boolean retained) {
    return wrap(0, payload, qos, retained);
  }

  default MqttMessage wrap(int id, byte[] payload, int qos, boolean retained) {
    MqttMessage msg = new MqttMessage();
    msg.setId(id);
    msg.setPayload(payload);
    msg.setQos(qos);
    msg.setRetained(retained);
    return msg;
  }

}
