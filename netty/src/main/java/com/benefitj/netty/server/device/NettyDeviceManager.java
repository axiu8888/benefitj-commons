package com.benefitj.netty.server.device;

import com.benefitj.netty.device.DeviceManager;
import io.netty.channel.Channel;

/**
 * Netty设备
 */
public interface NettyDeviceManager<T extends NettyDevice> extends DeviceManager<String, T> {

  default T create(String id, Channel ch) {
    return create(id, NettyDeviceFactory.wrap(ch));
  }

  default T computeIfAbsent(String key, Channel ch) {
    T t = get(key);
    if (t != null) {
      return t;
    }
    return create(key, ch);
  }

}
