package com.benefitj.spring.websocket.javax;

import com.benefitj.spring.websocket.AbstractWebSocketClient;

import javax.websocket.*;
import java.nio.ByteBuffer;

/**
 * Javax 实现下的 WebSocket客户端
 */
public abstract class JavaxWebSocketClient extends AbstractWebSocketClient<JavaxWebSocketSession> {

  private volatile JavaxWebSocketSession original;

  public JavaxWebSocketSession getOriginal() {
    return original;
  }

  /**
   * WebSocket打开
   *
   * @param session 会话
   */
  @OnOpen
  public final void onRecvOpen(Session session) {
    if (original != session) {
      original = new JavaxWebSocketSession(session);
    }
    onOpen(original);
  }

  /**
   * 接收到文本数据
   *
   * @param text   文本数据
   * @param isLast 是否是被分割的最后的帧数据
   */
  @OnMessage
  public final void onRecvMessage(String text, boolean isLast) {
    onMessage(text, isLast);
  }

  /**
   * 接收到二进制数据
   *
   * @param buffer 缓冲数据
   * @param isLast 是否是被分割的最后的帧数据
   */
  @OnMessage
  public final void onRecvBinaryMessage(ByteBuffer buffer, boolean isLast) {
    onBinaryMessage(buffer, isLast);
  }

  /**
   * 关闭
   *
   * @param reason 关闭原因
   */
  @OnClose
  public final void onRecvClose(CloseReason reason) {
    onClose(reason);
  }

  /**
   * 抛出异常
   *
   * @param e 异常
   */
  @OnError
  public final void onRecvError(Throwable e) {
    onError(e);
  }

}
