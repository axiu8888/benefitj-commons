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

  private final Map<String, D> devices = new ConcurrentHashMap<>();

  protected Map<String, D> getDevices() {
    return devices;
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
    return getDevices().put(key, value);
  }

  @Override
  public D remove(Object key) {
    return getDevices().remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends D> m) {
    getDevices().putAll(m);
  }

  @Override
  public void clear() {
    getDevices().clear();
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
