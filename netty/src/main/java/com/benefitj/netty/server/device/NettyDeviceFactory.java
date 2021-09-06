package com.benefitj.netty.server.device;

import com.benefitj.device.DeviceFactory;
import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备工厂
 *
 * @param <T>
 */
public interface NettyDeviceFactory<T extends NettyDevice> extends DeviceFactory<T> {

  String ATTRS_CHANNEL = "channel";

  /**
   * wrapper
   */
  static Map<String, Object> wrap(Channel channel) {
    Map<String, Object> attrs = new HashMap<>(1);
    attrs.put(ATTRS_CHANNEL, channel);
    return attrs;
  }

}
