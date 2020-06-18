package com.benefitj.netty.server.udp;

/**
 * 过期检查
 */
public interface ExpiredChecker<C extends UdpClient> {

  /**
   * 检查过期的客户端
   *
   * @param manager
   */
  void check(UdpClientManager<C> manager);

}
