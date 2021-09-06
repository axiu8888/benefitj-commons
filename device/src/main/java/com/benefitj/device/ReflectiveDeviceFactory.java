package com.benefitj.device;

import com.benefitj.core.ReflectUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
    try {
      T device = null;
      Class<? extends Device> type = getDeviceType();
      for (Constructor<?> c : type.getConstructors()) {
        if (ReflectUtils.isParameterTypesMatch(c.getParameterTypes(), new Object[]{id})) {
          ReflectUtils.setAccessible(c, true);
          device = (T) c.newInstance(new Object[]{id});
        }
      }
      if (device == null) {
        device = (T) type.newInstance();
      }
      device.setId(id);
      return device;
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  public Class<T> getDeviceType() {
    return deviceType;
  }

  public void setDeviceType(Class<T> deviceType) {
    this.deviceType = deviceType;
  }

}