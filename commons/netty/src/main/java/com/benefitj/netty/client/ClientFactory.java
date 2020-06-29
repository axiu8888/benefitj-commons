package com.benefitj.netty.client;

import com.benefitj.netty.NettyFactory;

/**
 * 客户端工厂，已过时，请使用{@link NettyFactory}
 */
@Deprecated
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
