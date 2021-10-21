package com.benefitj.device;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 简单的设备管理器
 *
 * @param <T>
 */
public class DefaultDeviceManager<Id, T extends Device<Id>> implements DeviceManager<Id, T> {

  /**
   * 设备
   */
  private final Map<Id, T> devices = new ConcurrentHashMap<>();
  /**
   * 设备工厂
   */
  private DeviceFactory<Id, T> deviceFactory;
  /**
   * 设备监听
   */
  private DeviceListener<Id, T> deviceListener;

  public DefaultDeviceManager() {
  }

  public DefaultDeviceManager(DeviceFactory<Id, T> deviceFactory) {
    this.deviceFactory = deviceFactory;
  }

  @Override
  public Map<Id, T> getDevices() {
    return devices;
  }

  @Override
  public void setDeviceFactory(DeviceFactory<Id, T> factory) {
    this.deviceFactory = factory;
  }

  @Override
  public DeviceFactory<Id, T> getDeviceFactory() {
    return deviceFactory;
  }

  @Override
  public DeviceListener<Id, T> getDeviceListener() {
    return deviceListener;
  }

  @Override
  public void setDeviceListener(DeviceListener<Id, T> listener) {
    this.deviceListener = listener;
  }

}

