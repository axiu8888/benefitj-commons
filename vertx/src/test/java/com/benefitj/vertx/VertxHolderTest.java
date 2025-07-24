package com.benefitj.vertx;

import com.benefitj.core.AutoConnectTimer;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.log.Slf4jLevel;
import com.benefitj.core.log.Slf4jLogger;
import com.benefitj.vertx.mqtt.client.VertxMqttClient;
import com.benefitj.vertx.mqtt.client.VertxMqttMessageDispatcher;
import com.benefitj.vertx.mqtt.server.MqttServerHolder;
import com.benefitj.vertx.tcp.client.VertxTcpClient;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.SocketAddress;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.proxy.handler.ProxyHandler;
import io.vertx.httpproxy.HttpProxy;
import io.vertx.httpproxy.ProxyOptions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;


@Slf4j
class VertxHolderTest {

  @BeforeEach
  void setUp() {
    log.info("------------------- setUp -------------------");
    Logger log = LoggerFactory.getLogger("vertx");
    VertxLogger.set(Slf4jLogger.newProxy(log, Slf4jLevel.INFO));
  }

  @AfterEach
  void tearDown() {
    log.info("------------------- tearDown -------------------");
  }

  @Test
  void test_mqttServer() {
    MqttServerHolder.getTcp().start();
    MqttServerHolder.getWs().start();
    EventLoop.sleepSecond(1000);
  }

  @Test
  void test_mqttClient() {
    VertxMqttClient client = VertxHolder.createMqttClient(
        SocketAddress.inetSocketAddress(2883, "192.168.1.198")
        , new VertxMqttMessageDispatcher(true));
    client.setAutoConnectTimer(new AutoConnectTimer(true, Duration.ofSeconds(10)));
    String result = client.connect().await();
    log.info("connect result -->: {}", result);

    for (int i = 0; i < 1000; i++) {
      // 发布
      if (client.isConnected()) client.publish("/device/123456", i + " test...");
      EventLoop.sleepSecond(1);
    }

    EventLoop.sleepSecond(1000);
  }

  @Test
  void createNetServer() {
  }

  @Test
  void createNetClient() {
    VertxTcpClient tcp = new VertxTcpClient(
        new NetClientOptions()
            .setReceiveBufferSize(8 * (1024 << 10))
            .setSendBufferSize(8 * (1024 << 10))
            .setReusePort(true)
            .setReconnectInterval(10_000) // 自动重连的间隔
            .setReconnectAttempts(0) // 尝试重连次数
    );
    tcp.setAutoConnectTimer(new AutoConnectTimer(true, Duration.ofSeconds(10)));
    tcp.addListener((client, buf) -> {
      String msg = new String(buf.getBytes(), StandardCharsets.UTF_8);
      log.info("[{}] rcv msg ==>: {}", client.remoteAddress(), msg);
    });
    tcp.connect("127.0.0.1", 8088).await();

    log.info("tcp.isActive: {}", tcp.isActive());

    for (int i = 0; i < 1000; i++) {
      if (!tcp.isActive()) {
        log.info("active: {}, {}, {}", tcp.isActive(), tcp.remoteAddress(), DateFmtter.fmtNowS());
      } else {
        tcp.write("hello, 当前时间: " + DateFmtter.fmtNowS())
            .onComplete(res -> {
              log.info("send: {}{}", res.succeeded(), res.cause() != null ? ", cause: " + res.cause().getMessage() : "");
            });
      }
      EventLoop.sleepSecond(1);
    }
    EventLoop.sleepSecond(5);
  }

  @Test
  void createHttpServer() {
//    VertxHolder.deploy(new HttpProxyVerticle());


    Vertx vertx = VertxHolder.getVertx();

    HttpServerOptions serverOptions = new HttpServerOptions()
        .setPort(80)
        .setReusePort(true)
        .setMaxHeaderSize(10 * (1024 << 10))//最大10兆
        .setCompressionLevel(1)
        .setCompressionSupported(true)
        .setReceiveBufferSize(4 * (1024 << 10));

    Router router = Router.router(vertx);
    // 设置路由处理
    router.get("/api/ping").handler(ctx -> {
      HttpServerRequest request = ctx.request();
      ctx.response()
          .putHeader("content-type", "text/plain")
          .end("Pong from Vert.x on Android!")
          .onComplete(result -> {
            log.info("{} -> {}, success: {}", request.method(), request.absoluteURI(), result.succeeded());
          });
    });


    // 1. 创建代理处理器
    HttpClientAgent clientAgent = vertx.httpClientBuilder()
        .with(new PoolOptions())
        .with(new HttpClientOptions())
        .build();
    HttpProxy proxy = HttpProxy
        .reverseProxy(new ProxyOptions().setSupportWebSocket(true), clientAgent)
        .origin(80, "192.168.1.198");
    // ✅ /support/api/* → 代理转发
    // 2. 注册代理路由 - 匹配 /support/api 开头的请求
    router.route("/support/api/*").handler(ProxyHandler.create(proxy));

    String staticDir = "E:/.tmp/cache/support/web-front/support";
    router.routeWithRegex("/support(?!/api).*").handler(ctx -> {
      ctx.response().sendFile(staticDir + "/index.html");
    });
    // 配置静态资源处理器，指向复制后的目录
    // 注意：这里我们设置根目录为内部存储的www目录，因此可以通过/support/...访问
    StaticHandler staticHandler = StaticHandler.create(staticDir);
    //staticHandler.setCachingEnabled(false); // 开发时禁用缓存
    // 将静态资源处理器挂载到路由上，比如所有以/support开头的请求
    router.route("/support/*").handler(staticHandler);


    HttpServer server = VertxHolder.createHttpServer(serverOptions);
    server
        .requestHandler(router)
        .listen(serverOptions.getPort())
        .onComplete(http -> {
          if (http.succeeded()) {
            log.info("start http server success, port: {}", http.result().actualPort());
          } else {
            log.warn("start http server fail, port: {}, cause: {}", http.result().actualPort(), http.cause().getMessage());
          }
        });

    EventLoop.sleepSecond(Integer.MAX_VALUE);
  }


}