package com.benefitj.netty.server.device;

import com.benefitj.netty.device.DeviceFactory;
import com.benefitj.netty.device.DeviceListener;
import com.benefitj.netty.device.DeviceManager;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.Map;

/**
 * Netty设备
 */
public interface NettyDeviceManager<T extends NettyDevice> extends DeviceManager<String, T> {

  default T create(String id, Channel ch) {
    return create(id, NettyDeviceFactory.wrap(ch));
  }

  /**
   * 获取设备，如果不存在，则创建
   */
  default T computeIfAbsent(String key, Channel ch) {
    return computeIfAbsent(key, ch, Collections.emptyMap());
  }

  /**
   * 获取设备，如果不存在，则创建
   */
  default T computeIfAbsent(String key, Channel ch, Map<String, Object> attrs) {
    T device = get(key);
    if (device != null)  return device;
    Map<String, Object> wrap = NettyDeviceFactory.wrap(ch);
    if (attrs != null && !attrs.isEmpty()) wrap.putAll(attrs);
    return create(key, wrap);
  }

  class Impl<T extends NettyDevice> extends DeviceManager.Impl<String, T> implements NettyDeviceManager<T> {

    public Impl() {
    }

    public Impl(DeviceFactory<String, T> deviceFactory,
                DeviceListener<String, T> deviceListener) {
      super(deviceFactory, deviceListener);
    }
  }

}
