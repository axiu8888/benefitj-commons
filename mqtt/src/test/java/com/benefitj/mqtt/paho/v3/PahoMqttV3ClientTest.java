package com.benefitj.mqtt.paho.v3;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.core.IdUtils;
import com.benefitj.core.functions.StreamBuilder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;


@Slf4j
class PahoMqttV3ClientTest {

  final PahoMqttV3Dispatcher dispatcher = new PahoMqttV3Dispatcher();
  PahoMqttV3Client client;

  @BeforeEach
  void setUp() {
    log.info("setUp ......................");

    client = StreamBuilder.of(new PahoMqttV3Client(StreamBuilder.of(new MqttConnectOptions())
            .set(opt -> {
              opt.setServerURIs(new String[]{"tcp://192.168.1.194:2883"});
              opt.setConnectionTimeout(1);
              opt.setUserName("admin");
              opt.setPassword("public".toCharArray());
              opt.setMaxInflight(100);
              opt.setAutomaticReconnect(false);
              opt.setMaxReconnectDelay(30_000);
            })
            .get(), "mqtt_test_" + IdUtils.uuid(0, 12)))
        .set(client -> {
          client.setManualAcks(false);
          client.setCallback(dispatcher);
          client.setAutoConnectTimer(timer ->
              timer
                  .setAutoConnect(true)
                  .setInterval(Duration.ofSeconds(5)
                  ));
        })
        .set(PahoMqttV3Client::connect)
        .get();
  }

  @AfterEach
  void tearDown() {
    if (client != null) {
      client.disconnect();
    }
    log.info("tearDown ......................");
  }

  @Test
  void test_publish() {
    for (int i = 0; i < 100; i++) {
      CatchUtils.ignore(() -> client.publish("collector/123456", new MqttMessage((DateFmtter.fmtNow() + " test...").getBytes())));
      EventLoop.sleepSecond(1);
    }
  }

  @Test
  void test_subscribe() {
    dispatcher.subscribe("collector/123456", (topicName, message) -> {
      log.info("topic: {}, message: {}", topicName, message);
    });
    EventLoop.sleepSecond(100);
  }

}