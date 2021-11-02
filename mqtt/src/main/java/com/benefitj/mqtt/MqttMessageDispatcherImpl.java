package com.benefitj.mqtt;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT消息分发
 *
 * @param <T>
 */
public class MqttMessageDispatcherImpl<T> implements MqttMessageDispatcher<T> {
  /**
   * 订阅
   */
  private final Map<MqttMessageSubscriber<T>, Set<MqttTopic>> subscribers = new ConcurrentHashMap<>();

  @Override
  public Map<MqttMessageSubscriber<T>, Set<MqttTopic>> getSubscribers() {
    return subscribers;
  }
}