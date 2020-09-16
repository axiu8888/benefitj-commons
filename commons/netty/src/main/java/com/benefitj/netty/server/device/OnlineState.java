package com.benefitj.netty.server.device;

/**
 * 在线状态
 */
public enum OnlineState {

  /**
   * 在线
   */
  ONLINE,
  /**
   * 离线
   */
  OFFLINE;

  /**
   * 是否在线
   */
  public boolean isOnline() {
    return this == ONLINE;
  }

  /**
   * 是否离线
   */
  public boolean isOffline() {
    return this == OFFLINE;
  }
}
