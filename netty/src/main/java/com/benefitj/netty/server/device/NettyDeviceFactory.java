package com.benefitj.netty.server.device;

import com.benefitj.netty.device.DeviceFactory;
import io.netty.channel.Channel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 设备工厂
 *
 * @param <T>
 */
public interface NettyDeviceFactory<T extends NettyDevice> extends DeviceFactory<String, T> {

  String ATTRS_CHANNEL = "channel";

  @Override
  default T create(String id, @Nullable Map<String, Object> attrs) {
    Channel ch = null;
    if (attrs != null && !attrs.isEmpty()) {
      ch = (Channel) attrs.get(ATTRS_CHANNEL);
      attrs.remove(ATTRS_CHANNEL);
    }
    T device = create(id, ch);
    if (attrs != null && !attrs.isEmpty()) {
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
    Map<String, Object> attrs = new HashMap<>(1);
    attrs.put(ATTRS_CHANNEL, channel);
    return attrs;
  }

}
