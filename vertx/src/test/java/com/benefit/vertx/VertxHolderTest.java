package com.benefit.vertx;

import com.benefit.vertx.log.Slf4jVertxLogger;
import com.benefit.vertx.log.VertxLogger;
import com.benefit.vertx.mqtt.client.VertxMqttClient;
import com.benefit.vertx.mqtt.client.VertxMqttMessageDispatcher;
import com.benefit.vertx.mqtt.server.MqttServerHolder;
import com.benefit.vertx.tcp.ClientNetListener;
import com.benefit.vertx.tcp.VertxTcpClient;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.SocketAddress;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;


@Slf4j
class VertxHolderTest {

  @BeforeEach
  void setUp() {
    log.info("------------------- setUp -------------------");
    VertxLogger.set(Slf4jVertxLogger.create("vertx", Slf4jVertxLogger.Level.INFO));
  }

  @AfterEach
  void tearDown() {
    log.info("------------------- tearDown -------------------");
  }

  @Test
  void test_mqttServer() {
    MqttServerHolder mqtt_tcp = MqttServerHolder.getTcp();
    mqtt_tcp.start();
    EventLoop.sleepSecond(1000);
  }

  @Test
  void test_mqttClient() {

    VertxMqttMessageDispatcher dispatcher = new VertxMqttMessageDispatcher(true);
    VertxMqttClient client = VertxHolder.createMqttClient(SocketAddress.inetSocketAddress(1883, "192.168.1.198"), dispatcher);
    // 发布
    client.publish("/device/123456", "test...");

    EventLoop.sleepSecond(1000);
  }

  @Test
  void createNetServer() {
  }

  @Test
  void createNetClient() {
    VertxTcpClient tcp = new VertxTcpClient(
        new NetClientOptions()
            .setReceiveBufferSize(4 * (1024 << 10))
            .setSendBufferSize(4 * (1024 << 10))
            .setReusePort(true)
            //.setAutoConnect(true) // 是否自动重连
            .setReconnectInterval(3_000) // 自动重连的间隔
            .setReconnectAttempts(1) // 尚持重连次数
    );

    AtomicReference<CountDownLatch> ref = new AtomicReference<>();
    tcp.addListener(new ClientNetListener<VertxTcpClient>() {
      @Override
      public void onMessage(VertxTcpClient socket, Buffer buf) {
      }

      @Override
      public void onComplete(VertxTcpClient socket) {
        ref.get().countDown();
      }
    });
    for (int i = 0; i < 1000; i++) {
      if (!tcp.isActive()) {
        ref.set(new CountDownLatch(1));
        tcp.connect("127.0.0.1", 52014);
        CatchUtils.ignore(() -> ref.get().await());
      }
      EventLoop.sleepSecond(1);
    }
    EventLoop.sleepSecond(5);
  }

  @Test
  void createHttpServer() {
  }

}