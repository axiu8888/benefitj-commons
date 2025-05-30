package com.benefitj.http.sse;

import com.benefitj.core.EventLoop;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;


@Slf4j
class SSEClientTest {

  @BeforeEach
  void setUp() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void test_SSE() {
    String sseUrl = "http://127.0.0.1/api/sse/connect";
    SSEClient sseClient = SSEClient.newClient(sseUrl, new SSEEventListener() {
      @Override
      public void onOpen(SSEClient client) {
        log.info("onOpen: " + client.getSseUrl());
      }

      @Override
      public void onEvent(SSEClient client, SSEEvent event) {
        log.info("onEvent: " + event);
      }

      @Override
      public void onFailure(SSEClient client, Throwable error) {
        log.error("onFailure: " + error.getMessage(), error);
      }

      @Override
      public void onClosed(SSEClient client) {
        log.info("onClosed: {}", client.getSseUrl());
      }
    });

    sseClient.setAutoReconnect(true);
    sseClient.setAutoReconnectInterval(Duration.ofSeconds(5));
    sseClient.connect();

    // 保持程序运行
    try {
      EventLoop.sleepSecond(100000);
    } finally {
      sseClient.disconnect();
    }

  }
}