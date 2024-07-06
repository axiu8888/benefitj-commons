package com.benefitj.mqtt.paho.v3;

import com.benefitj.core.log.ILogger;
import com.benefitj.mqtt.MqttLogger;
import com.benefitj.mqtt.MqttMessageDispatcher;
import com.benefitj.mqtt.TopicSubscription;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nullable;
import java.util.List;

/**
 * MQTT消息订阅与分发
 */
public class PahoMqttV3Dispatcher extends MqttMessageDispatcher.Impl<MqttMessage> implements PahoMqttV3Callback {

  static final ILogger log = MqttLogger.get();

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
  public void onConnected(PahoMqttV3Client client) {
    this.setClient(client);
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
      PahoMqttV3Client mc = getClient();
      if (mc != null && mc.isConnected()) {
        mc.subscribe(uniqueTopics.toArray(new String[0]));
      }
    }
  }

  @Override
  public void unsubscribeNotify(TopicSubscription<MqttMessage> subscription,
                                List<String> topics,
                                List<String> uniqueTopics) {
    if (!uniqueTopics.isEmpty()) {
      PahoMqttV3Client mc = getClient();
      if (mc != null && mc.isConnected()) {
        mc.unsubscribe(uniqueTopics.toArray(new String[0]));
      }
    }
  }
}
