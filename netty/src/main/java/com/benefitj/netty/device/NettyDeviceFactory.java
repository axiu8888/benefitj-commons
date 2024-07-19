package com.benefitj.netty.device;

import com.benefitj.core.device.DeviceFactory;
import io.netty.channel.Channel;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 设备工厂
 *
 * @param <T>
 */
public interface NettyDeviceFactory<T extends NettyDevice> extends DeviceFactory<String, T> {

  String ATTRS_CHANNEL = "channel";
  String ATTRS_LOCAL = "local";
  String ATTRS_REMOTE = "remote";

  @Override
  default T create(String id, @Nullable Map<String, Object> attrs) {
    Channel ch = null;
    if (attrs != null && !attrs.isEmpty()) {
      ch = (Channel) attrs.get(ATTRS_CHANNEL);
      attrs.remove(ATTRS_CHANNEL);
    }
    T device = create(id, ch);
    if (attrs != null && !attrs.isEmpty()) {
      InetSocketAddress local = (InetSocketAddress) attrs.remove(ATTRS_LOCAL);
      if (local != null) device.setLocalAddress(local);
      InetSocketAddress remote = (InetSocketAddress) attrs.remove(ATTRS_REMOTE);
      if (remote != null) device.setLocalAddress(remote);
      attrs.forEach(device::setAttr);
    }
    return device;
  }

  /**
   * 创建设备
   *
   * @param id      设备ID
   * @param channel 通道
   * @return 返回创建的设备
   */
  T create(String id, Channel channel);

  /**
   * wrapper
   */
  static Map<String, Object> wrap(Channel channel) {
    Map<String, Object> attrs = new LinkedHashMap<>(1);
    attrs.put(ATTRS_CHANNEL, channel);
    return attrs;
  }

}
