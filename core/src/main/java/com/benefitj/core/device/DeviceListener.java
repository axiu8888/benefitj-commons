package com.benefitj.core.device;

import com.benefitj.core.ProxyUtils;

/**
 * 设备监听
 *
 * @param <T>
 */
public interface DeviceListener<Id, T> {

  /**
   * 被添加
   *
   * @param id     设备ID
   * @param device 新的设备
   */
  default void onAddition(Id id, T device) {
    // ~
  }

  /**
   * 被移除
   *
   * @param id     设备ID
   * @param device 设备
   */
  default void onRemoval(Id id, T device) {
    // ~
  }


  /**
   * 创建代理监听
   */
  static <Id, T> DeviceListener<Id, T> newMapListener() {
    return ProxyUtils.newMapProxy(DeviceListener.class);
  }

  /**
   * 创建代理监听
   */
  static <Id, T> DeviceListener<Id, T> newListListener() {
    return ProxyUtils.newCopyListProxy(DeviceListener.class);
  }

}
