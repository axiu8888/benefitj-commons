package com.benefitj.vertx.mqtt.client;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.AutoConnectTimer;
import com.benefitj.core.IdUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.log.ILogger;
import com.benefitj.vertx.VerticleInitializer;
import com.benefitj.vertx.VertxLogger;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MQTT 客户端
 */
public class VertxMqttClient extends AbstractVerticle implements AttributeMap {

  public static final VerticleInitializer NONE = v -> { /* ignore */ };

  private static final Handler<AsyncResult<Integer>> IGNORE_HANDLER = event -> { /* ignore */ };

  protected ILogger log = VertxLogger.get();
  /**
   * 属性
   */
  private final Map<String, Object> attrs = new ConcurrentHashMap<>();
  /**
   * 代理处理器
   */
  private final VertxMqttClientHandler proxyHandler = ProxyUtils.newListProxy(
      VertxMqttClientHandler.class, new CopyOnWriteArrayList<>(Collections.singletonList(VertxMqttClientHandlerImpl.get())));

  private final AutoConnectTimer.Connector connector = new AutoConnectTimer.Connector() {
    @Override
    public boolean isConnected() {
      return self_().isConnected();
    }

    @Override
    public void doConnect() {
      self_().reconnect();
    }
  };

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
  private MqttClient raw;
  /**
   * 重新连接的定时器
   */
  private AutoConnectTimer autoConnectTimer = AutoConnectTimer.NONE;
  /**
   * 初始化器
   */
  private VerticleInitializer<VertxMqttClient> initializer = NONE;

  @Override
  public Map<String, Object> attrs() {
    return attrs;
  }

  protected VertxMqttClient self_() {
    return this;
  }

  @Override
  public void start() throws Exception {
    if (getRaw() != null) {
      return;
    }

    if (StringUtils.isBlank(getOptions().getClientId())) {
      getOptions().setClientId(IdUtils.nextLetterId("vertx-", null, 12));
    }
    AutoConnectTimer act = getAutoConnectTimer();
    getOptions().setConnectTimeout(Math.min((int) act.getInterval().toMillis(), getOptions().getConnectTimeout()));
    // 初始化
    this.getInitializer().onInitialize(this);
    // 创建客户端
    this.setRaw(MqttClient.create(getVertx(), getOptions()));

    this.getRaw()
        // 连接
        .connect(getPort(), getHost(), event -> {
          if (!event.succeeded())
            getAutoConnectTimer().start(connector);
          proxyHandler.onConnected(self_(), event, false);
        })
        // ping 消息
        .pingResponseHandler(ignore -> proxyHandler.onPingResponse(self_()))
        // 订阅成功
        .subscribeCompletionHandler(message -> proxyHandler.onSubscribeCompletion(self_(), message))
        // 取消订阅成功
        .unsubscribeCompletionHandler(messageId -> proxyHandler.onUnsubscribeCompletion(self_(), messageId))
        // 发布消息
        .publishHandler(message -> proxyHandler.onPublishMessage(self_(), message))
        // 发布完成
        .publishCompletionHandler(messageId -> proxyHandler.onPublishCompletion(self_(), messageId))
        // 发布超时
        .publishCompletionExpirationHandler(messageId -> proxyHandler.onPublishCompletionExpiration(self_(), messageId))
        // 发布完成未知包ID
        .publishCompletionUnknownPacketIdHandler(messageId -> proxyHandler.onPublishCompletionUnknownPacketId(self_(), messageId))
        // 异常
        .exceptionHandler(error -> proxyHandler.onException(self_(), error))
        // 关闭
        .closeHandler(ignore -> {
          // 重连
          getAutoConnectTimer().start(connector);
          // 连接断开
          proxyHandler.onClose(self_());
        })
    ;
  }

  @Override
  public void stop() throws Exception {
    MqttClient client = this.getRaw();
    if (client != null) {
      this.setRaw(null);
      client.disconnect(event ->
          log.trace("mqtt client disconnected, {}:{}, status: {}", getHost(), getPort(), event.succeeded()));
      getAutoConnectTimer().setAutoConnect(false).stop();
    }
  }

  public MqttClient getRaw() {
    return raw;
  }

  public VertxMqttClient setRaw(MqttClient raw) {
    this.raw = raw;
    return self_();
  }

  /**
   * 重连
   */
  public VertxMqttClient reconnect() {
    if (!isConnected()) {
      // 连接
      this.getRaw().connect(getPort(), getHost(), event -> {
        if (event.succeeded()) {
          // 重连成功
          proxyHandler.onConnected(self_(), event, true);
        }
      });
    }
    return self_();
  }

  public MqttClientOptions getOptions() {
    return options;
  }

  public VertxMqttClient setOptions(MqttClientOptions options) {
    this.options = options;
    return self_();
  }

  public String getHost() {
    return host;
  }

  public VertxMqttClient setHost(String host) {
    this.host = host;
    return self_();
  }

  public Integer getPort() {
    return port;
  }

  public VertxMqttClient setPort(Integer port) {
    this.port = port;
    return self_();
  }

  public VertxMqttClient setRemoteAddress(String host, Integer port) {
    return setHost(host).setPort(port);
  }

  public VertxMqttClient removeHandler(VertxMqttClientHandler handler) {
    if (handler != null) ((List) proxyHandler).remove(handler);
    return self_();
  }

  public VertxMqttClient addHandler(VertxMqttClientHandler handler) {
    if (handler != null && !((List) proxyHandler).contains(handler))
      ((List) proxyHandler).add(handler);
    return self_();
  }

  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public VertxMqttClient setAutoConnectTimer(AutoConnectTimer autoConnectTimer) {
    this.autoConnectTimer = autoConnectTimer != null ? autoConnectTimer : AutoConnectTimer.NONE;
    return self_();
  }

  public VerticleInitializer<VertxMqttClient> getInitializer() {
    return initializer;
  }

  public VertxMqttClient setInitializer(VerticleInitializer<VertxMqttClient> initializer) {
    this.initializer = initializer;
    return self_();
  }

  /**
   * 是否已连接
   */
  public boolean isConnected() {
    MqttClient client = getRaw();
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
    getRaw().subscribe(topic, qosLevel.value(), subscribeSentHandler);
    return self_();
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
   * @param topics 主题 和 服务质量
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(Map<String, Integer> topics) {
    return subscribe(topics, IGNORE_HANDLER);
  }

  /**
   * 订阅主题
   *
   * @param topics               主题 和 服务质量
   * @param subscribeSentHandler 订阅回调
   * @return 返回MQTT客户端
   */
  public VertxMqttClient subscribe(Map<String, Integer> topics, Handler<AsyncResult<Integer>> subscribeSentHandler) {
    getRaw().subscribe(topics, subscribeSentHandler);
    return self_();
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
      getRaw().unsubscribe(topic, unsubscribeSentHandler);
    }
    return self_();
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
    return self_();
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
    MqttClient raw = getRaw();
    if (raw != null && raw.isConnected()) {
      raw.publish(topic, payload, qosLevel, isDup, isRetain, publishSentHandler);
    } else {
      throw new IllegalStateException("MQTT 未连接!");
    }
    return self_();
  }

}
