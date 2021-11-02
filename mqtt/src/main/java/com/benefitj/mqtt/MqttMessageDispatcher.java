package com.benefitj.mqtt;

import com.benefitj.core.concurrent.ConcurrentHashSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * MQTT消息分发
 *
 * @param <T> 消息类型
 */
public interface MqttMessageDispatcher<T> {

  /**
   * 创建新的消息分发器
   *
   * @param <T> 消息类型
   * @return 返回消息分发器
   */
  static <T> MqttMessageDispatcher<T> newDispatcher() {
    return new MqttMessageDispatcherImpl<>();
  }

  /**
   * 处理消息
   *
   * @param topicName topic
   * @param message   消息
   */
  default void handleMessage(String topicName, T message) {
    MqttTopic topic = MqttTopic.get(topicName);
    for (Map.Entry<MqttMessageSubscriber<T>, Set<MqttTopic>> entry : getSubscribers().entrySet()) {
      try {
        for (MqttTopic filter : entry.getValue()) {
          if (filter.match(topic)) {
            entry.getKey().onMessage(topicName, message);
            break;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 订阅
   */
  Map<MqttMessageSubscriber<T>, Set<MqttTopic>> getSubscribers();

  /**
   * 订阅
   *
   * @param topic      主题
   * @param subscriber 订阅回调
   */
  default void subscribe(String topic, MqttMessageSubscriber<T> subscriber) {
    subscribe(Collections.singletonList(topic), subscriber);
  }

  /**
   * 订阅
   *
   * @param topics     主题
   * @param subscriber 订阅回调
   */
  default void subscribe(String[] topics, MqttMessageSubscriber<T> subscriber) {
    subscribe(Arrays.asList(topics), subscriber);
  }

  /**
   * 订阅
   *
   * @param topics     主题
   * @param subscriber 订阅回调
   */
  default void subscribe(List<String> topics, MqttMessageSubscriber<T> subscriber) {
    Set<MqttTopic> topicSet = getSubscribers().computeIfAbsent(subscriber, key -> new ConcurrentHashSet<>());
    topicSet.addAll(topics.stream()
        .map(MqttTopic::get)
        .collect(Collectors.toSet()));
  }

  /**
   * 取消订阅
   *
   * @param topic      主题
   * @param subscriber 订阅回调
   */
  default void unsubscribe(String topic, MqttMessageSubscriber<T> subscriber) {
    unsubscribe(Collections.singletonList(topic), subscriber);
  }

  /**
   * 取消订阅
   *
   * @param topics     主题
   * @param subscriber 订阅回调
   */
  default void unsubscribe(String[] topics, MqttMessageSubscriber<T> subscriber) {
    unsubscribe(Arrays.asList(topics), subscriber);
  }

  /**
   * 取消订阅
   *
   * @param topics     主题
   * @param subscriber 订阅回调
   */
  default void unsubscribe(List<String> topics, MqttMessageSubscriber<T> subscriber) {
    Set<MqttTopic> topicSet = getSubscribers().get(subscriber);
    if (topicSet != null) {
      topicSet.removeAll(topics.stream()
          .map(MqttTopic::get)
          .collect(Collectors.toSet()));
      if (topicSet.isEmpty()) {
        getSubscribers().remove(subscriber);
      }
    }
  }

  /**
   * 取消订阅
   *
   * @param subscriber 订阅回调
   */
  default void unsubscribe(MqttMessageSubscriber<T> subscriber) {
    unsubscribe("#", subscriber);
  }

  /**
   * 获取所有的订阅主题
   */
  default List<MqttTopic> getMqttTopics() {
    return getSubscribers().values()
        .stream()
        .flatMap(Collection::stream)
        .distinct()
        .collect(Collectors.toList());
  }

  /**
   * 获取消息订阅的主题数组
   */
  default String[] getMqttTopicArray() {
    return getSubscribers().values()
        .stream()
        .flatMap(Collection::stream)
        .map(MqttTopic::getTopicName)
        .distinct()
        .toArray(String[]::new);
  }

}
