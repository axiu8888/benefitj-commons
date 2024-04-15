package com.benefitj.mqtt.paho.v5;


import com.benefitj.mqtt.MqttMessageDispatcherImpl;
import com.benefitj.mqtt.TopicSubscription;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttMessage;

import java.util.List;

/**
 * MQTT消息订阅与分发
 */
public class PahoMqttV5Dispatcher extends MqttMessageDispatcherImpl<MqttMessage> implements PahoMqttV5Callback {

  PahoMqttV5Client client;

  public PahoMqttV5Dispatcher() {
  }

  public PahoMqttV5Dispatcher(PahoMqttV5Client client) {
    this.client = client;
  }

  public PahoMqttV5Client getClient() {
    return client;
  }

  public void setClient(PahoMqttV5Client client) {
    this.client = client;
  }

  @Override
  public void connectComplete(boolean reconnect, String serverURI) {
    // 重新订阅
    String[] array = getTopicArray();
    if (array != null && array.length > 0) {
      client.subscribe(array, new int[array.length]);
    }
  }

  @Override
  public void messageArrived(String topic, MqttMessage message) throws Exception {
    handleMessage(topic, message);
  }

  @Override
  public void disconnected(MqttDisconnectResponse disconnectResponse) {
    int returnCode = disconnectResponse.getReturnCode();
  }

  @Override
  public void subscribeNotify(TopicSubscription<MqttMessage> subscription,
                              List<String> topics,
                              List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      PahoMqttV5Client client = getClient();
      if (client != null && client.isConnected()) {
        client.subscribe(uniqueTopics.toArray(new String[0]), new int[uniqueTopics.size()]);
      }
    }
  }

  @Override
  public void unsubscribeNotify(TopicSubscription<MqttMessage> subscription,
                                List<String> topics,
                                List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      PahoMqttV5Client client = getClient();
      if (client != null && client.isConnected()) {
        client.unsubscribe(uniqueTopics.toArray(new String[0]));
      }
    }
  }


}
