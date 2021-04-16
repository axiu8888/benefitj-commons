package com.benefitj.netty.server.device;

import io.netty.channel.Channel;

import java.util.Map;

/**
 * 设备管理
 *
 * @param <D> 设备类型
 */
public interface DeviceManager<D extends Device> extends Map<String, D> {

  /**
   * 获取设备工厂对象
   */
  DeviceFactory<D> getDeviceFactory();

  /**
   * 设置设备的工厂对象
   *
   * @param factory 设备工厂对象
   */
  void setDeviceFactory(DeviceFactory<D> factory);

  /**
   * 移除设备，通知设备下线
   *
   * @param key 设备ID
   * @return 返回被移除的设备
   */
  @Override
  D remove(Object key);

  /**
   * 移除设备
   *
   * @param key    设备ID
   * @param notify 是否通知设备被移除
   * @return 返回被移除的设备
   */
  D remove(Object key, boolean notify);

  /**
   * 获取设备状态监听
   */
  DeviceStateListener<D> getStateListener();

  /**
   * 设备状态监听
   *
   * @param listener 监听
   */
  void setStateListener(DeviceStateListener<D> listener);

  /**
   * 获取设备，如果不存在就创建
   *
   * @param id      设备ID
   * @param channel 通道
   * @return 返回设备
   */
  D computeIfAbsent(String id, Channel channel);

}
