package com.benefitj.mqtt.message;

/**
 * 消息方向
 */
public enum Direction {
  /**
   * 客户端消息
   */
  CLIENT,
  /**
   * 服务端消息
   */
  SERVER,
  /**
   * 两个方向都允许
   */
  BOTH,
  /**
   * 消息保留
   */
  RESERVED;

  /**
   * 是否允许客户端发往服务端
   */
  public boolean isClientToServer() {
    return this == CLIENT || this == BOTH;
  }

  /**
   * 是否允许服务端发往客户端
   */
  public boolean isServerToClient() {
    return this == SERVER || this == BOTH;
  }

  /**
   * 是否保留
   */
  public boolean isReserved() {
    return this == RESERVED;
  }
}
