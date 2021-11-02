package com.benefitj.mqtt.server;

import com.benefitj.mqtt.MqttTopic;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import io.vertx.mqtt.MqttAuth;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttWill;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubscribeMessage;
import io.vertx.mqtt.messages.MqttUnsubscribeMessage;

import javax.net.ssl.SSLSession;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
  void subscribe(Subscription subscription);

  /**
   * 订阅
   *
   * @param subscriptions 主题
   */
  default void subscribe(List<Subscription> subscriptions) {
    if (subscriptions != null && !subscriptions.isEmpty()) {
      for (Subscription subscription : subscriptions) {
        subscribe(subscription);
      }
    }
  }

  /**
   * 取消订阅
   *
   * @param topic 主题
   */
  void unsubscribe(MqttTopic topic);

  /**
   * 取消订阅
   *
   * @param topics 主题
   */
  default void unsubscribe(List<MqttTopic> topics) {
    for (MqttTopic topic : topics) {
      unsubscribe(topic);
    }
  }

  /**
   * 匹配
   *
   * @param topic 主題
   * @return 返回匹配的订阅
   */
  Set<Subscription> matches(MqttTopic topic);

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
  default MqttEndpoint publishAutoAck(boolean isPublishAutoAck) {
    return getOriginal().publishAutoAck(isPublishAutoAck);
  }

  @Override
  default boolean isPublishAutoAck() {
    return getOriginal().isPublishAutoAck();
  }

  @Override
  default MqttEndpoint autoKeepAlive(boolean isAutoKeepAlive) {
    return getOriginal().autoKeepAlive(isAutoKeepAlive);
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
  default MqttEndpoint setClientIdentifier(String clientIdentifier) {
    return getOriginal().setClientIdentifier(clientIdentifier);
  }

  @Override
  default MqttEndpoint disconnectHandler(Handler<Void> handler) {
    return getOriginal().disconnectHandler(handler);
  }

  @Override
  default MqttEndpoint subscribeHandler(Handler<MqttSubscribeMessage> handler) {
    return getOriginal().subscribeHandler(handler);
  }

  @Override
  default MqttEndpoint unsubscribeHandler(Handler<MqttUnsubscribeMessage> handler) {
    return getOriginal().unsubscribeHandler(handler);
  }

  @Override
  default MqttEndpoint publishHandler(Handler<MqttPublishMessage> handler) {
    return getOriginal().publishHandler(handler);
  }

  @Override
  default MqttEndpoint publishAcknowledgeHandler(Handler<Integer> handler) {
    return getOriginal().publishAcknowledgeHandler(handler);
  }

  @Override
  default MqttEndpoint publishReceivedHandler(Handler<Integer> handler) {
    return getOriginal().publishReceivedHandler(handler);
  }

  @Override
  default MqttEndpoint publishReleaseHandler(Handler<Integer> handler) {
    return getOriginal().publishReceivedHandler(handler);
  }

  @Override
  default MqttEndpoint publishCompletionHandler(Handler<Integer> handler) {
    return getOriginal().publishCompletionHandler(handler);
  }

  @Override
  default MqttEndpoint pingHandler(Handler<Void> handler) {
    return getOriginal().pingHandler(handler);
  }

  @Override
  default MqttEndpoint closeHandler(Handler<Void> handler) {
    return getOriginal().closeHandler(handler);
  }

  @Override
  default MqttEndpoint exceptionHandler(Handler<Throwable> handler) {
    return getOriginal().exceptionHandler(handler);
  }

  @Override
  default MqttEndpoint accept() {
    return getOriginal().accept();
  }

  @Override
  default MqttEndpoint accept(boolean sessionPresent) {
    return getOriginal().accept(sessionPresent);
  }

  @Override
  default MqttEndpoint reject(MqttConnectReturnCode returnCode) {
    return getOriginal().reject(returnCode);
  }

  @Override
  default MqttEndpoint subscribeAcknowledge(int subscribeMessageId, List<MqttQoS> grantedQoSLevels) {
    return getOriginal().subscribeAcknowledge(subscribeMessageId, grantedQoSLevels);
  }

  @Override
  default MqttEndpoint unsubscribeAcknowledge(int unsubscribeMessageId) {
    return getOriginal().unsubscribeAcknowledge(unsubscribeMessageId);
  }

  @Override
  default MqttEndpoint publishAcknowledge(int publishMessageId) {
    return getOriginal().publishAcknowledge(publishMessageId);
  }

  @Override
  default MqttEndpoint publishReceived(int publishMessageId) {
    return getOriginal().publishReceived(publishMessageId);
  }

  @Override
  default MqttEndpoint publishRelease(int publishMessageId) {
    return getOriginal().publishRelease(publishMessageId);
  }

  @Override
  default MqttEndpoint publishComplete(int publishMessageId) {
    return getOriginal().publishComplete(publishMessageId);
  }

  @Override
  default Future<Integer> publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isDup);
  }

  @Override
  default MqttEndpoint publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, Handler<AsyncResult<Integer>> publishSentHandler) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler);
  }

  @Override
  default Future<Integer> publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, int messageId) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isRetain, messageId);
  }

  @Override
  default MqttEndpoint publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, int messageId, Handler<AsyncResult<Integer>> publishSentHandler) {
    return getOriginal().publish(topic, payload, qosLevel, isDup, isRetain, messageId, publishSentHandler);
  }

  @Override
  default MqttEndpoint pong() {
    return getOriginal().pong();
  }

}
