package com.benefitj.vertx.mqtt.server;

import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.functions.WrappedMap;
import com.benefitj.vertx.mqtt.MqttTopic;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.buffer.Buffer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 客户端管理类
 */
public class VertxMqttEndpointManager implements WrappedMap<String, VertxMqttEndpoint> {

  static final SingletonSupplier<VertxMqttEndpointManager> single = SingletonSupplier.of(VertxMqttEndpointManager::new);

  public static VertxMqttEndpointManager get() {
    return single.get();
  }

  final Map<String, VertxMqttEndpoint> _internal = new ConcurrentHashMap<>();

  @Override
  public Map<String, VertxMqttEndpoint> map() {
    return _internal;
  }

  /**
   * 发布消息
   *
   * @param topic   主题
   * @param payload 有效载荷
   */
  public void publish(String topic, byte[] payload) {
    publish(topic, payload, 0);
  }

  /**
   * 发布消息
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @param qos     服务质量
   */
  public void publish(String topic, byte[] payload, int qos) {
    publish(topic, payload, MqttQoS.valueOf(qos), false, false);
  }

  /**
   * 发布消息
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qosLevel 服务质量
   * @param isDup    指示消息是否是一个重复的消息
   * @param isRetain 是否保留，以便新订阅者连接时可以接收到最新的保留消息
   */
  public void publish(String topic, byte[] payload, MqttQoS qosLevel, boolean isDup, boolean isRetain) {
    MqttTopic mqttTopic = MqttTopic.get(topic);
    map().forEach((id, endpoint) -> {
      if (endpoint.hasSubscription(mqttTopic)) {
        endpoint.publish(topic, Buffer.buffer(payload), qosLevel, isDup, isRetain);
      }
    });
  }

  /**
   * 判断是否有对应客户端
   *
   * @param clientId 客户端ID
   * @return 返回判断结果，如果有返回true，否则返回false
   */
  public boolean hasClientId(String clientId) {
    return containsKey(clientId);
  }

  /**
   * 添加客户端
   *
   * @param clientId 客户端ID
   * @param endpoint 客户端
   */
  public VertxMqttEndpoint addEndpoint(String clientId, VertxMqttEndpoint endpoint) {
    return put(clientId, endpoint);
  }

  /**
   * 移除客户端
   *
   * @param clientId 客户端ID
   * @return 返回被移除的客户端
   */
  public VertxMqttEndpoint removeEndpoint(String clientId) {
    return remove(clientId);
  }

  /**
   * 获取客户端
   *
   * @param clientId 客户端ID
   * @return 返回获取的客户端
   */
  public VertxMqttEndpoint getEndpoint(String clientId) {
    return get(clientId);
  }

  /**
   * 获取某一个客户端的订阅
   *
   * @param clientId 客户端ID
   * @return 返回订阅的主题
   */
  public List<Subscription> getTopic(String clientId) {
    VertxMqttEndpoint endpoint = getEndpoint(clientId);
    if (endpoint != null) {
      return new ArrayList<>(endpoint.getSubscriptions().values());
    }
    return Collections.emptyList();
  }

  /**
   * 获取的全部的订阅
   */
  public List<String> getTopicAll() {
    return values()
        .stream()
        .flatMap(endpoint -> endpoint.getSubscriptions()
            .values()
            .stream())
        .map(Subscription::topicName)
        .distinct()
        .collect(Collectors.toList());
  }

}
