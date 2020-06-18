package com.benefitj.netty.client;

/**
 * 客户端工厂
 */
public class ClientFactory {

  /**
   * 创建TCP的客户端
   */
  public static TcpNettyClient newTcpClient() {
    return new TcpNettyClient();
  }

  /**
   * 创建UDP的客户端
   */
  public static UdpNettyClient newUdpClient() {
    return new UdpNettyClient();
  }
}
