package com.benefitj.netty.device;

import com.benefitj.core.EventLoop;
import com.benefitj.core.functions.WrappedMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
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
  long getExpire();

  /**
   * 设置过期时间
   *
   * @param expire 时间
   */
  void setExpire(long expire);

  /**
   * 获取过期时间单位
   */
  TimeUnit getExpireUnit();

  /**
   * 设置过期时间单位
   *
   * @param expireUnit 单位
   */
  void setExpireUnit(TimeUnit expireUnit);


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
    private long expire = 30L;
    /**
     * 过期时间单位
     */
    private TimeUnit expireUnit = TimeUnit.SECONDS;

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
    public long getExpire() {
      return expire;
    }

    @Override
    public void setExpire(long expire) {
      this.expire = expire;
    }

    @Override
    public TimeUnit getExpireUnit() {
      return expireUnit;
    }

    @Override
    public void setExpireUnit(TimeUnit expireUnit) {
      this.expireUnit = expireUnit;
    }

    @Override
    public V put(K key, V value) {
      if (value == null) {
        throw new IllegalArgumentException("value is null: " + key);
      }
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
      synchronized (this) {
        if (checkerRef.get() == null) {
          checkerRef.set(executor.scheduleAtFixedRate(this::selfCheck, 1, 1, TimeUnit.SECONDS));
        }
      }
    }

    public void stop() {
      synchronized (this) {
        EventLoop.cancel(checkerRef.getAndSet(null));
      }
    }

    protected EventLoop executor = EventLoop.single();
    protected final AtomicReference<ScheduledFuture<?>> checkerRef = new AtomicReference<>();

    protected void selfCheck() {
      if (!isEmpty()) {
        final Map<K, V> removeMap = new LinkedHashMap<>();
        long expireMillis = getExpireUnit().toMillis(getExpire());
        long now = System.currentTimeMillis();
        forEach((key, value) -> {
          if ((now - value.getActiveTime()) >= expireMillis) {
            removeMap.put(key, value);
          }
        });
        if (!removeMap.isEmpty()) {
          removeMap.forEach(this::remove);
        }
      }
    }

    public void setExecutor(EventLoop executor) {
      this.executor = executor;
    }

  }

}
