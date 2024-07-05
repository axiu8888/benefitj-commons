package com.benefitj.vertx.mqtt;

import com.benefitj.core.concurrent.ConcurrentHashSet;
import com.benefitj.core.functions.WrappedSet;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订阅的主题
 *
 * @param <T>
 */
public class TopicSubscription<T> implements WrappedSet<MqttTopic> {

  private final Set<MqttTopic> topics = new ConcurrentHashSet<>();

  private MqttMessageSubscriber<T> subscriber;

  public TopicSubscription(MqttMessageSubscriber<T> subscriber) {
    this.subscriber = subscriber;
  }

  @Override
  public Set<MqttTopic> set() {
    return topics;
  }

  public void setSubscriber(MqttMessageSubscriber<T> subscriber) {
    this.subscriber = subscriber;
  }

  public MqttMessageSubscriber<T> getSubscriber() {
    return subscriber;
  }

  public List<String> getTopicNames() {
    return stream()
        .map(MqttTopic::getTopicName)
        .collect(Collectors.toList());
  }

  /**
   * 判断是否存在匹配的主题
   *
   * @param topic 主题
   * @return 返回判断结果
   */
  public boolean match(MqttTopic topic) {
    for (MqttTopic filter : this) {
      if (filter.match(topic)) {
        return true;
      }
    }
    return false;
  }

}
