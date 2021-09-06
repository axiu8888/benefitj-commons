package com.benefitj.device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的设备工厂
 *
 * @param <T>
 */
public class DefaultDeviceManager<T extends Device> implements DeviceManager<T> {

  /**
   * 设备
   */
  private final Map<String, T> devices = new ConcurrentHashMap<>();
  /**
   * 设备工厂
   */
  private DeviceFactory<T> deviceFactory;
  /**
   * 设备监听
   */
  private DeviceListener<T> deviceListener;

  public DefaultDeviceManager() {
  }

  public DefaultDeviceManager(DeviceFactory<T> deviceFactory) {
    this.deviceFactory = deviceFactory;
  }

  @Override
  public Map<String, T> getDevices() {
    return devices;
  }

  @Override
  public void setDeviceFactory(DeviceFactory<T> factory) {
    this.deviceFactory = factory;
  }

  @Override
  public DeviceFactory<T> getDeviceFactory() {
    return deviceFactory;
  }

  @Override
  public DeviceListener<T> getDeviceListener() {
    return deviceListener;
  }

  @Override
  public void setDeviceListener(DeviceListener<T> listener) {
    this.deviceListener = listener;
  }

}

