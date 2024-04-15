package com.benefitj.mqtt.paho.v5;


import org.eclipse.paho.mqttv5.client.IMqttToken;
import org.eclipse.paho.mqttv5.client.MqttCallback;
import org.eclipse.paho.mqttv5.client.MqttDisconnectResponse;
import org.eclipse.paho.mqttv5.common.MqttException;
import org.eclipse.paho.mqttv5.common.MqttMessage;
import org.eclipse.paho.mqttv5.common.packet.MqttProperties;

public interface PahoMqttV5Callback extends MqttCallback {

  @Override
  void disconnected(MqttDisconnectResponse disconnectResponse);

  @Override
  default void mqttErrorOccurred(MqttException exception) {
    // ignore
  }

  @Override
  void messageArrived(String topic, MqttMessage message) throws Exception;

  @Override
  default void deliveryComplete(IMqttToken token)  {
    // ignore
  }

  @Override
  void connectComplete(boolean reconnect, String serverURI);

  @Override
  default void authPacketArrived(int reasonCode, MqttProperties properties) {
    // ignore
  }

}
