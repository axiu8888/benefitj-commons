package com.benefitj.netty.server.device;

import io.netty.channel.Channel;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备管理
 *
 * @param <D> 设备类型
 */
public class DefaultDeviceManager<D extends Device> implements DeviceManager<D> {

  /**
   * 设备
   */
  private final Map<String, D> devices = new ConcurrentHashMap<>(16);
  /**
   * 设备状态监听
   */
  private DeviceStateListener<D> stateListener = DeviceStateListener.emptyListener();
  /**
   * 设备工厂
   */
  private DeviceFactory<D> deviceFactory;

  public DefaultDeviceManager() {
  }

  public DefaultDeviceManager(DeviceStateListener<D> stateListener) {
    this.stateListener = stateListener;
  }

  protected Map<String, D> getDevices() {
    return devices;
  }

  @Override
  public DeviceStateListener<D> getStateListener() {
    return stateListener;
  }

  @Override
  public void setStateListener(DeviceStateListener<D> listener) {
    this.stateListener = (listener != null ? listener : DeviceStateListener.emptyListener());
  }

  @Override
  public D computeIfAbsent(String id, Channel channel) {
    D device = get(id);
    if (device == null) {
      DeviceFactory<D> factory = getDeviceFactory();
      if (factory == null) {
        throw new IllegalStateException("DeviceFactory == null !");
      }
      device = factory.create(id, channel);
      this.put(id, device);
    }
    return device;
  }

  @Override
  public int size() {
    return getDevices().size();
  }

  @Override
  public boolean isEmpty() {
    return getDevices().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getDevices().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getDevices().containsValue(value);
  }

  @Override
  public D get(Object key) {
    return getDevices().get(key);
  }

  @Override
  public D put(String key, D value) {
    D old = getDevices().put(key, value);
    getStateListener().onAddition(key, value, old);
    return old;
  }

  @Override
  public DeviceFactory<D> getDeviceFactory() {
    return deviceFactory;
  }

  @Override
  public void setDeviceFactory(DeviceFactory<D> deviceFactory) {
    this.deviceFactory = deviceFactory;
  }

  @Override
  public D remove(Object key) {
    return remove(key, true);
  }

  @Override
  public D remove(Object key, boolean notify) {
    if (key instanceof String) {
      D device = getDevices().remove(key);
      if (notify && device != null) {
        getStateListener().onRemoval((String) key, device);
      }
      return device;
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends String, ? extends D> m) {
    for (Entry<? extends String, ? extends D> entry : m.entrySet()) {
      this.put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    for (String id : keySet()) {
      remove(id, true);
    }
  }

  @Override
  public Set<String> keySet() {
    return getDevices().keySet();
  }

  @Override
  public Collection<D> values() {
    return getDevices().values();
  }

  @Override
  public Set<Entry<String, D>> entrySet() {
    return getDevices().entrySet();
  }

}
