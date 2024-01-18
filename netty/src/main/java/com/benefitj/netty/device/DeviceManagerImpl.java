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
 */
public class DeviceManagerImpl<K, V extends Device<K>> implements DeviceManager<K, V>, WrappedMap<K, V> {

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

  public DeviceManagerImpl() {
  }

  public DeviceManagerImpl(DeviceFactory<K, V> deviceFactory, DeviceListener<K, V> deviceListener) {
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
      ScheduledFuture<?> sf = checkerRef.getAndSet(null);
      if (sf != null) {
        sf.cancel(true);
      }
    }
  }

  protected static final EventLoop singleton = EventLoop.newSingle(true);
  protected EventLoop executor = singleton;
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
