package com.benefitj.spring.websocket;

import javax.websocket.*;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface IWebSocket<S extends WsSession<?>> {

  /**
   * @return 创建错误的异常
   */
  static IllegalStateException newFailureException() {
    return newFailureException("The session is not open !");
  }

  /**
   * 创建错误的异常
   *
   * @param msg 错误信息
   * @return 返回创建的异常
   */
  static IllegalStateException newFailureException(String msg) {
    return new IllegalStateException(msg);
  }

  /**
   * WebSocket Open
   *
   * @param session Session
   */
  @OnOpen
  void onOpen(S session);

  /**
   * 接收到消息
   *
   * @param text   文本数据
   * @param isLast 是否为最后的数据
   */
  @OnMessage
  void onMessage(String text, boolean isLast);

  /**
   * 接收到二进制数据
   *
   * @param buffer 数据
   * @param isLast 是否为最后的数据
   */
  @OnMessage
  void onBinaryMessage(ByteBuffer buffer, boolean isLast);

  /**
   * 连接关闭调用的方法
   */
  @OnClose
  void onClose(CloseReason reason);

  /**
   * 发生错误时调用
   *
   * @param e 异常
   */
  @OnError
  void onError(Throwable e);

  /**
   * 获取WebSocket的状态
   */
  WsStatus getStatus();

  /**
   * @return 获取WebSocket的Session
   */
  S getSession();

  /**
   * @return 获取Session ID
   */
  default String getSessionId() {
    return getSession().getId();
  }


  /**
   * 发送文本数据
   *
   * @param text 数据
   * @return 返回发送的结果
   */
  default FuturePromise<Void> sendText0(String text) throws IOException {
    final S session = getSession();
    if (isOpen(session)) {
      session.sendText(text);
      return PromiseFactory.newSuccess();
    }
    return PromiseFactory.newFailure(newFailureException());
  }

  /**
   * 发送文本数据
   *
   * @param text 数据
   * @return 返回发送的结果
   */
  default FuturePromise<Void> sendText(String text) {
    try {
      return sendText0(text);
    } catch (IOException e) {
      return PromiseFactory.newFailure(e);
    }
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   * @return 返回发送的结果
   */
  default FuturePromise<Void> sendBinary0(ByteBuffer data) throws IOException {
    final S session = getSession();
    if (isOpen(session)) {
      session.sendBinary(data);
      return PromiseFactory.newSuccess();
    }
    return PromiseFactory.newFailure(newFailureException());
  }

  /**
   * 发送字节数据
   *
   * @param data 数据
   * @return 返回发送的结果
   */
  default FuturePromise<Void> sendBinary(ByteBuffer data) {
    try {
      return sendBinary0(data);
    } catch (IOException e) {
      return PromiseFactory.newFailure(e);
    }
  }

  /**
   * 当前的WebSocket是否处于打开状态
   */
  default boolean isOpen() {
    return getSession().isOpen();
  }

  /**
   * 当前的WebSocket是否处于打开状态
   */
  default boolean isOpen(final S session) {
    return session != null && session.isOpen();
  }

  /**
   * 关闭当前会话
   */
  default void close() throws IOException {
    getSession().close();
  }

  /**
   * 关闭当前会话
   */
  default void closeSession() {
    S session = getSession();
    if (session != null) {
      try {
        session.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 关闭
   */
  default void close(AutoCloseable c) {
    try {
      if (c != null) {
        c.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
      /* ignore */
    }
  }
}
