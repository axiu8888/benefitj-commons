package com.benefitj.spring.websocket.spring;

import com.benefitj.spring.websocket.AbstractWebSocketClient;
import org.springframework.web.socket.*;

import java.nio.ByteBuffer;

/**
 * Spring 实现下的 WebSocket客户端
 */
public abstract class SpringWebSocketClient extends AbstractWebSocketClient<SpringWebSocketSession> {

  public SpringWebSocketClient() {
  }

  protected final void handleMessage(WebSocketMessage<?> message) throws Exception {
    if (message instanceof TextMessage) {
      TextMessage tm = ((TextMessage) message);
      onMessage(tm.getPayload(), tm.isLast());
    } else if (message instanceof BinaryMessage) {
      BinaryMessage bm = (BinaryMessage) message;
      onBinaryMessage(bm.getPayload(), bm.isLast());
    } else if (message instanceof PingMessage) {
      onPingMessage(((PingMessage) message).getPayload());
    } else if (message instanceof PongMessage) {
      onPongMessage(((PongMessage) message).getPayload());
    } else {
      onUnknownMessage(message);
    }
  }

  public void onPingMessage(ByteBuffer ping) {
    // ~
  }

  public void onPongMessage(ByteBuffer pong) {
    // ~
  }

  public void onUnknownMessage(WebSocketMessage<?> message) {
    // ~
  }

}
