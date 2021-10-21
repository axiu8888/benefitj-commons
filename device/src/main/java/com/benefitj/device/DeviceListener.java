package com.benefitj.device;

import javax.annotation.Nullable;

/**
 * 设备监听
 *
 * @param <T>
 */
public interface DeviceListener<Id, T> {

  /**
   * 被添加
   *
   * @param id        设备ID
   * @param newDevice 新的设备
   * @param oldDevice 旧的设备
   */
  default void onAddition(Id id, T newDevice, @Nullable T oldDevice) {
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

}
