package com.benefitj.netty.server.device;

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
  private DeviceStateChangeListener<D> stateChangeListener = DeviceStateChangeListener.emptyListener();

  public DefaultDeviceManager() {
  }

  public DefaultDeviceManager(DeviceStateChangeListener<D> stateChangeListener) {
    this.stateChangeListener = stateChangeListener;
  }

  protected Map<String, D> getDevices() {
    return devices;
  }

  @Override
  public DeviceStateChangeListener<D> getStateChangeListener() {
    return stateChangeListener;
  }

  @Override
  public void setStateChangeListener(DeviceStateChangeListener<D> listener) {
    this.stateChangeListener = (listener != null ? listener : DeviceStateChangeListener.emptyListener());
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
    getStateChangeListener().onAddition(key, value, old);
    return old;
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
        getStateChangeListener().onRemoval((String)key, device);
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
