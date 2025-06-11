package com.benefitj.http.sse;


/**
 * 事件监听
 */
public interface SSEEventListener {

  /**
   * 打开
   *
   * @param client 客户端
   */
  default void onOpen(SSEClient client) {
  }

  /**
   * 事件
   *
   * @param client 客户端
   * @param event  事件
   */
  void onEvent(SSEClient client, SSEEvent event);

  /**
   * 保持存活
   *
   * @param client 客户端
   * @param event  事件
   */
  default void onKeepAlive(SSEClient client, SSEEvent event) {
  }

  /**
   * 出现异常
   *
   * @param client 客户端
   * @param error  返回错误
   */
  default void onFailure(SSEClient client, Throwable error) {
  }

  /**
   * 关闭
   *
   * @param client 客户端
   */
  default void onClosed(SSEClient client) {
  }

  /**
   * 重新连接前
   *
   * @param client 客户端
   */
  default void onReconnectBefore(SSEClient client) {
  }

}
