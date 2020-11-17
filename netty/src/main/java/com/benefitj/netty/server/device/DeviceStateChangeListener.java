package com.benefitj.netty.server.device;

import javax.annotation.Nullable;

/**
 * 设备状态改变的监听
 */
public interface DeviceStateChangeListener<D extends Device> {

  /**
   * 被添加
   *
   * @param id        设备ID
   * @param newDevice 新的设备
   * @param oldDevice 旧的设备
   */
  default void onAddition(String id, D newDevice, @Nullable D oldDevice) {
    // ~
  }

  /**
   * 被移除
   *
   * @param id     设备ID
   * @param device 设备
   */
  default void onRemoval(String id, D device) {
    // ~
  }

  /**
   * 设备监听
   */
  static <D extends Device> DeviceStateChangeListener<D> emptyListener() {
    return new DeviceStateChangeListener<D>() {};
  }

}
