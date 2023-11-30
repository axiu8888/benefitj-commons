package com.benefitj.mqtt.vertx.client;

import com.benefitj.core.EventLoop;
import com.benefitj.mqtt.vertx.VertxHolder;
import io.netty.handler.codec.mqtt.MqttQoS;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * 测试客户端
 */
@Slf4j
public class VertxMqttClientTest {

  private VertxMqttClient client;

  private VertxMqttMessageDispatcher dispatcher = new VertxMqttMessageDispatcher(true);

  @Before
  public void setUp() throws Exception {
    VertxHolder.deploy(client = new VertxMqttClient()
        .setHandler(dispatcher)
        .setInitializer(verticle -> {
          // 初始化
        })
        .setAutoConnectTimer(new AutoConnectTimer(true).setPeriod(5))
//        .setRemoteAddress("127.0.0.1", 1883))
//        .setRemoteAddress("192.168.85.129", 1883))
        .setRemoteAddress("192.168.1.198", 1883))
        .onComplete(event -> {
          log.info("deploy: {}", event.result());
        });
    for (int i = 0; i < 5; i++) {
      if (client.isConnected()) {
        return;
      } else {
        EventLoop.await(1, TimeUnit.SECONDS);
      }
    }
  }

  @Test
  public void testPublish() {
    client.publish("/message/hello", "Hello World !", MqttQoS.AT_LEAST_ONCE
        , event -> log.info("publish  {}, {}", event.result(), event.succeeded()));
  }

  @Test
  public void testSubscribe() {
    // 订阅消息
    dispatcher.subscribe("save/#", (topicName, message) ->
        log.info("rcv topic[{}], msg: {}", topicName, message.payload().toString()));

    EventLoop.await(120, TimeUnit.SECONDS);
  }

  @After
  public void tearDown() throws Exception {
    if (client != null) {
      VertxHolder.undeploy(client.deploymentID())
          .onFailure(Throwable::printStackTrace)
          .onComplete(event -> log.info("undeploy: {}", event.succeeded()));
      EventLoop.await(1, TimeUnit.SECONDS);
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