package com.benefitj.mqtt.vertx.server;

import com.benefitj.core.EventLoop;
import com.benefitj.mqtt.vertx.VertxHolder;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class VertxMqttServerTest {

  VertxMqttServer server;

  @BeforeEach
  public void setUp() throws Exception {
  }

  @Test
  public void testServer() {
    VertxHolder.deploy(server = new VertxMqttServer()
        .setProperty(new MqttServerProperty())
        .setInitializer(verticle -> {
          // 初始化
        })
        // 认证
        .setAuthenticator(endpoint -> {
          //MqttAuth auth = endpoint.auth();
          log.info("auth: {}", endpoint.auth());
          return true;
        })
        .setEndpointHandler(new MqttEndpointHandlerImpl())
    ).onComplete(event -> log.info("deploy: {}", event.result()));

    EventLoop.await(300, TimeUnit.SECONDS);
  }

  @AfterEach
  public void tearDown() throws Exception {
    if (server != null) {
      VertxHolder.undeploy(server.deploymentID())
          .onComplete(event -> log.info("undeploy: {}", event.succeeded()));
      EventLoop.await(1, TimeUnit.SECONDS);
    }
  }
}