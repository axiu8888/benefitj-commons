package com.benefitj.spring.websocket;

import javax.websocket.CloseReason;
import java.io.IOException;
import java.nio.ByteBuffer;

public interface WsSession<Session> {

  /**
   * Return a unique session identifier.
   */
  String getId();


  Session getSession();

  /**
   * 发送文本数据
   *
   * @param text 数据
   */
  void sendText(String text) throws IOException;

  /**
   * 发送二进制数据
   *
   * @param buff 数据
   * @return 返回结果
   */
  void sendBinary(ByteBuffer buff) throws IOException;

  /**
   * 判断是否开启
   */
  boolean isOpen();

  /**
   * 判断是否安全
   */
  boolean isSecure();

  /**
   * 关闭
   */
  void close() throws IOException;

  /**
   * 关闭
   *
   * @param reason
   */
  void close(CloseReason reason) throws IOException;


}
