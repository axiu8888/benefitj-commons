package com.benefitj.vertx.mqtt.server;

import com.benefitj.vertx.mqtt.MqttTopic;
import io.vertx.mqtt.MqttEndpoint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

  public VertxMqttEndpointImpl(MqttEndpoint original) {
    this.original = original;
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
  public VertxMqttEndpoint subscribe(Subscription subscription) {
    getSubscriptions().put(subscription.getTopic(), subscription);
    return this;
  }

  /**
   * 取消订阅
   *
   * @param topic 主题
   */
  @Override
  public VertxMqttEndpoint unsubscribe(MqttTopic topic) {
    getSubscriptions().remove(topic);
    return this;
  }

  /**
   * 匹配
   *
   * @param topic 主題
   * @return 返回匹配的订阅
   */
  @Override
  public Collection<Subscription> matches(MqttTopic topic) {
    List<Subscription> subscriptions = new LinkedList<>();
    for (Map.Entry<MqttTopic, Subscription> entry : getSubscriptions().entrySet()) {
      Subscription subscription = entry.getValue();
      if (subscription.match(topic)) {
        subscriptions.add(subscription);
      }
    }
    return subscriptions;
    /*return getSubscriptions().values()
        .stream()
        .filter(subscription -> subscription.getTopic().match(topic))
        .collect(Collectors.toSet());*/
  }

}
