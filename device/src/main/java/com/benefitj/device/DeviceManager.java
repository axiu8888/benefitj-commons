package com.benefitj.device;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 设备管理器
 *
 * @param <T>
 */
public interface DeviceManager<Id, T extends Device<Id>> {

  /**
   * 获取全部的设备
   */
  Map<Id, T> getDevices();

  /**
   * 保存设备
   *
   * @param id     设备ID
   * @param device 设备
   */
  default void put(Id id, T device) {
    T oldDevice = getDevices().put(id, device);
    getDeviceListener().onAddition(id, device, oldDevice);
  }

  /**
   * 获取设备
   *
   * @param id 设备ID
   * @return 返回设备
   */
  default T get(Id id) {
    return getDevices().get(id);
  }

  /**
   * 移除设备
   *
   * @param id 设备ID
   * @return 返回被移除的设备
   */
  default T remove(Id id) {
    T device = getDevices().remove(id);
    if (device != null) {
      getDeviceListener().onRemoval(id, device);
    }
    return device;
  }

  /**
   * 创建新的设备对象
   *
   * @param id    设备ID
   * @param attrs 设备属性
   * @return 返回新创建的设备对象
   */
  default T create(Id id, Map<String, Object> attrs) {
    return getDeviceFactory().create(id, attrs);
  }

  /**
   * 获取设备，如果没有就创建
   *
   * @param id    设备ID
   * @param attrs 属性
   * @return 返回新创建的设备对象
   */
  default T computeIfAbsent(Id id, Map<String, Object> attrs) {
    T device = get(id);
    if (device == null) {
      synchronized (this) {
        if ((device = get(id)) == null) {
          device = create(id, attrs);
          put(id, device);
        }
      }
    }
    return device;
  }

  /**
   * 设备数量
   */
  default int size() {
    return getDevices().size();
  }

  /**
   * 是否包含某个设备
   *
   * @param id 设备ID
   * @return 返回检查结果
   */
  default boolean contains(Id id) {
    return getDevices().containsKey(id);
  }

  /**
   * 获取全部的设备ID
   */
  default Set<Id> ids() {
    return getDevices().keySet();
  }

  /**
   * 获取全部的设备
   */
  default Collection<T> devices() {
    return getDevices().values();
  }

  /**
   * 设置设备工程
   *
   * @param factory 工厂
   */
  void setDeviceFactory(DeviceFactory<Id, T> factory);

  /**
   * 获取设备工厂
   */
  DeviceFactory<Id, T> getDeviceFactory();

  /**
   * 获取设备监听
   */
  DeviceListener<Id, T> getDeviceListener();

  /**
   * 设置设备监听
   *
   * @param listener 监听
   */
  void setDeviceListener(DeviceListener<Id, T> listener);

}
