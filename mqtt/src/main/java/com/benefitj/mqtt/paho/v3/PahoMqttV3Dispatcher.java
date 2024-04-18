package com.benefitj.mqtt.paho.v3;

import com.benefitj.mqtt.MqttMessageDispatcher;
import com.benefitj.mqtt.TopicSubscription;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nullable;
import java.util.List;

/**
 * MQTT消息订阅与分发
 */
public class PahoMqttV3Dispatcher extends MqttMessageDispatcher.Impl<MqttMessage> implements PahoMqttV3Callback {

  private PahoMqttV3Client client;

  public PahoMqttV3Dispatcher() {
  }

  public PahoMqttV3Client getClient() {
    return client;
  }

  public void setClient(PahoMqttV3Client client) {
    this.client = client;
  }

  @Override
  public void onConnected(PahoMqttV3Client client, boolean reconnect) {
    this.client = client;
    // 重新订阅
    String[] array = getTopicArray();
    if (array != null && array.length > 0) {
      client.subscribe(array);
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    handleMessage(topic, message);
  }

  @Override
  public void onDisconnected(PahoMqttV3Client client, @Nullable Throwable cause) {
    if (cause != null) {
      cause.printStackTrace();
    }
  }

  @Override
  public void subscribeNotify(TopicSubscription<MqttMessage> subscription,
                              List<String> topics,
                              List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      PahoMqttV3Client client = getClient();
      if (client != null && client.isConnected()) {
        client.subscribe(uniqueTopics.toArray(new String[0]));
      }
    }
  }

  @Override
  public void unsubscribeNotify(TopicSubscription<MqttMessage> subscription,
                                List<String> topics,
                                List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      PahoMqttV3Client client = getClient();
      if (client != null && client.isConnected()) {
        client.unsubscribe(uniqueTopics.toArray(new String[0]));
      }
    }
  }
}
