package com.benefitj.netty.device;

import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 设备管理
 *
 * @param <K> ID类型
 * @param <V> 设备类型
 */
public interface DeviceManager<K, V extends Device<K>> extends Map<K, V> {

  /**
   * 创建设备
   *
   * @param k     ID
   * @param attrs 属性
   * @return 返回新创建的设备
   */
  default V create(K k, Map<String, Object> attrs) {
    V device = getDeviceFactory().create(k, attrs);
    device.setActiveTimeNow();
    put(k, device);
    return device;
  }

  /**
   * 设置设备工程
   *
   * @param factory 工厂
   */
  void setDeviceFactory(DeviceFactory<K, V> factory);

  /**
   * 获取设备工厂
   */
  DeviceFactory<K, V> getDeviceFactory();

  /**
   * 设置设备监听
   *
   * @param listener 监听
   */
  void setDeviceListener(DeviceListener<K, V> listener);

  /**
   * 获取设备监听
   */
  DeviceListener<K, V> getDeviceListener();

  /**
   * 获取过期时间
   */
  long getExpire();

  /**
   * 设置过期时间
   *
   * @param expire 时间
   */
  void setExpire(long expire);

  /**
   * 获取过期时间单位
   */
  TimeUnit getExpireUnit();

  /**
   * 设置过期时间单位
   *
   * @param expireUnit 单位
   */
  void setExpireUnit(TimeUnit expireUnit);

}
