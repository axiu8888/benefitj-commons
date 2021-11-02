package com.benefitj.mqtt.server;

import com.benefitj.core.EventLoop;
import com.benefitj.mqtt.VerticleInitializer;
import com.benefitj.mqtt.VertxHolder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VertxMqttServerTest {

  private final Logger log = LoggerFactory.getLogger(getClass());

  VertxMqttServer server;

  @Before
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
          return true;
        })
        .setEndpointHandler(new MqttEndpointHandlerImpl())
    ).onComplete(event -> log.info("deploy: {}", event.result()));

    EventLoop.sleepSecond(5);
  }

  @After
  public void tearDown() throws Exception {
    if (server != null) {
      VertxHolder.undeploy(server.deploymentID())
          .onComplete(event -> log.info("undeploy: {}", event.succeeded()));
      EventLoop.sleepSecond(1);
    }
  }
}