package com.benefitj.vertx.mqtt.server;

import com.benefitj.core.log.ILogger;
import com.benefitj.vertx.VertxLogger;
import com.benefitj.vertx.mqtt.MqttTopic;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttWill;
import io.vertx.mqtt.messages.MqttPublishMessage;
import io.vertx.mqtt.messages.MqttSubscribeMessage;
import io.vertx.mqtt.messages.MqttUnsubscribeMessage;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MQTT客户端连接处理器
 */
public class MqttEndpointHandlerImpl implements MqttEndpointHandler {

  private final ILogger log = VertxLogger.get();

  @Override
  public void onConnect(VertxMqttServer server, MqttEndpoint endpoint) {
    // shows main connect info
    log.trace("MQTT client [{}] request to connect, clean session = {}", endpoint.clientIdentifier(), endpoint.isCleanSession());

    if (endpoint.auth() != null) {
      log.trace("[username = {}, password = {}]", endpoint.auth().getUsername(), endpoint.auth().getPassword());
    }
    if (!server.getAuthenticator().authenticate(endpoint)) {
      // 认证失败，直接关闭
      endpoint.close();
      return;
    }

    MqttWill will = endpoint.will();
    if (will != null) {
      log.trace("[will flag = {} topic = {} msg = {} QoS = {} isRetain = {}]",
          will.isWillFlag(), will.getWillTopic(), will.getWillMessage(), will.getWillQos(), will.isWillRetain()
      );
    }

    // accept connection from the remote client
    VertxMqttEndpointManager manager = server.getEndpointManager();
    VertxMqttEndpoint existEndpoint = manager.getEndpoint(endpoint.clientIdentifier());
    if (existEndpoint != null) {
      if (!server.getProperty().isDislodgeSession()) {
        endpoint.accept(true);
        return;
      }
      existEndpoint.close();
    }
    endpoint.accept(false);
    // 添加客户端
    manager.addEndpoint(endpoint.clientIdentifier(), new VertxMqttEndpointImpl(endpoint));
  }

  @Override
  public void onSubscribe(VertxMqttServer server, VertxMqttEndpoint endpoint, MqttSubscribeMessage message, List<Subscription> subscriptions) {
    // 订阅主题
    endpoint.subscribe(subscriptions);
    // 接受订阅
    endpoint.subscribeAcknowledge(message.messageId(), subscriptions.stream()
        .map(Subscription::qualityOfService)
        .collect(Collectors.toList()));

    log.trace("client[{}], subscriber: {}", endpoint.clientIdentifier(), subscriptions.stream()
        .map(s -> String.format("[%s, %d]", s.topicName(), s.qualityOfService().value()))
        .collect(Collectors.toList()));
  }

  @Override
  public void onUnsubscribe(VertxMqttServer server, VertxMqttEndpoint endpoint, MqttUnsubscribeMessage message, List<MqttTopic> topics) {
    endpoint.unsubscribe(topics);
    // ack the subscriptions request
    endpoint.unsubscribeAcknowledge(message.messageId());

    log.trace("clientId[{}], Unsubscription for {}", endpoint.clientIdentifier(), topics.stream()
        .map(MqttTopic::getTopicName)
        .collect(Collectors.toList()));
  }

  @Override
  public void onPublishMessageState(VertxMqttServer server, VertxMqttEndpoint endpoint, Integer messageId, PublishMessageState state) {
    if (state == PublishMessageState.RECEIVED) {
      endpoint.publishRelease(messageId);
    }

    log.trace("clientId[{}], Received ack for message = {}, state = {}", endpoint.clientIdentifier(), messageId, state);
  }

  @Override
  public void onPingMessage(VertxMqttServer server, VertxMqttEndpoint endpoint) {
    log.trace("clientId[{}], Ping...", endpoint.clientIdentifier());
  }

  @Override
  public void onPublishMessage(VertxMqttServer server, VertxMqttEndpoint endpoint, MqttPublishMessage message) {
    MqttTopic topic = MqttTopic.get(message.topicName());
    for (VertxMqttEndpoint vme : server.getEndpointManager().values()) {
      if (vme != endpoint) {
        Subscription subscription = vme.getTopQosSubscription(topic);
        if (subscription != null) {
          vme.publish(message.topicName(), message.payload(), subscription.qualityOfService(), message.isDup(), message.isRetain());
        }
      }
    }
    if (message.qosLevel() == MqttQoS.AT_LEAST_ONCE) {
      endpoint.publishAcknowledge(message.messageId());
    } else if (message.qosLevel() == MqttQoS.EXACTLY_ONCE) {
      endpoint.publishReceived(message.messageId());
    }

    log.trace("clientId[{}], received message on [{}] payload.length [{}] with QoS [{}], messageId[{}]"
        , endpoint.clientIdentifier(), message.topicName(), message.payload().length(), message.qosLevel(), message.messageId());

  }

  @Override
  public void onClose(VertxMqttServer server, VertxMqttEndpoint endpoint) {
    VertxMqttEndpointManager manager = server.getEndpointManager();
    if (manager.hasClientId(endpoint.clientIdentifier())) {
      manager.removeEndpoint(endpoint.clientIdentifier());
    }
    log.trace("clientId[{}], Connection close", endpoint.clientIdentifier());
  }

  @Override
  public void onDisconnect(VertxMqttServer server, VertxMqttEndpoint endpoint) {
    log.trace("clientId[{}], Received disconnect from client", endpoint.clientIdentifier());
  }

}
