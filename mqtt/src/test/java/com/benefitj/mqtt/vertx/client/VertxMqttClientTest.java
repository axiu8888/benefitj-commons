package com.benefitj.mqtt.vertx.client;

import com.benefitj.core.EventLoop;
import com.benefitj.mqtt.vertx.VertxHolder;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 测试客户端
 */
@Slf4j
public class VertxMqttClientTest {

  private VertxMqttClient client;

  private VertxMqttMessageDispatcher dispatcher = new VertxMqttMessageDispatcher(true);

  @Before
  public void setUp() throws Exception {
    final CountDownLatch latch = new CountDownLatch(1);
    VertxHolder.deploy(client = new VertxMqttClient()
        .setHandler(dispatcher)
        .setInitializer(verticle -> {
          // 初始化
        })
        .setAutoConnectTimer(new AutoConnectTimer(true).setPeriod(10))
        .setRemoteAddress("127.0.0.1", 1883))
//        .setRemoteAddress("192.168.85.128", 1883))
        .onComplete(event -> {
          latch.countDown();
          log.info("deploy: {}", event.result());
        });

    latch.await();

    EventLoop.sleepSecond(1);
  }

  @Test
  public void testPublish() {
    for (;;) {
      if (client.isConnected()) {
        client.publish("/message/hello", "Hello World !", MqttQoS.AT_LEAST_ONCE
            , event -> log.info("publish  {}, {}", event.result(), event.succeeded()));
        break;
      }
    }
    EventLoop.sleepSecond(1);
  }

  @Test
  public void testSubscribe() {
    // 订阅消息
    dispatcher.subscribe("/message/#", (topicName, message) ->
        log.info("rcv topic[{}], msg: {}", topicName, message.payload().toString()));

    EventLoop.sleepSecond(120);
  }

  @After
  public void tearDown() throws Exception {
    if (client != null) {
      VertxHolder.undeploy(client.deploymentID())
          .onFailure(Throwable::printStackTrace)
          .onComplete(event -> log.info("undeploy: {}", event.succeeded()));
      EventLoop.sleepSecond(1);
    }
  }


  public static void loop(Callable<Boolean> callable) {
    for (;;) {
      try {
        Boolean status = callable.call();
        if (Boolean.TRUE.equals(status)) {
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

}