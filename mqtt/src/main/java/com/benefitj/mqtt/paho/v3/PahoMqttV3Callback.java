package com.benefitj.mqtt.paho.v3;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import javax.annotation.Nullable;

/**
 * 回调
 */
public interface PahoMqttV3Callback extends MqttCallback {

  /**
   * 客户端重连之后的重新订阅
   *
   * @param client 客户端
   */
  default void onConnected(PahoMqttV3Client client) {
    // ~
  }

  @Override
  void messageArrived(String topic, MqttMessage message) throws Exception;

  @Override
  default void deliveryComplete(IMqttDeliveryToken token) {
  }

  @Override
  default void connectionLost(Throwable cause) {
    if (cause != null) {
      cause.printStackTrace();
    }
  }

  /**
   * 连接断开
   * s
   *
   * @param client 客户端
   * @param cause  异常
   */
  default void onDisconnected(PahoMqttV3Client client, @Nullable Throwable cause) {
  }

}
