package com.benefit.vertx.tcp;

import io.vertx.core.buffer.Buffer;

public interface ClientNetListener<T> {

  // *********************************************************************************** //

  /**
   * 处理数据
   */
  void onMessage(T socket, Buffer buf);

  /**
   * 出现异常
   *
   * @param e 异常
   */
  default void onException(T socket, Throwable e) {
    e.printStackTrace();
  }

  /**
   *
   */
  default void onDrainHandle(T socket) {
  }

  /**
   * socket被关闭
   */
  default void onCloseHandle(T socket) {
  }

  /**
   * 结束
   */
  default void onEndHandle(T socket) {
  }

  // *********************************************************************************** //

  /**
   * 连接成功
   *
   * @param socket SOCKET
   */
  default void onSuccess(T socket) {
  }

  default void onFailure(T socket, Throwable e) {
    e.printStackTrace();
  }

  default void onComplete(T socket) {
  }

  // *********************************************************************************** //

}
