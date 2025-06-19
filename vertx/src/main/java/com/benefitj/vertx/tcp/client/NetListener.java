package com.benefitj.vertx.tcp.client;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

/**
 * 监听
 */
public interface NetListener {

  // *********************************************************************************** //

  /**
   * 处理数据
   */
  void onMessage(VertxTcpClient client, Buffer buf);

  /**
   * 出现异常
   *
   * @param e 异常
   */
  default void onException(VertxTcpClient client, Throwable e) {
    e.printStackTrace();
  }

  /**
   *
   */
  default void onDrainHandle(VertxTcpClient client) {
  }

  /**
   * socket被关闭
   */
  default void onCloseHandle(VertxTcpClient client) {
  }

  /**
   * 结束
   */
  default void onEndHandle(VertxTcpClient client) {
  }

  // *********************************************************************************** //

  /**
   * 连接成功
   *
   * @param socket SOCKET
   */
  default void onSuccess(VertxTcpClient client, NetSocket socket) {
  }

  default void onFailure(VertxTcpClient client, Throwable e) {
    e.printStackTrace();
  }

  default void onComplete(VertxTcpClient client, NetSocket socket) {
  }

  // *********************************************************************************** //

}
