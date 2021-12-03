package com.benefitj.mqtt.vertx.client;

import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import com.benefitj.mqtt.TopicSubscription;
import io.vertx.core.AsyncResult;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;

import java.util.Arrays;
import java.util.List;

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
      String[] array = getTopicArray();
      if (array.length > 0) {
        client.subscribe(Arrays.asList(array));
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
  public void subscribeNotify(TopicSubscription<MqttPublishMessage> subscription,
                              List<String> topics,
                              List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      VertxMqttClient client = getClient();
      if (client != null) {
        client.subscribe(topics);
      }
    }
  }

  @Override
  public void unsubscribeNotify(TopicSubscription<MqttPublishMessage> subscription,
                                List<String> topics,
                                List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      VertxMqttClient client = getClient();
      if (client != null) {
        client.unsubscribe(topics);
      }
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
