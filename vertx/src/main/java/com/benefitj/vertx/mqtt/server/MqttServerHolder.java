package com.benefitj.vertx.mqtt.server;


import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.log.ILogger;
import com.benefitj.vertx.VertxHolder;
import com.benefitj.vertx.VertxLogger;

/**
 * MQTT 服务
 */
public class MqttServerHolder {

  static final SingletonSupplier<MqttServerHolder> tcp = SingletonSupplier.of(() -> new MqttServerHolder(1883, false));
  static final SingletonSupplier<MqttServerHolder> ws = SingletonSupplier.of(() -> new MqttServerHolder(8883, true));

  public static MqttServerHolder getTcp() {
    return tcp.get();
  }

  public static MqttServerHolder getWs() {
    return ws.get();
  }

  final ILogger log = VertxLogger.get();

  private MqttEndpointHandler handler;
  private VertxMqttEndpointManager manager;

  volatile VertxMqttServer server;

  public MqttServerHolder(int port, boolean useWS) {
    this(port, useWS, MqttEndpointHandler.get(), VertxMqttEndpointManager.get());
  }

  public MqttServerHolder(int port, boolean useWS,
                          MqttEndpointHandler handler,
                          VertxMqttEndpointManager manager) {
    this.handler = handler;
    this.manager = manager;
    this.server = create(port, useWS, handler, manager);
  }

  public MqttEndpointHandler getHandler() {
    return handler;
  }

  public void setHandler(MqttEndpointHandler handler) {
    this.handler = handler;
  }

  public VertxMqttEndpointManager getManager() {
    return manager;
  }

  public void setManager(VertxMqttEndpointManager manager) {
    this.manager = manager;
  }

  public VertxMqttServer getServer() {
    return server;
  }

  public void start() {
    server.deploy(VertxHolder.getVertx());
  }

  public void stop() {
    server.undeploy();
  }

  /**
   * 创建MQTT服务
   *
   * @param port            端口
   * @param useWS           是否使用WebSocket
   * @param endpointHandler 短点消息处理
   * @param endpointManager 客户端管理
   * @return 返回服务端
   */
  public static VertxMqttServer create(int port,
                                       boolean useWS,
                                       MqttEndpointHandler endpointHandler,
                                       VertxMqttEndpointManager endpointManager) {
    MqttServerProperty property = new MqttServerProperty();
    property.setPort(port);
    property.setUseWebSocket(useWS);
    VertxMqttServer server = new VertxMqttServer();
    server.setAuthenticator(endpoint -> true);  // 认证
    server.setProperty(property);
    server.setEndpointHandler(endpointHandler);
    server.setEndpointManager(endpointManager);
    return server;
  }

}
