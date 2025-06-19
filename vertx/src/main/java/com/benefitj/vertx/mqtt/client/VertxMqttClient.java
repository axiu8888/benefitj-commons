package com.benefitj.vertx.mqtt.client;

import com.alibaba.fastjson2.JSON;
import com.benefitj.core.AutoConnectTimer;
import com.benefitj.core.IdUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.vertx.VerticleInitializer;
import com.benefitj.vertx.VertxHolder;
import com.benefitj.vertx.VertxManager;
import com.benefitj.vertx.VertxVerticle;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.SocketAddress;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;
import io.vertx.mqtt.messages.MqttConnAckMessage;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * MQTT 客户端
 */
public class VertxMqttClient extends VertxVerticle<VertxMqttClient> {

  public static final VerticleInitializer NONE = v -> { /* ignore */ };

  private static final Handler<AsyncResult<Integer>> IGNORE_HANDLER = event -> { /* ignore */ };

  /**
   * 代理处理器
   */
  private final VertxMqttClientHandler proxyHandler
      = ProxyUtils.newListProxy(VertxMqttClientHandler.class, new CopyOnWriteArrayList<>(Collections.singletonList(VertxMqttClientHandler.Impl.get())));

  private final AutoConnectTimer.Connector connector = new AutoConnectTimer.Connector() {
    @Override
    public boolean isConnected() {
      return self.isConnected();
    }

    @Override
    public void doConnect() {
      self.reconnect();
    }
  };

  /**
   * 配置参数
   */
  private MqttClientOptions options = new MqttClientOptions();
  /**
   * 远程主机:端口
   */
  private SocketAddress remoteAddress;
  /**
   * 客户端
   */
  private MqttClient original;
  /**
   * 重新连接的定时器
   */
  private AutoConnectTimer autoConnectTimer = AutoConnectTimer.NONE;
  /**
   * 初始化器
   */
  private VerticleInitializer<VertxMqttClient> initializer = NONE;

  @Override
  public void start() throws Exception {
    if (getOriginal() != null) {
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
    this.setOriginal(MqttClient.create(getVertx(), getOptions()));

    this.getOriginal()
        // ping 消息
        .pingResponseHandler(ignore -> proxyHandler.onPingResponse(self))
        // 订阅成功
        .subscribeCompletionHandler(message -> proxyHandler.onSubscribeCompletion(self, message))
        // 取消订阅成功
        .unsubscribeCompletionHandler(messageId -> proxyHandler.onUnsubscribeCompletion(self, messageId))
        // 发布消息
        .publishHandler(message -> proxyHandler.onPublishMessage(self, message))
        // 发布完成
        .publishCompletionHandler(messageId -> proxyHandler.onPublishCompletion(self, messageId))
        // 发布超时
        .publishCompletionExpirationHandler(messageId -> proxyHandler.onPublishCompletionExpiration(self, messageId))
        // 发布完成未知包ID
        .publishCompletionUnknownPacketIdHandler(messageId -> proxyHandler.onPublishCompletionUnknownPacketId(self, messageId))
        // 异常
        .exceptionHandler(error -> proxyHandler.onException(self, error))
        // 关闭
        .closeHandler(ignore -> {
          // 重连
          getAutoConnectTimer().start(connector);
          // 连接断开
          proxyHandler.onClose(self);
        });

    VertxManager.get().safeLocalThread(() -> {
      SocketAddress remote = remoteAddress();
      //异步连接，同步会阻塞线程
      eventLoop().submit(() -> await(this.getOriginal().connect(remote.port(), remote.host()), (res -> {// 连接
        if (res.succeeded()) {
          MqttConnAckMessage ackMsg = res.result();
          log.trace("mqtt client connect success, ack: {}", JSON.toJSONString(ackMsg));
        } else {
          log.trace("mqtt client connect fail: {}", res.cause().getMessage());
          getAutoConnectTimer().start(connector);
        }
        proxyHandler.onConnected(self, res, false);
      }))).get();
    });
  }

  @Override
  public void stop() throws Exception {
    MqttClient original = this.getOriginal();
    if (original != null) {
      this.setOriginal(null);
      SocketAddress remote = remoteAddress();
      original.disconnect().onComplete(event ->
          log.trace("mqtt client disconnected, {}, status: {}", remote, event.succeeded()));
      getAutoConnectTimer().setAutoConnect(false).stop();
    }
  }

  /**
   * 部署
   */
  public Future<String> deploy(Vertx vertx) {
    return vertx.deployVerticle(this);
  }

  /**
   * 卸载
   */
  public Future<Void> undeploy() {
    if (getOriginal() != null) {
      return getVertx().undeploy(deploymentID());
    }
    return Future.succeededFuture();
  }

  public MqttClient getOriginal() {
    return original;
  }

  protected VertxMqttClient setOriginal(MqttClient original) {
    this.original = original;
    return this;
  }

  /**
   * 重连
   */
  public VertxMqttClient reconnect() {
    if (!isConnected()) {
      // 连接
      SocketAddress remote = remoteAddress();
      this.getOriginal().connect(remote.port(), remote.host()).onComplete(event -> {
        if (event.succeeded()) {
          // 重连成功
          proxyHandler.onConnected(self, event, true);
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

  public SocketAddress remoteAddress() {
    return remoteAddress;
  }

  public VertxMqttClient remoteAddress(SocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
    return this;
  }

  public VertxMqttClient removeHandler(VertxMqttClientHandler handler) {
    if (handler != null) ((List) proxyHandler).remove(handler);
    return this;
  }

  public VertxMqttClient addHandler(VertxMqttClientHandler handler) {
    if (handler != null && !((List) proxyHandler).contains(handler))
      ((List) proxyHandler).add(handler);
    return this;
  }

  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public VertxMqttClient setAutoConnectTimer(AutoConnectTimer autoConnectTimer) {
    this.autoConnectTimer = autoConnectTimer != null ? autoConnectTimer : AutoConnectTimer.NONE;
    return this;
  }

  public VerticleInitializer<VertxMqttClient> getInitializer() {
    return initializer;
  }

  public VertxMqttClient setInitializer(VerticleInitializer<VertxMqttClient> initializer) {
    this.initializer = initializer;
    return this;
  }

  public Future<String> connect() {
    return getOriginal() == null ? deploy(VertxHolder.getVertx()) : Future.succeededFuture();
  }

  public Future<Void> disconnect() {
    return getOriginal() != null ? undeploy() : Future.succeededFuture();
  }

  /**
   * 是否已连接
   */
  public boolean isConnected() {
    MqttClient client = getOriginal();
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
    getOriginal().subscribe(topic, qosLevel.value()).onComplete(subscribeSentHandler);
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
    getOriginal().subscribe(topics).onComplete(subscribeSentHandler);
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
      getOriginal().unsubscribe(topic).onComplete(unsubscribeSentHandler);
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
    MqttClient original = getOriginal();
    if (original != null && original.isConnected()) {
      original.publish(topic, payload, qosLevel, isDup, isRetain).onComplete(publishSentHandler);
    } else {
      throw new IllegalStateException("MQTT 未连接!");
    }
    return this;
  }

}
