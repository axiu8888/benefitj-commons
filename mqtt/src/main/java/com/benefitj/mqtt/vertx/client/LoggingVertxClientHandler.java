package com.benefitj.mqtt.vertx.client;

import io.vertx.core.AsyncResult;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubAckMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingVertxClientHandler implements VertxClientHandler {

  protected Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public void onConnected(VertxMqttClient client, AsyncResult<MqttConnAckMessage> event, boolean reconnect) {
    log.debug("[{}] onConnected, {}:{}, status: {}, reconnect: {}"
        , client.getClientId(), client.getHost(), client.getPort(), event.succeeded(), reconnect);
  }

  /**
   * PING 消息处理
   *
   * @param client 客户端
   */
  @Override
  public void onPingResponse(VertxMqttClient client) {
    log.debug("[{}] onPingResponse, {}:{}"
        , client.getClientId(), client.getHost(), client.getPort());
  }

  /**
   * 订阅成功
   *
   * @param client  客户端
   * @param message 确认消息
   */
  @Override
  public void onSubscribeCompletion(VertxMqttClient client, MqttSubAckMessage message) {
    log.debug("[{}] onSubscribeCompletion, {}:{}, messageId[{}]"
        , client.getClientId(), client.getHost(), client.getPort(), message.messageId());
  }

  /**
   * 取消订阅成功
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  @Override
  public void onUnsubscribeCompletion(VertxMqttClient client, Integer messageId) {
    log.debug("[{}] onUnsubscribeCompletion, {}:{}, messageId[{}]"
        , client.getClientId(), client.getHost(), client.getPort(), messageId);
  }

  /**
   * 接收到发布
   *
   * @param client  客户端
   * @param message 消息
   */
  @Override
  public void onPublishMessage(VertxMqttClient client, MqttPublishMessage message) {
    log.debug("[{}] onPublishMessage, {}:{}, message[{}, {}, {}]"
        , client.getClientId(), client.getHost(), client.getPort()
        , message.messageId(), message.topicName(), message.qosLevel().value());
  }

  /**
   * 发布完成
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  @Override
  public void onPublishCompletion(VertxMqttClient client, Integer messageId) {
    log.debug("[{}] onPublishCompletion, {}:{}, messageId[{}]"
        , client.getClientId(), client.getHost(), client.getPort(), messageId);
  }

  /**
   * 发布超时处理
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  @Override
  public void onPublishCompletionExpiration(VertxMqttClient client, Integer messageId) {
    log.debug("[{}] onPublishCompletionExpiration, {}:{}, messageId[{}]"
        , client.getClientId(), client.getHost(), client.getPort(), messageId);
  }

  /**
   * 发布完成未知包ID
   *
   * @param client    客户端
   * @param messageId 消息ID
   */
  @Override
  public void onPublishCompletionUnknownPacketId(VertxMqttClient client, Integer messageId) {
    log.debug("[{}] onPublishCompletionUnknownPacketId, {}:{}, messageId[{}]"
        , client.getClientId(), client.getHost(), client.getPort(), messageId);
  }

  /**
   * 异常处理
   *
   * @param client 客户端
   * @param error  异常
   */
  @Override
  public void onException(VertxMqttClient client, Throwable error) {
    log.warn("[{}] onException, {}:{}, {}"
        , client.getClientId(), client.getHost(), client.getPort(), error.getCause());
    log.error(error.getMessage(), error);
  }

  /**
   * 发布超时处理
   *
   * @param client 客户端
   */
  @Override
  public void onClose(VertxMqttClient client) {
    log.debug("[{}] onClose, {}:{}"
        , client.getClientId(), client.getHost(), client.getPort());
  }
}
