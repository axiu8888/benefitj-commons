package com.benefitj.mqtt.paho.v3;

import org.eclipse.paho.client.mqttv3.*;

public interface IPahoMqttV3Client extends IMqttClient {

  @Override
  void connect();

  @Override
  void connect(MqttConnectOptions options);

  @Override
  IMqttToken connectWithResult(MqttConnectOptions options);

  @Override
  void disconnect();

  @Override
  void disconnect(long quiesceTimeout);

  @Override
  void disconnectForcibly();

  @Override
  void disconnectForcibly(long disconnectTimeout);

  @Override
  void disconnectForcibly(long quiesceTimeout, long disconnectTimeout);

  @Override
  void subscribe(String topicFilter);

  @Override
  void subscribe(String[] topicFilters);

  @Override
  void subscribe(String topicFilter, int qos);

  @Override
  void subscribe(String[] topicFilters, int[] qos);

  @Override
  void subscribe(String topicFilter, IMqttMessageListener messageListener);

  @Override
  void subscribe(String[] topicFilters, IMqttMessageListener[] messageListeners);

  @Override
  void subscribe(String topicFilter, int qos, IMqttMessageListener messageListener);

  @Override
  void subscribe(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners);

  @Override
  IMqttToken subscribeWithResponse(String topicFilter);

  @Override
  IMqttToken subscribeWithResponse(String topicFilter, IMqttMessageListener messageListener);

  @Override
  IMqttToken subscribeWithResponse(String topicFilter, int qos);

  @Override
  IMqttToken subscribeWithResponse(String topicFilter, int qos, IMqttMessageListener messageListener);

  @Override
  IMqttToken subscribeWithResponse(String[] topicFilters);

  @Override
  IMqttToken subscribeWithResponse(String[] topicFilters, IMqttMessageListener[] messageListeners);

  @Override
  IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos);

  @Override
  IMqttToken subscribeWithResponse(String[] topicFilters, int[] qos, IMqttMessageListener[] messageListeners);

  @Override
  void unsubscribe(String topicFilter);

  @Override
  void unsubscribe(String[] topicFilters);

  @Override
  void publish(String topic, byte[] payload, int qos, boolean retained);

  @Override
  void publish(String topic, MqttMessage message);

  @Override
  void setCallback(MqttCallback callback);

  @Override
  MqttTopic getTopic(String topic);

  @Override
  boolean isConnected();

  @Override
  String getClientId();

  @Override
  String getServerURI();

  @Override
  IMqttDeliveryToken[] getPendingDeliveryTokens();

  @Override
  void setManualAcks(boolean manualAcks);

  @Override
  void reconnect();

  @Override
  void messageArrivedComplete(int messageId, int qos);

  @Override
  void close();
}
