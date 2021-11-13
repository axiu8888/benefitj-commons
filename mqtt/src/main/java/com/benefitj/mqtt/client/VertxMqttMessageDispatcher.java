package com.benefitj.mqtt.client;

import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import com.benefitj.mqtt.MqttTopic;
import io.vertx.core.AsyncResult;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MQTT消息分发器
 */
public class VertxMqttMessageDispatcher extends MqttMessageDispatcherImpl<MqttPublishMessage>
    implements VertxClientHandler {
  /**
   * 自动订阅
   */
  private boolean autoSubscribe = true;
  /**
   * 客户端
   */
  private VertxMqttClient client;

  public VertxMqttMessageDispatcher() {
  }

  public VertxMqttMessageDispatcher(boolean autoSubscribe) {
    this.autoSubscribe = autoSubscribe;
  }

  @Override
  public void onConnected(VertxMqttClient client, AsyncResult<MqttConnAckMessage> event, boolean reconnect) {
    if (event.succeeded() && isAutoSubscribe()) {
      this.client = client;
      List<String> topics = getMqttTopics()
          .stream()
          .map(MqttTopic::getTopicName)
          .distinct()
          .collect(Collectors.toList());
      if (!topics.isEmpty()) {
        client.subscribe(topics);
      }
    }
  }

  @Override
  public final void onPublishMessage(VertxMqttClient client, MqttPublishMessage message) {
    handleMessage(message.topicName(), message);
  }

  @Override
  public void onClose(VertxMqttClient client) {
    this.client = null;
  }

  @Override
  public void subscribeNotify(List<String> topics) {
    VertxMqttClient client = getClient();
    if (client != null && topics != null && !topics.isEmpty()) {
      client.subscribe(topics);
    }
  }

  @Override
  public void unsubscribeNotify(List<String> topics) {
    VertxMqttClient client = getClient();
    if (client != null && topics != null && !topics.isEmpty()) {
      client.unsubscribe(topics);
    }
  }

  public VertxMqttClient getClient() {
    return client;
  }

  public boolean isAutoSubscribe() {
    return autoSubscribe;
  }

  public VertxMqttMessageDispatcher setAutoSubscribe(boolean autoSubscribe) {
    this.autoSubscribe = autoSubscribe;
    return this;
  }
}
