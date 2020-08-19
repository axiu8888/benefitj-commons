package com.benefitj.netty.server.udpdevice;

/**
 * 过期检查
 */
public interface ExpireChecker<C extends UdpDeviceClient> {

  /**
   * 检查过期的客户端
   *
   * @param manager
   */
  void check(UdpDeviceClientManager<C> manager);

  /**
   * 默认过期检查的实现
   */
  static <C extends UdpDeviceClient> ExpireChecker<C> newInstance() {
    return new DefaultExpireChecker<>();
  }

}
