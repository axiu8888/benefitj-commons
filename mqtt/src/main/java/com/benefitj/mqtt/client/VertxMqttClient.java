package com.benefitj.mqtt.client;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.IdUtils;
import com.benefitj.mqtt.VerticleInitializer;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * MQTT 客户端
 */
public class VertxMqttClient extends AbstractVerticle implements AttributeMap {

  protected static VerticleInitializer DISCARD = verticle -> { /* ignore */ };

  private static final Handler<AsyncResult<Integer>> IGNORE_HANDLER = event -> { /* ignore */ };

  protected Logger logger = LoggerFactory.getLogger(getClass());

  /**
   * 配置参数
   */
  private MqttClientOptions options = new MqttClientOptions();
  /**
   * 远程主机
   */
  private String host;
  /**
   * 端口
   */
  private Integer port = 1883;
  /**
   * 客户端
   */
  private MqttClient mqttClient;
  /**
   * 处理
   */
  private VertxClientHandler handler = new LoggingVertxClientHandler();
  /**
   * 重新连接的定时器
   */
  private AutoConnectTimer autoConnectTimer = new AutoConnectTimer(false);
  /**
   * 属性
   */
  private final Map<String, Object> attributes = new ConcurrentHashMap<>();
  /**
   * 初始化器
   */
  private VerticleInitializer<VertxMqttClient> initializer = DISCARD;

  @Override
  public Map<String, Object> attributes() {
    return attributes;
  }

  @Override
  public void start() throws Exception {
    if (getMqttClient() != null) {
      return;
    }

    if (StringUtils.isBlank(getOptions().getClientId())) {
      getOptions().setClientId(IdUtils.nextLetterId("vertx-", null, 12));
    }
    AutoConnectTimer acTimer = getAutoConnectTimer();
    getOptions().setConnectTimeout(Math.min((int) acTimer.getUnit().toMillis(acTimer.getPeriod()), getOptions().getConnectTimeout()));
    // 初始化
    this.getInitializer().onInitialize(this);
    // 创建客户端
    this.setMqttClient(MqttClient.create(getVertx(), getOptions()));

    this.getMqttClient()
        // 连接
        .connect(getPort(), getHost(), event -> {
          if (!event.succeeded()) {
            getAutoConnectTimer().start(VertxMqttClient.this);
          }
          getHandler().onConnected(VertxMqttClient.this, event, false);
        })
        // ping 消息
        .pingResponseHandler(ignore -> getHandler().onPingResponse(VertxMqttClient.this))
        // 订阅成功
        .subscribeCompletionHandler(message -> getHandler().onSubscribeCompletion(VertxMqttClient.this, message))
        // 取消订阅成功
        .unsubscribeCompletionHandler(messageId -> getHandler().onUnsubscribeCompletion(VertxMqttClient.this, messageId))
        // 发布消息
        .publishHandler(message -> getHandler().onPublishMessage(VertxMqttClient.this, message))
        // 发布完成
        .publishCompletionHandler(messageId -> getHandler().onPublishCompletion(VertxMqttClient.this, messageId))
        // 发布超时
        .publishCompletionExpirationHandler(messageId -> getHandler().onPublishCompletionExpiration(VertxMqttClient.this, messageId))
        // 发布完成未知包ID
        .publishCompletionUnknownPacketIdHandler(messageId -> getHandler().onPublishCompletionUnknownPacketId(VertxMqttClient.this, messageId))
        // 异常
        .exceptionHandler(error -> getHandler().onException(VertxMqttClient.this, error))
        // 关闭
        .closeHandler(ignore -> {
          // 重连
          getAutoConnectTimer().start(VertxMqttClient.this);
          // 连接断开
          getHandler().onClose(VertxMqttClient.this);
        })
    ;
  }

  @Override
  public void stop() throws Exception {
    MqttClient client = this.getMqttClient();
    if (client != null) {
      this.setMqttClient(null);
      client.disconnect(event ->
          logger.info("mqtt client disconnected, {}:{}, status: {}", getHost(), getPort(), event.succeeded()));
      getAutoConnectTimer().setAutoConnect(false).stop();
    }
  }

  public MqttClient getMqttClient() {
    return mqttClient;
  }

  public VertxMqttClient setMqttClient(MqttClient mqttClient) {
    this.mqttClient = mqttClient;
    return this;
  }

  /**
   * 重连
   */
  public VertxMqttClient reconnect() {
    if (!isConnected()) {
      // 连接
      this.getMqttClient().connect(getPort(), getHost(), event -> {
        if (event.succeeded()) {
          // 重连成功
          getHandler().onConnected(VertxMqttClient.this, event, true);
        }
      });
    }
    return this;
  }

  public MqttClientOptions getOptions() {
    return options;
  }

  public VertxMqttClient setOptions(MqttClientOptions options) {
    this.options = options;
    return this;
  }

  public String getHost() {
    return host;
  }

  public VertxMqttClient setHost(String host) {
    this.host = host;
    return this;
  }

  public Integer getPort() {
    return port;
  }

  public VertxMqttClient setPort(Integer port) {
    this.port = port;
    return this;
  }

  public VertxMqttClient setRemoteAddress(String host, Integer port) {
    return setHost(host).setPort(port);
  }

  public VertxClientHandler getHandler() {
    return handler;
  }

  public VertxMqttClient setHandler(VertxClientHandler handler) {
    this.handler = handler;
    return this;
  }

  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public VertxMqttClient setAutoConnectTimer(AutoConnectTimer autoConnectTimer) {
    this.autoConnectTimer = autoConnectTimer;
    return this;
  }

  public VerticleInitializer<VertxMqttClient> getInitializer() {
    return initializer;
  }

  public VertxMqttClient setInitializer(VerticleInitializer<VertxMqttClient> initializer) {
    this.initializer = initializer;
    return this;
  }

  /**
   * 是否已连接
   */
  public boolean isConnected() {
    MqttClient client = getMqttClient();
    return client != null && client.isConnected();
  }

  public String getClientId() {
    return getOptions().getClientId();
  }

  /**
   * 订阅主题
   *
   * @param topic 主题
   */
  public VertxMqttClient subscribe(String topic) {
    return subscribe(topic, MqttQoS.AT_LEAST_ONCE);
  }

  /**
   * 订阅主题
   *
   * @param topic    主题
   * @param qosLevel 服务质量
   */
  public VertxMqttClient subscribe(String topic, MqttQoS qosLevel) {
    return subscribe(topic, qosLevel, IGNORE_HANDLER);
  }

  /**
   * 订阅主题
   *
   * @param topic                主题
   * @param qosLevel             服务质量
   * @param subscribeSentHandler 订阅回调
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(String topic, MqttQoS qosLevel, Handler<AsyncResult<Integer>> subscribeSentHandler) {
    getMqttClient().subscribe(topic, qosLevel.value(), subscribeSentHandler);
    return this;
  }

  /**
   * 订阅主题
   *
   * @param topics 主题
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(List<String> topics) {
    return subscribe(topics, IGNORE_HANDLER);
  }

  /**
   * 订阅主题
   *
   * @param topics               主题
   * @param subscribeSentHandler 订阅回调
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(List<String> topics, Handler<AsyncResult<Integer>> subscribeSentHandler) {
    Map<String, Integer> topicMap = new HashMap<>(topics.size());
    topics.forEach(s -> topicMap.put(s, MqttQoS.AT_MOST_ONCE.value()));
    return subscribe(topicMap, subscribeSentHandler);
  }

  /**
   * 订阅主题
   *
   * @param topics 主题 & 服务质量
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(Map<String, Integer> topics) {
    return subscribe(topics, IGNORE_HANDLER);
  }

  /**
   * 订阅主题
   *
   * @param topics               主题 & 服务质量
   * @param subscribeSentHandler 订阅回调
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(Map<String, Integer> topics, Handler<AsyncResult<Integer>> subscribeSentHandler) {
    getMqttClient().subscribe(topics, subscribeSentHandler);
    return this;
  }

  /**
   * 取消订阅
   *
   * @param topic 主题
   * @return 返回MQTT客户端
   */
  public VertxMqttClient unsubscribe(String topic) {
    return unsubscribe(topic, IGNORE_HANDLER);
  }

  /**
   * 取消订阅
   *
   * @param topic                  主题
   * @param unsubscribeSentHandler 取消订阅处理
   * @return 返回MQTT客户端
   */
  public VertxMqttClient unsubscribe(String topic, Handler<AsyncResult<Integer>> unsubscribeSentHandler) {
    if (StringUtils.isNotBlank(topic)) {
      getMqttClient().unsubscribe(topic, unsubscribeSentHandler);
    }
    return this;
  }


  /**
   * 取消订阅
   *
   * @param topics 主题
   * @return 返回MQTT客户端
   */
  public VertxMqttClient unsubscribe(List<String> topics) {
    return unsubscribe(topics, IGNORE_HANDLER);
  }

  /**
   * 取消订阅
   *
   * @param topics                 主题
   * @param unsubscribeSentHandler 取消订阅处理
   * @return 返回MQTT客户端
   */
  public VertxMqttClient unsubscribe(List<String> topics, Handler<AsyncResult<Integer>> unsubscribeSentHandler) {
    if (topics != null) {
      for (String topic : topics) {
        unsubscribe(topic, unsubscribeSentHandler);
      }
    }
    return this;
  }

  /**
   * 发布消息
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, byte[] payload) {
    return publish(topic, payload, MqttQoS.AT_MOST_ONCE, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic   主题
   * @param payload 有效载荷
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, String payload) {
    return publish(topic, payload, MqttQoS.AT_MOST_ONCE, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qosLevel 服务质量
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, byte[] payload, MqttQoS qosLevel) {
    return publish(topic, payload, qosLevel, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic    主题
   * @param payload  有效载荷
   * @param qosLevel 服务质量
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, String payload, MqttQoS qosLevel) {
    return publish(topic, payload, qosLevel, IGNORE_HANDLER);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param publishSentHandler 发布处理
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, byte[] payload, MqttQoS qosLevel, Handler<AsyncResult<Integer>> publishSentHandler) {
    return publish(topic, Buffer.buffer(payload), qosLevel, publishSentHandler);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param publishSentHandler 发布处理
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, String payload, MqttQoS qosLevel, Handler<AsyncResult<Integer>> publishSentHandler) {
    return publish(topic, Buffer.buffer(payload), qosLevel, publishSentHandler);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param publishSentHandler 发布处理
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, Buffer payload, MqttQoS qosLevel, Handler<AsyncResult<Integer>> publishSentHandler) {
    return publish(topic, payload, qosLevel, false, false, publishSentHandler);
  }

  /**
   * 发布消息
   *
   * @param topic              主题
   * @param payload            有效载荷
   * @param qosLevel           服务质量
   * @param isDup              是否重发
   * @param isRetain           是否保留
   * @param publishSentHandler 发布处理
   * @return 返回MQTT客户端
   */
  public VertxMqttClient publish(String topic, Buffer payload, MqttQoS qosLevel, boolean isDup, boolean isRetain, Handler<AsyncResult<Integer>> publishSentHandler) {
    getMqttClient().publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler);
    return this;
  }

}
