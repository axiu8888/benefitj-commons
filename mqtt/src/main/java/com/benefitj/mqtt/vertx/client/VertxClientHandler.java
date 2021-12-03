package com.benefitj.mqtt.vertx.client;

import io.vertx.core.AsyncResult;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubAckMessage;

public interface VertxClientHandler {

  /**
   * 连接结果处理
   *
   * @param client    客户端
   * @param event     确认事件
   * @param reconnect 是否为重连
   */
  default void onConnected(VertxMqttClient client, AsyncResult<MqttConnAckMessage> event, boolean reconnect) {
    // ~
  }

  /**
   * PING 消息处理
   *
   * @param client 客户端
   */
  default void onPingResponse(VertxMqttClient client) {
    // ~
  }

  /**
   * 订阅成功
   *
   * @param client  客户端
   * @param message 确认消息
   */
  default void onSubscribeCompletion(VertxMqttClient client, MqttSubAckMessage message) {
    // ~
  }

  /**
   * 取消订阅成功
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  default void onUnsubscribeCompletion(VertxMqttClient client, Integer messageId) {
    // ~
  }

  /**
   * 接收到发布
   *
   * @param client  客户端
   * @param message 消息
   */
  default void onPublishMessage(VertxMqttClient client, MqttPublishMessage message) {
    // ~
  }

  /**
   * 发布完成
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  default void onPublishCompletion(VertxMqttClient client, Integer messageId) {
    // ~
  }

  /**
   * 发布超时处理
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  default void onPublishCompletionExpiration(VertxMqttClient client, Integer messageId) {
    // ~
  }

  /**
   * 发布完成未知包ID
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  default void onPublishCompletionUnknownPacketId(VertxMqttClient client, Integer messageId) {
    // ~
  }

  /**
   * 异常处理
   *
   * @param client 客户端
   * @param error  异常
   */
  default void onException(VertxMqttClient client, Throwable error) {
    error.printStackTrace();
  }

  /**
   * 发布超时处理
   *
   * @param client 客户端
   */
  default void onClose(VertxMqttClient client) {
    // ~
  }

}
