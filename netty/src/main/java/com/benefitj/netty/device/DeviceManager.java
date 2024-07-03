package com.benefitj.netty.device;

import com.benefitj.core.functions.WrappedMap;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;


/**
 * 设备管理
 *
 * @param <K> ID类型
 * @param <V> 设备类型
 */
public interface DeviceManager<K, V extends Device<K>> extends Map<K, V> {

  /**
   * 创建设备
   *
   * @param k     ID
   * @param attrs 属性
   * @return 返回新创建的设备
   */
  default V create(K k, Map<String, Object> attrs) {
    V device = getDeviceFactory().create(k, attrs);
    device.setActiveTimeNow();
    put(k, device);
    return device;
  }

  /**
   * 设置设备工程
   *
   * @param factory 工厂
   */
  void setDeviceFactory(DeviceFactory<K, V> factory);

  /**
   * 获取设备工厂
   */
  DeviceFactory<K, V> getDeviceFactory();

  /**
   * 设置设备监听
   *
   * @param listener 监听
   */
  void setDeviceListener(DeviceListener<K, V> listener);

  /**
   * 获取设备监听
   */
  DeviceListener<K, V> getDeviceListener();

  /**
   * 获取过期时间
   */
  Duration getExpire();

  /**
   * 设置过期时间
   *
   * @param expire 时间
   */
  void setExpire(Duration expire);

  /**
   * 设备管理
   */
  class Impl<K, V extends Device<K>> implements DeviceManager<K, V>, WrappedMap<K, V> {

    final Map<K, V> map = new ConcurrentHashMap<>();
    /**
     * 设备工厂
     */
    private DeviceFactory<K, V> deviceFactory;
    /**
     * 设备监听
     */
    private DeviceListener<K, V> deviceListener;
    /**
     * 过期时间
     */
    private Duration expire = Duration.ofSeconds(30L);

    public Impl() {
    }

    public Impl(DeviceFactory<K, V> deviceFactory, DeviceListener<K, V> deviceListener) {
      this.deviceFactory = deviceFactory;
      this.deviceListener = deviceListener;
    }

    @Override
    public Map<K, V> map() {
      return map;
    }

    @Override
    public void setDeviceFactory(DeviceFactory<K, V> factory) {
      this.deviceFactory = factory;
    }

    @Override
    public DeviceFactory<K, V> getDeviceFactory() {
      return deviceFactory;
    }

    @Override
    public void setDeviceListener(DeviceListener<K, V> listener) {
      this.deviceListener = listener;
    }

    @Override
    public DeviceListener<K, V> getDeviceListener() {
      return deviceListener;
    }

    @Override
    public Duration getExpire() {
      return expire;
    }

    @Override
    public void setExpire(Duration expire) {
      this.expire = expire;
    }

    @Override
    public V put(K key, V value) {
      if (key == null || value == null)
        throw new IllegalArgumentException("The key or value is null: " + key + ", " + value + "]");
      V old = map().put(key, value);
      if (old != value) {
        getDeviceListener().onAddition(key, value);
      }
      return old;
    }

    @Override
    public boolean remove(Object key, Object value) {
      boolean remove = map().remove(key, value);
      if (remove) {
        getDeviceListener().onRemoval((K) key, (V) value);
      }
      return remove;
    }

    @Override
    public V remove(Object key) {
      V remove = map().remove(key);
      if (remove != null) {
        getDeviceListener().onRemoval((K) key, remove);
      }
      return remove;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
      m.forEach(this::put);
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
      throw new UnsupportedOperationException("不支持此操作!");
    }

    @Override
    public V replace(K key, V value) {
      throw new UnsupportedOperationException("不支持此操作!");
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      throw new UnsupportedOperationException("不支持此操作!");
    }

    public void start() {
    }

    public void stop() {
    }

  }

  /**
   * 移除超时过期的设备
   *
   * @param manager 设备管理对象
   * @return 返回被移除的设备
   */
  static <K, V extends Device<K>> Map<K, V> removeInactive(DeviceManager<K, V> manager) {
    return removeInactive(manager, true);
  }

  /**
   * 移除超时过期的设备
   *
   * @param manager    设备管理对象
   * @param autoRemove 是否自动移除
   * @return 返回被移除的设备
   */
  static <K, V extends Device<K>> Map<K, V> removeInactive(DeviceManager<K, V> manager, boolean autoRemove) {
    if (!manager.isEmpty()) {
      final Map<K, V> removeMap = new LinkedHashMap<>();
      long expireMillis = manager.getExpire().toMillis();
      long now = System.currentTimeMillis();
      manager.forEach((key, value) -> {
        if ((now - value.getActiveTime()) >= expireMillis) {
          removeMap.put(key, value);
        }
      });
      if (!removeMap.isEmpty() && autoRemove) {
        removeMap.forEach(manager::remove);
      }
      return removeMap;
    }
    return Collections.emptyMap();
  }

}
