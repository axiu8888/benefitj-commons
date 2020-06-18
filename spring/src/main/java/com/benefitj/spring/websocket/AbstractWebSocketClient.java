package com.benefitj.spring.websocket;

import javax.websocket.CloseReason;
import java.nio.ByteBuffer;

/**
 * WebSocket客户端
 */
public abstract class AbstractWebSocketClient<S extends WsSession<?>> implements IWebSocket<S> {

  /**
   * 会话
   */
  private volatile S session;
  /**
   * webSocket的状态
   */
  private volatile WsStatus status;

  /**
   * 获取WebSocket的状态
   */
  @Override
  public WsStatus getStatus() {
    return status;
  }

  public void setStatus(WsStatus status) {
    this.status = status;
  }

  @Override
  public S getSession() {
    return session;
  }

  public void setSession(S session) {
    this.session = session;
  }

  @Override
  public final void onOpen(S session) {
    this.setSession(session);
    this.setStatus(WsStatus.OPEN);
    onOpen0(session);
  }

  /**
   * WebSocket打开
   *
   * @param session 会话
   */
  protected abstract void onOpen0(S session);

  @Override
  public final void onMessage(String text, boolean isLast) {
    onMessage0(getSession(), text, isLast);
  }

  /**
   * 接收到文本数据
   *
   * @param session 会话
   * @param text    文本数据
   * @param isLast  是否是被分割的最后的帧数据
   */
  protected abstract void onMessage0(S session, String text, boolean isLast);

  @Override
  public final void onBinaryMessage(ByteBuffer buffer, boolean isLast) {
    onBinaryMessage0(getSession(), buffer, isLast);
  }

  /**
   * 接收到二进制数据
   *
   * @param session 会话
   * @param buffer  缓冲数据
   * @param isLast  是否是被分割的最后的帧数据
   */
  protected abstract void onBinaryMessage0(S session, ByteBuffer buffer, boolean isLast);

  @Override
  public final void onClose(CloseReason reason) {
    this.setStatus(WsStatus.CLOSE);
    onClose0(getSession(), reason);
  }

  /**
   * 关闭
   *
   * @param session 会话
   * @param reason  关闭原因
   */
  protected abstract void onClose0(S session, CloseReason reason);

  @Override
  public final void onError(Throwable e) {
    onError0(getSession(), e);
  }

  /**
   * 抛出异常
   *
   * @param session 会话
   * @param e       异常
   */
  protected abstract void onError0(S session, Throwable e);


}
