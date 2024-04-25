package com.benefit.vertx.mqtt.server;

import com.benefit.vertx.mqtt.MqttTopic;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.netty.handler.codec.mqtt.MqttSubscriptionOption;
import io.vertx.mqtt.MqttTopicSubscription;

import java.util.Objects;

/**
 * 主题订阅
 */
public class Subscription implements MqttTopicSubscription {

  private final MqttTopicSubscription original;

  private final MqttTopic topic;

  public Subscription(MqttTopicSubscription original) {
    this.original = original;
    this.topic = MqttTopic.get(topicName());
  }

  public MqttTopicSubscription getOriginal() {
    return original;
  }

  public MqttTopic getTopic() {
    return topic;
  }

  /**
   * @return Subscription topic name
   */
  @Override
  public String topicName() {
    return getOriginal().topicName();
  }

  /**
   * @return Quality of Service level for the subscription
   */
  @Override
  public MqttQoS qualityOfService() {
    return getOriginal().qualityOfService();
  }

  @Override
  public MqttSubscriptionOption subscriptionOption() {
    return getOriginal().subscriptionOption();
  }

  public boolean match(MqttTopic topic) {
    return getTopic().match(topic);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Subscription that = (Subscription) o;
    return Objects.equals(topic, that.topic);
  }

  @Override
  public int hashCode() {
    return Objects.hash(topic);
  }
}
