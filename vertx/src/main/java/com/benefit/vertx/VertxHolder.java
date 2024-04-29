package com.benefit.vertx;

import com.benefit.vertx.mqtt.client.VertxMqttClient;
import com.benefit.vertx.mqtt.client.VertxMqttMessageDispatcher;
import com.benefitj.core.SingletonSupplier;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.*;

import java.util.concurrent.atomic.AtomicReference;


public class VertxHolder {

  /**
   * singleton instance
   */
  static final SingletonSupplier<Vertx> single = SingletonSupplier.of(Vertx::vertx);

  /**
   * 获取Vertx实例
   */
  public static Vertx get() {
    return single.get();
  }

  /**
   * 部署
   *
   * @param verticle Verticle对象
   * @return 返回部署结果
   */
  public static Future<String> deploy(Verticle verticle) {
    return deploy(verticle, new DeploymentOptions());
  }

  /**
   * 部署
   *
   * @param verticle Verticle对象
   * @param options  部署参数
   * @return 返回部署结果
   */
  public static Future<String> deploy(Verticle verticle, DeploymentOptions options) {
    return get().deployVerticle(verticle, options);
  }

  /**
   * 取消部署
   *
   * @param verticle Verticle对象
   * @return 返回结果
   */
  public static Future<Void> undeploy(AbstractVerticle verticle) {
    return undeploy(verticle.deploymentID());
  }

  /**
   * 取消部署
   *
   * @param deploymentID verticle ID
   * @return 返回结果
   */
  public static Future<Void> undeploy(String deploymentID) {
    return get().undeploy(deploymentID);
  }

  /**
   * 创建 TCP Server
   */
  public static NetServer createNetServer(NetServerOptions options) {
    return get().createNetServer(options);
  }

  /**
   * 创建 TCP client
   */
  public static NetClient createNetClient(NetClientOptions options) {
    return get().createNetClient(options);
  }

  /**
   * 创建 HTTP Server
   */
  public static HttpServer createHttpServer(HttpServerOptions options) {
    return get().createHttpServer(options);
  }

  /**
   * 创建MQTT客户端
   *
   * @param remote     远程地址
   * @param dispatcher 订阅和消息分发
   * @return 返回MQTT客户端
   */
  public static VertxMqttClient createMqttClient(SocketAddress remote,
                                                 VertxMqttMessageDispatcher dispatcher) {
    return createMqttClient(remote, dispatcher, null);
  }

  /**
   * 创建MQTT客户端
   *
   * @param remote           远程地址
   * @param dispatcher       订阅和消息分发
   * @param autoConnectTimer 自动连接器
   * @return 返回MQTT客户端
   */
  public static VertxMqttClient createMqttClient(SocketAddress remote,
                                                 VertxMqttMessageDispatcher dispatcher,
                                                 AutoConnectTimer autoConnectTimer) {
    AtomicReference<VertxMqttClient> ref = new AtomicReference<>();
    ref.set(
        new VertxMqttClient()
            .addHandler(dispatcher)
            .setInitializer(verticle -> {})
            .setAutoConnectTimer(autoConnectTimer)
            .setRemoteAddress(remote.host(), remote.port())
    );
    deploy(ref.get());
    return ref.get();
  }
}
