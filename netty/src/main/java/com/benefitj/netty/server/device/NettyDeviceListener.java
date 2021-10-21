package com.benefitj.netty.server.device;

import com.benefitj.device.DeviceListener;

/**
 * 设备状态改变的监听
 */
public interface NettyDeviceListener<D extends NettyDevice> extends DeviceListener<String, D> {

  /**
   * 设备监听
   */
  static <D extends NettyDevice> NettyDeviceListener<D> emptyListener() {
    return new NettyDeviceListener<D>() {};
  }

}
