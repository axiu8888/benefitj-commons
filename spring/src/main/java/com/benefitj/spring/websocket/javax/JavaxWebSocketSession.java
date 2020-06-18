package com.benefitj.spring.websocket.javax;

import com.benefitj.spring.websocket.WsSession;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JavaxWebSocketSession implements WsSession<Session>, Session {

  private final Session session;

  public JavaxWebSocketSession(Session session) {
    this.session = session;
  }

  @Override
  public WebSocketContainer getContainer() {
    return getSession().getContainer();
  }

  @Override
  public void addMessageHandler(MessageHandler handler) throws IllegalStateException {
    getSession().addMessageHandler(handler);
  }

  @Override
  public Set<MessageHandler> getMessageHandlers() {
    return getSession().getMessageHandlers();
  }

  @Override
  public void removeMessageHandler(MessageHandler listener) {
    getSession().removeMessageHandler(listener);
  }

  @Override
  public String getProtocolVersion() {
    return getSession().getProtocolVersion();
  }

  @Override
  public String getNegotiatedSubprotocol() {
    return getSession().getNegotiatedSubprotocol();
  }

  @Override
  public List<Extension> getNegotiatedExtensions() {
    return getSession().getNegotiatedExtensions();
  }

  @Override
  public boolean isSecure() {
    return getSession().isSecure();
  }

  @Override
  public boolean isOpen() {
    return getSession().isOpen();
  }

  @Override
  public long getMaxIdleTimeout() {
    return getSession().getMaxIdleTimeout();
  }

  @Override
  public void setMaxIdleTimeout(long timeout) {
    getSession().setMaxIdleTimeout(timeout);
  }

  @Override
  public void setMaxBinaryMessageBufferSize(int max) {
    getSession().setMaxBinaryMessageBufferSize(max);
  }

  @Override
  public int getMaxBinaryMessageBufferSize() {
    return getSession().getMaxBinaryMessageBufferSize();
  }

  @Override
  public void setMaxTextMessageBufferSize(int max) {
    getSession().setMaxTextMessageBufferSize(max);
  }

  @Override
  public int getMaxTextMessageBufferSize() {
    return getSession().getMaxTextMessageBufferSize();
  }

  @Override
  public RemoteEndpoint.Async getAsyncRemote() {
    return getSession().getAsyncRemote();
  }

  @Override
  public RemoteEndpoint.Basic getBasicRemote() {
    return getSession().getBasicRemote();
  }

  @Override
  public String getId() {
    return getSession().getId();
  }

  @Override
  public void close() throws IOException {
    getSession().close();
  }

  @Override
  public void close(CloseReason closeReason) throws IOException {
    getSession().close(closeReason);
  }

  @Override
  public URI getRequestURI() {
    return getSession().getRequestURI();
  }

  @Override
  public Map<String, List<String>> getRequestParameterMap() {
    return getSession().getRequestParameterMap();
  }

  @Override
  public String getQueryString() {
    return getSession().getQueryString();
  }

  @Override
  public Map<String, String> getPathParameters() {
    return getSession().getPathParameters();
  }

  @Override
  public Map<String, Object> getUserProperties() {
    return getSession().getUserProperties();
  }

  @Override
  public Principal getUserPrincipal() {
    return getSession().getUserPrincipal();
  }

  @Override
  public Set<Session> getOpenSessions() {
    return getSession().getOpenSessions();
  }

  @Override
  public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Partial<T> handler) throws IllegalStateException {
    getSession().addMessageHandler(clazz, handler);
  }

  @Override
  public <T> void addMessageHandler(Class<T> clazz, MessageHandler.Whole<T> handler) throws IllegalStateException {
    getSession().addMessageHandler(clazz, handler);
  }

  @Override
  public Session getSession() {
    return session;
  }

  @Override
  public void sendText(String text) throws IOException {
    getSession().getBasicRemote().sendText(text);
  }

  @Override
  public void sendBinary(ByteBuffer buff) throws IOException {
    getSession().getBasicRemote().sendBinary(buff);
  }

}
