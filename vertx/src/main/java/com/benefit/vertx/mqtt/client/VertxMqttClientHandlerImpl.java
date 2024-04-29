package com.benefit.vertx.mqtt.client;

import com.benefit.vertx.VertxLogger;
import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.log.ILogger;
import io.vertx.core.AsyncResult;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubAckMessage;

public class VertxMqttClientHandlerImpl implements VertxMqttClientHandler {

  static final SingletonSupplier<VertxMqttClientHandlerImpl> single = SingletonSupplier.of(VertxMqttClientHandlerImpl::new);

  public static VertxMqttClientHandlerImpl get() {
    return single.get();
  }

  protected ILogger log = VertxLogger.get();

  @Override
  public void onConnected(VertxMqttClient client, AsyncResult<MqttConnAckMessage> event, boolean reconnect) {
    log.trace("[{}] onConnected, {}:{}, status: {}, reconnect: {}"
        , client.getClientId(), client.getHost(), client.getPort(), event.succeeded(), reconnect);
  }

  /**
   * PING 消息处理
   *
   * @param client 客户端
   */
  @Override
  public void onPingResponse(VertxMqttClient client) {
    log.trace("[{}] onPingResponse, {}:{}"
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
    log.trace("[{}] onSubscribeCompletion, {}:{}, messageId[{}]"
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
    log.trace("[{}] onUnsubscribeCompletion, {}:{}, messageId[{}]"
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
    log.trace("[{}] onPublishMessage, {}:{}, message[{}, {}, {}]"
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
    log.trace("[{}] onPublishCompletion, {}:{}, messageId[{}]"
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
    log.trace("[{}] onPublishCompletionExpiration, {}:{}, messageId[{}]"
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
    log.trace("[{}] onPublishCompletionUnknownPacketId, {}:{}, messageId[{}]"
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
    log.trace("[{}] onException, {}:{}, {}"
        , client.getClientId(), client.getHost(), client.getPort(), error.getCause());
    log.trace(error.getMessage(), error);
  }

  /**
   * 发布超时处理
   *
   * @param client 客户端
   */
  @Override
  public void onClose(VertxMqttClient client) {
    log.trace("[{}] onClose, {}:{}"
        , client.getClientId(), client.getHost(), client.getPort());
  }
}
