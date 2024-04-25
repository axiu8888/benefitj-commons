package com.benefit.vertx.tcp;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

public interface NetListener {

  // *********************************************************************************** //

  /**
   * 处理数据
   */
  void onMessage(Buffer buf);

  /**
   * 出现异常
   *
   * @param e 异常
   */
  default void onException(Throwable e) {
    e.printStackTrace();
  }

  /**
   *
   */
  default void onDrainHandle() {
  }

  /**
   * socket被关闭
   */
  default void onCloseHandle() {
  }

  /**
   * 结束
   */
  default void onEndHandle() {
  }

  // *********************************************************************************** //

  /**
   * 连接成功
   *
   * @param socket SOCKET
   */
  default void onSuccess(NetSocket socket) {
  }

  default void onFailure(Throwable e) {
    e.printStackTrace();
  }

  default void onComplete(NetSocket socket) {
  }

  // *********************************************************************************** //

}
