package com.benefitj.vertx.mqtt;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    return new Impl<>();
  }

  /**
   * 处理消息
   *
   * @param topicName topic
   * @param message   消息
   */
  default void handleMessage(String topicName, T message) {
    final MqttTopic topic = MqttTopic.get(topicName);
    getSubscribers().forEach((subscriber, subscription) -> {
      if (subscription.match(topic)) {
        try {
          subscriber.onMessage(topicName, message);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    });
  }

  /**
   * 订阅
   */
  Map<MqttMessageSubscriber<T>, TopicSubscription<T>> getSubscribers();

  /**
   * 获取一个新的或存在的订阅者
   *
   * @param subscriber 订阅回调
   * @return 返回对应的订阅者
   */
  default TopicSubscription<T> obtainSubscriber(MqttMessageSubscriber<T> subscriber) {
    return getSubscribers().computeIfAbsent(subscriber, TopicSubscription::new);
  }

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
    TopicSubscription<T> subscription = obtainSubscriber(subscriber);
    Collection<MqttTopic> mqttTopics = wrap(topics);
    // 过滤出重复的topic
    List<String> uniqueTopics = getUniqueTopics(mqttTopics);
    subscription.addAll(mqttTopics);
    subscribeNotify(subscription, topics, uniqueTopics);
  }

  /**
   * 订阅通知
   *
   * @param subscription 主题订阅者
   * @param topics       主题
   * @param uniqueTopics 唯一的主题
   */
  default void subscribeNotify(TopicSubscription<T> subscription,
                               List<String> topics,
                               List<String> uniqueTopics) {
    // ignore
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
    TopicSubscription<T> subscription = getSubscribers().get(subscriber);
    if (subscription != null) {
      Collection<MqttTopic> mqttTopics = wrap(topics);
      subscription.removeAll(mqttTopics);
      if (subscription.isEmpty()) {
        getSubscribers().remove(subscriber);
      }
      unsubscribeNotify(subscription, topics, getUniqueTopics(mqttTopics));
    }
  }

  /**
   * 取消订阅的通知
   *
   * @param subscription 主题订阅者
   * @param topics       主题
   * @param uniqueTopics 唯一的主题
   */
  default void unsubscribeNotify(TopicSubscription<T> subscription,
                                 List<String> topics,
                                 List<String> uniqueTopics) {
    // ignore
  }

  /**
   * 取消订阅
   *
   * @param subscriber 订阅回调
   */
  default void unsubscribe(MqttMessageSubscriber<T> subscriber) {
    TopicSubscription<T> subscription = getSubscribers().remove(subscriber);
    if (subscription != null) {
      unsubscribeNotify(subscription, subscription.getTopicNames(), getUniqueTopics(subscription));
    }
  }

  /**
   * 是否存在主题
   *
   * @param topic 主题
   * @return 如果存在返回true，否则返回false
   */
  default boolean hasMqttTopic(String topic) {
    return hasMqttTopic(MqttTopic.get(topic));
  }

  /**
   * 是否存在主题
   *
   * @param topic 主题
   * @return 如果存在返回true，否则返回false
   */
  default boolean hasMqttTopic(MqttTopic topic) {
    for (TopicSubscription<T> subscription : getSubscribers().values()) {
      if (subscription.contains(topic)) {
        return true;
      }
    }
    return false;
  }

  /**
   * 获取唯一的主题
   *
   * @param topics 判断的主题集合
   * @return 返回唯一的主题
   */
  default List<String> getUniqueTopics(Collection<MqttTopic> topics) {
    return topics.stream()
        .filter(topic -> !hasMqttTopic(topic))
        .map(MqttTopic::getTopicName)
        .collect(Collectors.toList());
  }

  /**
   * 获取所有的订阅主题
   */
  default Set<MqttTopic> getMqttTopics() {
    return getSubscribers().values()
        .stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  /**
   * 获取消息订阅的主题数组
   */
  default String[] getTopicArray() {
    return getSubscribers().values()
        .stream()
        .flatMap(Collection::stream)
        .map(MqttTopic::getTopicName)
        .distinct()
        .toArray(String[]::new);
  }

  default Collection<MqttTopic> wrap(Collection<String> topics) {
    return topics.stream()
        .map(MqttTopic::get)
        .collect(Collectors.toSet());
  }

  default Collection<String> unwrap(Collection<MqttTopic> topics) {
    return topics.stream()
        .map(MqttTopic::getTopicName)
        .collect(Collectors.toSet());
  }


  /**
   * MQTT消息分发
   *
   * @param <T>
   */
  public class Impl<T> implements MqttMessageDispatcher<T> {
    /**
     * 订阅
     */
    private final Map<MqttMessageSubscriber<T>, TopicSubscription<T>> subscribers = new ConcurrentHashMap<>();

    @Override
    public Map<MqttMessageSubscriber<T>, TopicSubscription<T>> getSubscribers() {
      return subscribers;
    }
  }
}
