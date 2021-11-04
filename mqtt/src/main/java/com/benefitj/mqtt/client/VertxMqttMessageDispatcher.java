package com.benefitj.mqtt.client;

import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import com.benefitj.mqtt.MqttTopic;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import io.vertx.mqtt.messages.MqttPublishMessage;

/**
 * MQTT消息分发器
 */
public class VertxMqttMessageDispatcher extends MqttMessageDispatcherImpl<MqttPublishMessage>
    implements VertxClientHandler {
  /**
   * 自动订阅
   */
  private boolean autoSubscribe = true;

  public VertxMqttMessageDispatcher() {
  }

  public VertxMqttMessageDispatcher(boolean autoSubscribe) {
    this.autoSubscribe = autoSubscribe;
  }

  @Override
  public void onConnected(VertxMqttClient client, AsyncResult<MqttConnAckMessage> event, boolean reconnect) {
    if (event.succeeded() && isAutoSubscribe()) {
      getMqttTopics()
          .stream()
          .map(MqttTopic::getTopicName)
          .distinct()
          .forEach(topic -> client.subscribe(topic, MqttQoS.AT_MOST_ONCE));
    }
  }

  @Override
  public final void onPublishMessage(VertxMqttClient client, MqttPublishMessage message) {
    handleMessage(message.topicName(), message);
  }

  public boolean isAutoSubscribe() {
    return autoSubscribe;
  }

  public VertxMqttMessageDispatcher setAutoSubscribe(boolean autoSubscribe) {
    this.autoSubscribe = autoSubscribe;
    return this;
  }
}
