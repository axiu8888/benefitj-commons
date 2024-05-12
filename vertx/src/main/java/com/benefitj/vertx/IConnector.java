package com.benefitj.vertx;

public interface IConnector {

  /**
   * 是否已连接
   */
  boolean isConnected();

  void doConnect();

}
