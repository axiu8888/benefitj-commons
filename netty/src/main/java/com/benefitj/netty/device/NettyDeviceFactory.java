package com.benefitj.netty.device;

import com.benefitj.core.device.DeviceFactory;
import com.benefitj.core.executable.Instantiator;
import io.netty.channel.Channel;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 设备工厂
 *
 * @param <T>
 */
public interface NettyDeviceFactory<T extends NettyDevice> extends DeviceFactory<String, T> {

  String ATTRS_TYPE = "type";
  String ATTRS_CHANNEL = "channel";
  String ATTRS_LOCAL = "local";
  String ATTRS_REMOTE = "remote";

  @Override
  T create(String id, @Nullable Map<String, Object> attrs);

  /**
   * 创建设备
   *
   * @param id      设备ID
   * @param channel 通道
   * @return 返回创建的设备
   */
  default T create(String id, Channel channel) {
    return create(id, wrap(channel));
  }

  /**
   * 创建设备
   *
   * @param id      设备ID
   * @param channel 通道
   * @return 返回创建的设备
   */
  default T create(String id, Channel channel, InetSocketAddress local, InetSocketAddress remote) {
    return create(id, wrap(channel, local, remote));
  }

  /**
   * wrapper
   */
  static Map<String, Object> wrap(Channel ch) {
    return wrap(ch, AbstractDevice.ofLocal(ch), AbstractDevice.ofRemote(ch));
  }

  /**
   * wrapper
   */
  static Map<String, Object> wrap(Channel ch, InetSocketAddress local, InetSocketAddress remote) {
    if (ch == null && local == null && remote == null) return Collections.emptyMap();
    Map<String, Object> attrs = new LinkedHashMap<>(3);
    attrs.put(ATTRS_CHANNEL, ch);
    attrs.put(ATTRS_LOCAL, local);
    attrs.put(ATTRS_REMOTE, remote);
    return attrs;
  }

  static <D extends NettyDevice> D unwrap(D device, Map<String, Object> attrs) {
    if (attrs != null && !attrs.isEmpty()) {
      device.setChannel((Channel) attrs.get(ATTRS_CHANNEL));
      device.setLocalAddress((InetSocketAddress) attrs.get(ATTRS_LOCAL));
      device.setRemoteAddress((InetSocketAddress) attrs.get(ATTRS_REMOTE));
    }
    return device;
  }

  /**
   * 创建设备工程
   *
   * @param deviceType 设备类型
   * @return 返回设备工厂
   */
  static <T extends NettyDevice> Impl<T> newFactory(Class<T> deviceType) {
    return new Impl<>(deviceType);
  }

  class Impl<T extends NettyDevice> implements NettyDeviceFactory<T> {

    protected final Class<T> deviceType;

    public Impl(Class<T> deviceType) {
      this.deviceType = deviceType;
    }

    @Override
    public T create(String id, @Nullable Map<String, Object> attrs) {
      Channel ch = (Channel) attrs.get(ATTRS_CHANNEL);
      InetSocketAddress local = (InetSocketAddress) attrs.get(ATTRS_LOCAL);
      InetSocketAddress remote = (InetSocketAddress) attrs.get(ATTRS_REMOTE);
      return Instantiator.get().create(deviceType, id, ch, local, remote);
    }
  }
}
