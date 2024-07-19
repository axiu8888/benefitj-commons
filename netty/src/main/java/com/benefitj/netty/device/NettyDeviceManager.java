package com.benefitj.netty.device;

import com.benefitj.core.device.DeviceFactory;
import com.benefitj.core.device.DeviceListener;
import com.benefitj.core.device.DeviceManager;
import io.netty.channel.Channel;

import java.util.Collections;
import java.util.LinkedHashMap;
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
  default T computeIfAbsent(String key, Map<String, Object> attrs) {
    return computeIfAbsent(key, null, attrs);
  }

  /**
   * 获取设备，如果不存在，则创建
   */
  default T computeIfAbsent(String key, Channel ch, Map<String, Object> attrs) {
    T device = get(key);
    if (device != null)  return device;
    Map<String, Object> wrap = ch != null ? NettyDeviceFactory.wrap(ch) : new LinkedHashMap<>();
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
