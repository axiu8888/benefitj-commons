package com.benefitj.device;


import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 监听的代理
 *
 * @param <D>
 */
public class MultiDeviceListener<D extends Device> implements DeviceListener<D> {

  /**
   * 监听
   */
  private final List<DeviceListener<D>> listeners = new CopyOnWriteArrayList<>();

  /**
   * 添加监听
   *
   * @param listener 监听
   */
  public void addListener(DeviceListener<D> listener) {
    this.listeners.add(listener);
  }

  /**
   * 移除监听
   *
   * @param listener 监听
   */
  public void removeListener(DeviceListener<D> listener) {
    this.listeners.remove(listener);
  }

  /**
   * 添加监听
   *
   * @param listeners 监听
   */
  public void addListeners(List<? extends DeviceListener<D>> listeners) {
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
  public void onAddition(String id, D newDevice, @Nullable D oldDevice) {
    for (DeviceListener<D> l : this.listeners) {
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
  public void onRemoval(String id, D device) {
    for (DeviceListener<D> l : this.listeners) {
      try {
        l.onRemoval(id, device);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
