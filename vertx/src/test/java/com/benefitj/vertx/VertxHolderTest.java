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
import io.vertx.core.Handler;
import io.vertx.core.http.*;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.SocketAddress;
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
      if(client.isConnected()) client.publish("/device/123456", i + " test...");
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

    HttpServerOptions serverOptions = new HttpServerOptions()
        .setPort(8080)
        .setReusePort(true)
        .setMaxHeaderSize(10 * (1024 << 10))//最大10兆
        .setCompressionLevel(1)
        .setCompressionSupported(true)
        .setReceiveBufferSize(4 * (1024 << 10));
    HttpServer server = VertxHolder.createHttpServer(serverOptions)
        .connectionHandler(new Handler<HttpConnection>() {
          @Override
          public void handle(HttpConnection event) {
          }
        })
        .exceptionHandler(new Handler<Throwable>() {
          @Override
          public void handle(Throwable e) {
            log.error("throw: " + e.getMessage(), e);
          }
        })
        .requestHandler(new Handler<HttpServerRequest>() {
          @Override
          public void handle(HttpServerRequest serverRequest) {
            HttpServerResponse serverResponse = serverRequest.response();
          }
        })
        .webSocketHandshakeHandler(new Handler<ServerWebSocketHandshake>() {
          @Override
          public void handle(ServerWebSocketHandshake event) {
          }
        })
        .invalidRequestHandler(new Handler<HttpServerRequest>() {
          @Override
          public void handle(HttpServerRequest event) {

          }
        })
        //
        ;
    server.listen().onComplete(res -> {
      if (res.succeeded()) {
        log.info("start http server success, port: {}", res.result().actualPort());
      } else {
        log.info("start http server fail, port: {}, cause: {}", res.result().actualPort(), res.cause().getMessage());
      }
    });


    EventLoop.sleepSecond(1000);
  }

}