package com.benefitj.mqtt.server;

import com.benefitj.mqtt.MqttTopic;
import io.vertx.mqtt.MqttEndpoint;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * MQTT客户端连接
 */
public class VertxMqttEndpointImpl implements VertxMqttEndpoint {
  /**
   * 原始的连接
   */
  private final MqttEndpoint original;
  /**
   * 订阅的Topic
   */
  private final Map<MqttTopic, Subscription> subscriptions = new ConcurrentHashMap<>();

  public VertxMqttEndpointImpl(MqttEndpoint source) {
    this.original = source;
  }

  @Override
  public MqttEndpoint getOriginal() {
    return original;
  }

  @Override
  public Subscription getSubscription(MqttTopic topic, Comparator<Subscription> comparator) {
    if (!getSubscriptions().isEmpty()) {
      return getSubscriptions().values()
          .stream()
          .filter(subscription -> subscription.match(topic))
          .min(comparator)
          .orElse(null);
    }
    return null;
  }

  @Override
  public Map<MqttTopic, Subscription> getSubscriptions() {
    return subscriptions;
  }

  @Override
  public boolean hasSubscription(MqttTopic topic) {
    if (!getSubscriptions().isEmpty()) {
      for (MqttTopic t : getSubscriptions().keySet()) {
        if (t.match(topic)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * 订阅
   *
   * @param subscription 主题
   */
  @Override
  public void subscribe(Subscription subscription) {
    getSubscriptions().put(subscription.getTopic(), subscription);
  }

  /**
   * 取消订阅
   *
   * @param topic 主题
   */
  @Override
  public void unsubscribe(MqttTopic topic) {
    getSubscriptions().remove(topic);
  }

  /**
   * 匹配
   *
   * @param topic 主題
   * @return 返回匹配的订阅
   */
  @Override
  public Set<Subscription> matches(MqttTopic topic) {
    return getSubscriptions().values()
        .stream()
        .filter(subscription -> subscription.getTopic().match(topic))
        .collect(Collectors.toSet());
  }

}
