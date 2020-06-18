package com.benefitj.spring.websocket.spring;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.websocket.CloseReason;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务端
 */
public class SpringWebSocketServer implements WebSocketHandler, HandshakeInterceptor {

  /**
   * 客户端
   */
  private final Map<String, SpringWebSocketClient> clients = new ConcurrentHashMap<>();
  /**
   * 客户端类型
   */
  private volatile Class<? extends SpringWebSocketClient> clientType;

  public SpringWebSocketServer() {
  }

  @Override
  public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
    return true;
  }

  @Override
  public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    // ~
  }

  @Override
  public final void afterConnectionEstablished(WebSocketSession session) throws Exception {
    SpringWebSocketClient client = newClient();
    clients.put(session.getId(), client);
    client.onOpen(new SpringWebSocketSession(session));
  }

  @Override
  public final void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    getClient(session.getId()).handleMessage(message);
  }

  @Override
  public final void handleTransportError(WebSocketSession session, Throwable e) throws Exception {
    getClient(session.getId()).onError(e);
  }

  @Override
  public final void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    int code = closeStatus.getCode();
    SpringWebSocketClient client = removeClient(session.getId());
    for (CloseReason.CloseCodes cc : CloseReason.CloseCodes.values()) {
      if (cc.getCode() == code) {
        client.onClose(new CloseReason(cc, closeStatus.getReason()));
        return;
      }
    }
    client.onClose(new CloseReason(CloseReason.CloseCodes.NO_STATUS_CODE, closeStatus.getReason()));
  }

  @Override
  public boolean supportsPartialMessages() {
    return false;
  }

  /**
   * 获取客户端
   */
  public SpringWebSocketClient getClient(String sessionId) {
    return clients.get(sessionId);
  }

  /**
   * 移除客户端
   */
  public SpringWebSocketClient removeClient(String sessionId) {
    return clients.remove(sessionId);
  }

  /**
   * 创建新的WebSocket客户端
   */
  public SpringWebSocketClient newClient() {
    try {
      return getClientType().newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  public Map<String, SpringWebSocketClient> getClients() {
    return clients;
  }

  public Class<? extends SpringWebSocketClient> getClientType() {
    return clientType;
  }

  public void setClientType(Class<? extends SpringWebSocketClient> clientType) {
    this.clientType = clientType;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
