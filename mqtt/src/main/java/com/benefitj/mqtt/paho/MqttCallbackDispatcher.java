package com.benefitj.mqtt.paho;

import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import com.benefitj.mqtt.TopicSubscription;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nullable;
import java.util.List;

/**
 * MQTT消息订阅与分发
 */
public class MqttCallbackDispatcher extends MqttMessageDispatcherImpl<MqttMessage> implements IMqttCallback {

  private PahoMqttClient client;

  public PahoMqttClient getClient() {
    return client;
  }

  @Override
  public void onConnected(PahoMqttClient client, boolean reconnect) {
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
  public void onDisconnected(PahoMqttClient client, @Nullable Throwable cause) {
    if (cause != null) {
      cause.printStackTrace();
    }
  }

  @Override
  public void subscribeNotify(TopicSubscription<MqttMessage> subscription,
                              List<String> topics,
                              List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      PahoMqttClient client = getClient();
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
      PahoMqttClient client = getClient();
      if (client != null && client.isConnected()) {
        client.unsubscribe(uniqueTopics.toArray(new String[0]));
      }
    }
  }
}
