package com.benefitj.vertx;

import com.benefitj.core.AutoConnectTimer;
import com.benefitj.vertx.mqtt.client.VertxMqttClient;
import com.benefitj.vertx.mqtt.client.VertxMqttMessageDispatcher;
import com.benefitj.core.CatchUtils;
import io.vertx.core.*;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;


public class VertxHolder {

  /**
   * singleton instance
   */
  static final AtomicReference<Vertx> holder = new AtomicReference<>(Vertx.vertx());

  public static void setVertx(Vertx vertx) {
    holder.set(vertx);
  }

  /**
   * 获取Vertx实例
   */
  public static Vertx getVertx() {
    return holder.get();
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
    return getVertx().deployVerticle(verticle, options);
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
    return getVertx().undeploy(deploymentID);
  }

  /**
   * 创建 TCP Server
   */
  public static NetServer createNetServer(NetServerOptions options) {
    return getVertx().createNetServer(options);
  }

  /**
   * 创建 TCP client
   */
  public static NetClient createNetClient(NetClientOptions options) {
    return getVertx().createNetClient(options);
  }

  /**
   * 创建 HTTP Server
   */
  public static HttpServer createHttpServer(HttpServerOptions options) {
    return getVertx().createHttpServer(options);
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

  /**
   * 部署
   */
  public static <T extends Verticle> T deploy(Vertx vertx, T verticle) {
    CountDownLatch latch = new CountDownLatch(1);
    AtomicReference<Throwable> errRef = new AtomicReference<>();
    vertx
        .deployVerticle(verticle)
        .onComplete(event -> {
          errRef.set(event.succeeded() ? null : event.cause());
          latch.countDown();
        });
    CatchUtils.ignore(() -> latch.await());
    if (errRef.get() != null) {
      throw new IllegalStateException(errRef.get());
    }
    return verticle;
  }

}
