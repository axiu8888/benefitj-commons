package com.benefitj.device;

import com.benefitj.core.ReflectUtils;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 默认的设备工厂
 */
public class ReflectiveDeviceFactory<T extends Device> implements DeviceFactory<T> {

  /**
   * 创建设备工厂
   */
  public static DeviceFactory<DeviceImpl> newBasicFactory() {
    return newInstance(DeviceImpl.class);
  }

  /**
   * 创建设备工厂
   */
  public static <T extends Device> DeviceFactory<T> newInstance(Class<T> deviceType) {
    return new ReflectiveDeviceFactory<>(deviceType);
  }

  private Class<T> deviceType;

  public ReflectiveDeviceFactory(Class<T> deviceType) {
    this.deviceType = deviceType;
  }

  @Override
  public T create(String id, @Nullable Map<String, Object> attrs) {
    T device = newInstance(id, attrs);
    device.setOnlineTime(System.currentTimeMillis());
    device.setActiveTime(System.currentTimeMillis());
    if (attrs != null && !attrs.isEmpty()) {
      attrs.forEach(device::setAttr);
    }
    return device;
  }

  public T newInstance(String id, Map<String, Object> attrs) {
    T device = ReflectUtils.newInstance(getDeviceType());
    device.setId(id);
    return device;
  }

  public Class<T> getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(Class<T> deviceType) {
    this.deviceType = deviceType;
  }

}