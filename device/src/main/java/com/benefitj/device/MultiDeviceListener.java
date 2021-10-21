package com.benefitj.device;


import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 监听的代理
 *
 * @param <D>
 */
public class MultiDeviceListener<Id, D extends Device<Id>> implements DeviceListener<Id, D> {

  /**
   * 监听
   */
  private final List<DeviceListener<Id, D>> listeners = new CopyOnWriteArrayList<>();

  /**
   * 添加监听
   *
   * @param listener 监听
   */
  public void addListener(DeviceListener<Id, D> listener) {
    this.listeners.add(listener);
  }

  /**
   * 移除监听
   *
   * @param listener 监听
   */
  public void removeListener(DeviceListener<Id, D> listener) {
    this.listeners.remove(listener);
  }

  /**
   * 添加监听
   *
   * @param listeners 监听
   */
  public void addListeners(List<? extends DeviceListener<Id, D>> listeners) {
    if (listeners != null) {
      listeners.forEach(this::addListener);
    }
  }

  /**
   * 被添加
   *
   * @param id        设备ID
   * @param newDevice 新的设备
   * @param oldDevice 旧的设备
   */
  @Override
  public void onAddition(Id id, D newDevice, @Nullable D oldDevice) {
    for (DeviceListener<Id, D> l : this.listeners) {
      try {
        l.onAddition(id, newDevice, oldDevice);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * 被移除
   *
   * @param id     设备ID
   * @param device 设备
   */
  @Override
  public void onRemoval(Id id, D device) {
    for (DeviceListener<Id, D> l : this.listeners) {
      try {
        l.onRemoval(id, device);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
