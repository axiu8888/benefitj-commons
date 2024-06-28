package com.benefitj.vertx.mqtt.server;

import com.benefitj.core.SingletonSupplier;
import com.benefitj.vertx.mqtt.MqttTopic;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubscribeMessage;
import io.vertx.mqtt.messages.MqttUnsubscribeMessage;

import java.util.List;

public interface MqttEndpointHandler {

  SingletonSupplier<MqttEndpointHandler> single = SingletonSupplier.of(MqttEndpointHandlerImpl::new);

  static MqttEndpointHandler get() {
    return single.get();
  }

  /**
   * 连接
   *
   * @param server   服务端
   * @param endpoint 客户端
   */
  void onConnect(VertxMqttServer server, MqttEndpoint endpoint);

  /**
   * 订阅时
   *
   * @param endpoint     客户端
   * @param message      订阅主题的消息
   * @param subscription 主题
   */
  void onSubscribe(VertxMqttServer server, VertxMqttEndpoint endpoint, MqttSubscribeMessage message, List<Subscription> subscription);

  /**
   * 取消订阅
   *
   * @param endpoint 客户端
   * @param message  消息
   * @param topics   主题
   */
  void onUnsubscribe(VertxMqttServer server, VertxMqttEndpoint endpoint, MqttUnsubscribeMessage message, List<MqttTopic> topics);

  /**
   * 发布消息是的确认
   *
   * @param endpoint  客户端
   * @param messageId 消息ID
   * @param state     状态
   */
  void onPublishMessageState(VertxMqttServer server, VertxMqttEndpoint endpoint, Integer messageId, PublishMessageState state);

  /**
   * ping消息
   *
   * @param endpoint 客户端
   */
  void onPingMessage(VertxMqttServer server, VertxMqttEndpoint endpoint);

  /**
   * 发布消息
   *
   * @param endpoint 发布消息的客户端
   * @param message  消息
   */
  void onPublishMessage(VertxMqttServer server, VertxMqttEndpoint endpoint, MqttPublishMessage message);

  /**
   * 客户端被关闭
   *
   * @param server   服务端
   * @param endpoint 客户端
   */
  void onClose(VertxMqttServer server, VertxMqttEndpoint endpoint);

  /**
   * 断开
   *
   * @param server   服务端
   * @param endpoint 客户端
   */
  void onDisconnect(VertxMqttServer server, VertxMqttEndpoint endpoint);

}
