package com.benefitj.vertx.mqtt.server;

import com.benefitj.vertx.mqtt.MqttTopic;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttProperties;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import io.vertx.mqtt.MqttAuth;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttWill;
import io.vertx.mqtt.messages.*;
import io.vertx.mqtt.messages.codes.*;

import javax.net.ssl.SSLSession;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * MQTT客户端连接
 */
public interface VertxMqttEndpoint extends MqttEndpoint {

  Comparator<Subscription> TOP_SUBSCRIPTION_COMPARATOR = (o1, o2) -> -Integer.compare(o1.qualityOfService().value(), o2.qualityOfService().value());

  MqttEndpoint getOriginal();

  /**
   * 获取全部的订阅
   */
  Map<MqttTopic, Subscription> getSubscriptions();

  /**
   * 获取Subscription
   */
  Subscription getSubscription(MqttTopic topic, Comparator<Subscription> comparator);

  /**
   * 获取最大值的Subscription
   */
  default Subscription getTopQosSubscription(MqttTopic topic) {
    return getSubscription(topic, TOP_SUBSCRIPTION_COMPARATOR);
  }

  /**
   * 检查是否存在对应topic的订阅
   */
  boolean hasSubscription(MqttTopic topic);

  /**
   * 订阅
   *
   * @param subscription 主题
   */
  VertxMqttEndpoint subscribe(Subscription subscription);

  /**
   * 订阅
   *
   * @param subscriptions 主题
   */
  default VertxMqttEndpoint subscribe(List<Subscription> subscriptions) {
    if (subscriptions != null && !subscriptions.isEmpty()) {
      for (Subscription subscription : subscriptions) {
        subscribe(subscription);
      }
    }
    return this;
  }

  /**
   * 取消订阅
   *
   * @param topic 主题
   */
  VertxMqttEndpoint unsubscribe(MqttTopic topic);

  /**
   * 取消订阅
   *
   * @param topics 主题
   */
  default VertxMqttEndpoint unsubscribe(List<MqttTopic> topics) {
    for (MqttTopic topic : topics) {
      unsubscribe(topic);
    }
    return this;
  }

  /**
   * 匹配
   *
   * @param topic 主題
   * @return 返回匹配的订阅
   */
  Collection<Subscription> matches(MqttTopic topic);

  @Override
  default void close() {
    getOriginal().close();
  }

  @Override
  default SocketAddress remoteAddress() {
    return getOriginal().remoteAddress();
  }

  @Override
  default SocketAddress localAddress() {
    return getOriginal().localAddress();
  }

  @Override
  default boolean isSsl() {
    return getOriginal().isSsl();
  }

  @Override
  default MultiMap httpHeaders() {
    return getOriginal().httpHeaders();
  }

  @Override
  default String httpRequestURI() {
    return getOriginal().httpRequestURI();
  }

  @Override
  default SSLSession sslSession() {
    return getOriginal().sslSession();
  }

  @Override
  default String clientIdentifier() {
    return getOriginal().clientIdentifier();
  }

  @Override
  default MqttAuth auth() {
    return getOriginal().auth();
  }

  @Override
  default MqttWill will() {
    return getOriginal().will();
  }

  @Override
  default int protocolVersion() {
    return getOriginal().protocolVersion();
  }

  @Override
  default String protocolName() {
    return getOriginal().protocolName();
  }

  @Override
  default boolean isCleanSession() {
    return getOriginal().isCleanSession();
  }

  @Override
  default int keepAliveTimeSeconds() {
    return getOriginal().keepAliveTimeSeconds();
  }

  @Override
  default int lastMessageId() {
    return getOriginal().lastMessageId();
  }

  @Override
  default void subscriptionAutoAck(boolean isSubscriptionAutoAck) {
    getOriginal().subscriptionAutoAck(isSubscriptionAutoAck);
  }

  @Override
  default boolean isSubscriptionAutoAck() {
    return getOriginal().isSubscriptionAutoAck();
  }

  @Override
  default VertxMqttEndpoint publishAutoAck(boolean isPublishAutoAck) {
    getOriginal().publishAutoAck(isPublishAutoAck);
    return this;
  }

  @Override
  default boolean isPublishAutoAck() {
    return getOriginal().isPublishAutoAck();
  }

  @Override
  default VertxMqttEndpoint autoKeepAlive(boolean isAutoKeepAlive) {
    getOriginal().autoKeepAlive(isAutoKeepAlive);
    return this;
  }

  @Override
  default boolean isAutoKeepAlive() {
    return getOriginal().isAutoKeepAlive();
  }

  @Override
  default boolean isConnected() {
    return getOriginal().isConnected();
  }

  @Override
  default MqttProperties connectProperties() {
    return getOriginal().connectProperties();
  }

  @Override
  default VertxMqttEndpoint setClientIdentifier(String clientIdentifier) {
    getOriginal().setClientIdentifier(clientIdentifier);
    return this;
  }

  @Override
  default VertxMqttEndpoint disconnectHandler(Handler<Void> handler) {
    getOriginal().disconnectHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint disconnectMessageHandler(Handler<MqttDisconnectMessage> handler) {
    getOriginal().disconnectMessageHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint subscribeHandler(Handler<MqttSubscribeMessage> handler) {
    getOriginal().subscribeHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint unsubscribeHandler(Handler<MqttUnsubscribeMessage> handler) {
    getOriginal().unsubscribeHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishHandler(Handler<MqttPublishMessage> handler) {
    getOriginal().publishHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishAcknowledgeHandler(Handler<Integer> handler) {
    getOriginal().publishAcknowledgeHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishAcknowledgeMessageHandler(Handler<MqttPubAckMessage> handler) {
    getOriginal().publishAcknowledgeMessageHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishReceivedHandler(Handler<Integer> handler) {
    getOriginal().publishReceivedHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishReceivedMessageHandler(Handler<MqttPubRecMessage> handler) {
    getOriginal().publishReceivedMessageHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishReleaseHandler(Handler<Integer> handler) {
    getOriginal().publishReceivedHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishReleaseMessageHandler(Handler<MqttPubRelMessage> handler) {
    getOriginal().publishReleaseMessageHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishCompletionHandler(Handler<Integer> handler) {
    getOriginal().publishCompletionHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishCompletionMessageHandler(Handler<MqttPubCompMessage> handler) {
    getOriginal().publishCompletionMessageHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint pingHandler(Handler<Void> handler) {
    getOriginal().pingHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint closeHandler(Handler<Void> handler) {
    getOriginal().closeHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint exceptionHandler(Handler<Throwable> handler) {
    getOriginal().exceptionHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint accept() {
    getOriginal().accept();
    return this;
  }

  @Override
  default VertxMqttEndpoint accept(boolean sessionPresent) {
    getOriginal().accept(sessionPresent);
    return this;
  }

  @Override
  default VertxMqttEndpoint accept(boolean sessionPresent, MqttProperties properties) {
    getOriginal().accept(sessionPresent, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint reject(MqttConnectReturnCode returnCode) {
    getOriginal().reject(returnCode);
    return this;
  }

  @Override
  default VertxMqttEndpoint reject(MqttConnectReturnCode returnCode, MqttProperties properties) {
    getOriginal().reject(returnCode, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint subscribeAcknowledge(int subscribeMessageId, List<MqttQoS> grantedQoSLevels) {
    getOriginal().subscribeAcknowledge(subscribeMessageId, grantedQoSLevels);
    return this;
  }

  @Override
  default VertxMqttEndpoint subscribeAcknowledge(int subscribeMessageId, List<MqttSubAckReasonCode> reasonCodes, MqttProperties properties) {
    getOriginal().subscribeAcknowledge(subscribeMessageId, reasonCodes, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint unsubscribeAcknowledge(int unsubscribeMessageId) {
    getOriginal().unsubscribeAcknowledge(unsubscribeMessageId);
    return this;
  }

  @Override
  default VertxMqttEndpoint unsubscribeAcknowledge(int unsubscribeMessageId, List<MqttUnsubAckReasonCode> reasonCodes, MqttProperties properties) {
    getOriginal().unsubscribeAcknowledge(unsubscribeMessageId, reasonCodes, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishAcknowledge(int publishMessageId) {
    getOriginal().publishAcknowledge(publishMessageId);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishAcknowledge(int publishMessageId, MqttPubAckReasonCode reasonCode, MqttProperties properties) {
    getOriginal().publishAcknowledge(publishMessageId, reasonCode, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishReceived(int publishMessageId) {
    getOriginal().publishReceived(publishMessageId);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishReceived(int publishMessageId, MqttPubRecReasonCode reasonCode, MqttProperties properties) {
    getOriginal().publishReceived(publishMessageId, reasonCode, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishRelease(int publishMessageId) {
    getOriginal().publishRelease(publishMessageId);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishRelease(int publishMessageId, MqttPubRelReasonCode reasonCode, MqttProperties properties) {
    getOriginal().publishRelease(publishMessageId, reasonCode, properties);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishComplete(int publishMessageId) {
    getOriginal().publishComplete(publishMessageId);
    return this;
  }

  @Override
  default VertxMqttEndpoint publishComplete(int publishMessageId, MqttPubCompReasonCode reasonCode, MqttProperties properties) {
    getOriginal().publishComplete(publishMessageId, reasonCode, properties);
    return this;
  }

  @Override
  default Future<Integer> publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isDup);
  }

  @Override
  default Future<Integer> publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, int messageId) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isRetain, messageId);
  }

  @Override
  default Future<Integer> publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, int messageId, MqttProperties properties) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isRetain, messageId, properties);
  }

  @Override
  default VertxMqttEndpoint authenticationExchangeHandler(Handler<MqttAuthenticationExchangeMessage> handler) {
    getOriginal().authenticationExchangeHandler(handler);
    return this;
  }

  @Override
  default VertxMqttEndpoint authenticationExchange(MqttAuthenticationExchangeMessage message) {
    getOriginal().authenticationExchange(message);
    return this;
  }

  @Override
  default VertxMqttEndpoint pong() {
    getOriginal().pong();
    return this;
  }

  @Override
  default VertxMqttEndpoint disconnect(MqttDisconnectReasonCode code, MqttProperties properties) {
    getOriginal().disconnect(code, properties);
    return this;
  }

}
