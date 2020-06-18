package com.benefitj.spring.websocket.spring;

import com.benefitj.spring.websocket.WsSession;
import org.springframework.http.HttpHeaders;
import org.springframework.web.socket.*;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.List;
import java.util.Map;

public class SpringWebSocketSession implements WsSession<WebSocketSession>, WebSocketSession {

  private final WebSocketSession session;

  public SpringWebSocketSession(WebSocketSession session) {
    this.session = session;
  }

  @Override
  public String getId() {
    return getSession().getId();
  }

  @Override
  public URI getUri() {
    return getSession().getUri();
  }

  @Override
  public HttpHeaders getHandshakeHeaders() {
    return getSession().getHandshakeHeaders();
  }

  @Override
  public Map<String, Object> getAttributes() {
    return getSession().getAttributes();
  }

  @Override
  public Principal getPrincipal() {
    return getSession().getPrincipal();
  }

  @Override
  public InetSocketAddress getLocalAddress() {
    return getSession().getLocalAddress();
  }

  @Override
  public InetSocketAddress getRemoteAddress() {
    return getSession().getRemoteAddress();
  }

  @Override
  public String getAcceptedProtocol() {
    return getSession().getAcceptedProtocol();
  }

  @Override
  public void setTextMessageSizeLimit(int messageSizeLimit) {
    getSession().setTextMessageSizeLimit(messageSizeLimit);
  }

  @Override
  public int getTextMessageSizeLimit() {
    return getSession().getTextMessageSizeLimit();
  }

  @Override
  public void setBinaryMessageSizeLimit(int messageSizeLimit) {
    getSession().setBinaryMessageSizeLimit(messageSizeLimit);
  }

  @Override
  public int getBinaryMessageSizeLimit() {
    return getSession().getBinaryMessageSizeLimit();
  }

  @Override
  public List<WebSocketExtension> getExtensions() {
    return getSession().getExtensions();
  }

  @Override
  public void sendMessage(WebSocketMessage<?> message) throws IOException {
    getSession().sendMessage(message);
  }

  @Override
  public boolean isOpen() {
    return getSession().isOpen();
  }

  @Override
  public boolean isSecure() {
    return false;
  }

  @Override
  public void close() throws IOException {
    getSession().close();
  }

  @Override
  public void close(CloseReason reason) {
    try {
      CloseReason.CloseCode closeCode = reason.getCloseCode();
      CloseStatus status = new CloseStatus(closeCode.getCode(), reason.getReasonPhrase());
      getSession().close(status);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public void close(CloseStatus status) throws IOException {
    getSession().close(status);
  }

  @Override
  public WebSocketSession getSession() {
    return session;
  }

  @Override
  public void sendText(String text) throws IOException {
    TextMessage msg = new TextMessage(text);
    getSession().sendMessage(msg);
  }

  @Override
  public void sendBinary(ByteBuffer buff) throws IOException {
    BinaryMessage msg = new BinaryMessage(buff);
    getSession().sendMessage(msg);
  }

}
