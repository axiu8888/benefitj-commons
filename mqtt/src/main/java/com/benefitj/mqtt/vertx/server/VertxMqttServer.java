/*
 * Copyright 2016 Red Hat Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.benefitj.mqtt.vertx.server;

import com.benefitj.mqtt.MqttTopic;
import com.benefitj.mqtt.vertx.VerticleInitializer;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.net.PemKeyCertOptions;
import io.vertx.mqtt.MqttEndpoint;
import io.vertx.mqtt.MqttServer;
import io.vertx.mqtt.MqttServerOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * MQTT 服务端
 */
public class VertxMqttServer extends AbstractVerticle {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  protected static VerticleInitializer DISCARD = verticle -> { /* ignore */ };

  private static MqttAuthenticator NONE_MQTT_AUTHENTICATOR = endpoint -> true;

  private MqttServerProperty property;
  /**
   * 服务端配置
   */
  private MqttServerOptions options = new MqttServerOptions();
  /**
   * 服务端
   */
  private MqttServer mqttServer;
  /**
   * 客户端状态处理器
   */
  private MqttEndpointHandler endpointHandler;
  /**
   * 认证器
   */
  private MqttAuthenticator authenticator = NONE_MQTT_AUTHENTICATOR;
  /**
   * 客户端
   */
  private VertxMqttEndpointManager endpointManager = VertxMqttEndpointManager.get();
  /**
   * 初始化器
   */
  private VerticleInitializer<VertxMqttServer> initializer = DISCARD;

  @Override
  public void start() {
    if (getMqttServer() != null) {
      return;
    }

    if (getOptions() == null) {
      this.setOptions(new MqttServerOptions());
    }

    MqttServerProperty prop = getProperty();
    // SSL
    if (prop.isSsl()) {
      getOptions()
          .setKeyCertOptions(new PemKeyCertOptions()
              .setKeyPath(prop.getKeyPath())
              .setCertPath(prop.getCertPath()));
    }
    getOptions()
        .setMaxMessageSize(prop.getMaxMessageSize())
        .setUseWebSocket(prop.isUseWebSocket())
        .setPort(prop.getPort())
        .setHost(prop.getHost())
        .setWebSocketMaxFrameSize(prop.getWsMaxFrameSize())
    ;
    // 初始化
    getInitializer().onInitialize(this);
    // 创见服务端
    this.setMqttServer(MqttServer.create(getVertx(), getOptions()));
    // 监听客户端连接
    this.getMqttServer().endpointHandler(this::onEndpointHandle);
    this.getMqttServer().exceptionHandler(this::onExceptionHandle);
    // 监听
    this.getMqttServer().listen(ar -> {
      if (ar.succeeded()) {
        log.trace("MQTT server started and listening on port {}", mqttServer.actualPort());
      } else {
        log.warn("MQTT server error on start: " + ar.cause().getMessage(), ar.cause());
      }
    });
  }

  @Override
  public void stop() {
    final MqttServer server = this.getMqttServer();
    if (server != null) {
      this.setMqttServer(null);
      // 重置服务端
      server.close()
          .onSuccess(event -> log.trace("MQTT server stop success on port {}", server.actualPort()))
          .onFailure(err -> log.warn("MQTT server stop failure on port {}", server.actualPort() + ", error: " + err.getMessage()))
          // 清空连接
          .onComplete(event -> {
            VertxMqttEndpointManager endpoints = getEndpointManager();
            endpoints.forEach((clientId, endpoint) -> {
              if (!endpoint.isConnected()) {
                endpoints.remove(endpoint);
              }
            });
          });
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
    if (getMqttServer() != null) {
      return getVertx().undeploy(deploymentID());
    }
    return Future.succeededFuture();
  }

  /**
   * 客户端连接处理
   *
   * @param endpoint 客户端
   */
  protected void onEndpointHandle(MqttEndpoint endpoint) {
    // 处理连接
    getEndpointHandler().onConnect(VertxMqttServer.this, endpoint);
    VertxMqttEndpoint vme = getEndpointManager().getEndpoint(endpoint.clientIdentifier());
    endpoint
        // 订阅主题
        .subscribeHandler(message -> {
          // 订阅
          List<Subscription> subscriptions = message.topicSubscriptions()
              .stream()
              .map(Subscription::new)
              .collect(Collectors.toList());
          getEndpointHandler().onSubscribe(VertxMqttServer.this, vme, message, subscriptions);
        })
        // specifing handlers for handling QoS 1 and 2
        .publishAcknowledgeHandler(messageId ->
            getEndpointHandler().onPublishMessageState(VertxMqttServer.this, vme, messageId, PublishMessageState.ACKNOWLEDGE))
        .publishReceivedHandler(messageId ->
            getEndpointHandler().onPublishMessageState(VertxMqttServer.this, vme, messageId, PublishMessageState.RECEIVED))
        .publishReleaseHandler(messageId ->
            getEndpointHandler().onPublishMessageState(VertxMqttServer.this, vme, messageId, PublishMessageState.RELEASE))
        .publishCompletionHandler(messageId ->
            getEndpointHandler().onPublishMessageState(VertxMqttServer.this, vme, messageId, PublishMessageState.COMPLETION))
        // 取消订阅
        .unsubscribeHandler(message -> {
          List<MqttTopic> topics = message.topics()
              .stream()
              .map(MqttTopic::get)
              .collect(Collectors.toList());
          getEndpointHandler().onUnsubscribe(VertxMqttServer.this, vme, message, topics);
        })
        // ping 消息
        .pingHandler(v -> getEndpointHandler().onPingMessage(VertxMqttServer.this, vme))
        // 发送数据
        .publishHandler(message -> getEndpointHandler().onPublishMessage(VertxMqttServer.this, vme, message))
        // close
        .closeHandler(event -> getEndpointHandler().onClose(VertxMqttServer.this, vme))
        // disconnect
        .disconnectHandler(event -> getEndpointHandler().onDisconnect(VertxMqttServer.this, vme))
    ;
  }

  /**
   * 异常处理
   *
   * @param error 异常
   */
  protected void onExceptionHandle(Throwable error) {
    log.warn(error.getMessage(), error);
  }

  public MqttServerProperty getProperty() {
    return property;
  }

  /**
   * 设置服务端的配置
   *
   * @param property 配置
   * @return 返回当前服务对象
   */
  public VertxMqttServer setProperty(MqttServerProperty property) {
    this.property = property;
    return this;
  }

  public MqttServerOptions getOptions() {
    return options;
  }

  public VertxMqttServer setOptions(MqttServerOptions options) {
    this.options = options;
    return this;
  }

  public VerticleInitializer<VertxMqttServer> getInitializer() {
    return initializer;
  }

  public VertxMqttServer setInitializer(VerticleInitializer<VertxMqttServer> initializer) {
    this.initializer = initializer;
    return this;
  }

  public MqttServer getMqttServer() {
    return mqttServer;
  }

  public VertxMqttServer setMqttServer(MqttServer mqttServer) {
    this.mqttServer = mqttServer;
    return this;
  }

  public VertxMqttEndpointManager getEndpointManager() {
    return endpointManager;
  }

  public void setEndpointManager(VertxMqttEndpointManager endpoints) {
    this.endpointManager = endpoints;
  }

  /**
   * 获取MQTT客户端处理回调
   */
  public MqttEndpointHandler getEndpointHandler() {
    return endpointHandler;
  }

  /**
   * 设置客户端处理回调
   *
   * @param endpointHandler MQTT客户端处理回调
   */
  public VertxMqttServer setEndpointHandler(MqttEndpointHandler endpointHandler) {
    this.endpointHandler = endpointHandler;
    return this;
  }

  /**
   * 获取认证器
   */
  public MqttAuthenticator getAuthenticator() {
    return authenticator;
  }

  /**
   * 设置认证器
   *
   * @param authenticator 认证器
   * @return 返回当前服务对象
   */
  public VertxMqttServer setAuthenticator(MqttAuthenticator authenticator) {
    this.authenticator = authenticator;
    return this;
  }

}

