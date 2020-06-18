package com.benefitj.netty.server.udp;

import javax.annotation.Nullable;

/**
 * 客户端状态改变的监听
 */
public interface ClientStateChangeListener<C extends UdpClient> {

  /**
   * 被添加
   *
   * @param id        客户端ID
   * @param newClient 新的客户端
   * @param oldClient 旧的客户端
   */
  default void onAddition(String id, C newClient, @Nullable C oldClient) {
    // ~
  }

  /**
   * 被移除
   *
   * @param id     客户端ID
   * @param client 客户端
   */
  default void onRemoval(String id, C client) {
    // ~
  }

}
