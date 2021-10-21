package com.benefitj.netty.server.device;

import com.benefitj.device.DeviceManager;
import io.netty.channel.Channel;

/**
 * 设备管理
 *
 * @param <D> 设备类型
 */
public interface NettyDeviceManager<D extends NettyDevice> extends DeviceManager<String, D> {

  /**
   * 获取设备，如果不存在就创建
   *
   * @param id      设备ID
   * @param channel 通道
   * @return 返回设备
   */
  default D computeIfAbsent(String id, Channel channel) {
    D device = get(id);
    if (device == null) {
      return computeIfAbsent(id, NettyDeviceFactory.wrap(channel));
    }
    return device;
  }

}
