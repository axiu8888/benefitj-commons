package com.benefitj.netty.server.device;

import io.netty.channel.Channel;

/**
 * 设备工厂
 *
 * @param <T>
 */
public interface DeviceFactory<T extends Device> {

  /**
   * 创建新设备
   *
   * @param id      设备ID
   * @param channel 通道
   * @return 返回新的设备对象
   */
  T create(String id, Channel channel);

}
