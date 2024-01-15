package com.benefitj.device;

import com.benefitj.core.EventLoop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 默认设备超时检测
 *
 * @param <T>
 */
@Deprecated
public class DefaultExpireChecker<Id, T extends Device<Id>> implements ExpireChecker<Id, T> {

  private final Logger log = LoggerFactory.getLogger(getClass());

  /**
   * 延迟时间
   */
  private long initialDelay = 1;
  /**
   * 延迟间隔
   */
  private long delay = 1;
  /**
   * 超时时长，默认60秒
   */
  private long timeout = 60;
  /**
   * 时间单位
   */
  private TimeUnit unit = TimeUnit.SECONDS;
  /**
   * 设备管理器
   */
  private DeviceManager<Id, T> deviceManager;
  /**
   * 定时任务
   */
  private volatile ScheduledFuture<?> timer;

  /**
   * 被移除的设备
   */
  private final ThreadLocal<Map<Id, T>> localRemovalMap = ThreadLocal.withInitial(LinkedHashMap::new);

  public DefaultExpireChecker() {
  }

  public DefaultExpireChecker(DeviceManager<Id, T> deviceManager) {
    this.deviceManager = deviceManager;
  }

  public Map<Id, T> getRemovalMap() {
    return localRemovalMap.get();
  }

  /**
   * 开始
   */
  public void start() {
    startTimer();
  }

  protected void startTimer() {
    synchronized (this) {
      if (this.timer != null) {
        return;
      }
      // 启动定时
      this.timer = EventLoop.single().scheduleAtFixedRate(
          () -> check(getDeviceManager()), getInitialDelay(), getDelay(), getUnit());
    }
  }

  @Override
  public void check(DeviceManager<Id, T> manager) {
    if (manager.isNotEmpty()) {
      final Map<Id, T> removalMap = getRemovalMap();
      for (Map.Entry<Id, T> entry : manager.getDevices().entrySet()) {
        final T device = entry.getValue();
        if (isExpired(device)) {
          removalMap.put(entry.getKey(), device);
        }
      }

      if (!removalMap.isEmpty()) {
        for (Map.Entry<Id, T> entry : removalMap.entrySet()) {
          try {
            getDeviceManager().remove(entry.getKey());
          } catch (Exception e) {
            log.warn("throws on expired device: " + e.getMessage());
          }
        }
        removalMap.clear();
      }
    }
  }

  /**
   * 判断是否过期
   *
   * @param device 客户端
   * @return 返回是否过期
   */
  public boolean isExpired(T device) {
    // 当前时间 - activeTime时间 > 超时时间
    return (System.currentTimeMillis() - device.getActiveTime()) > getUnit().toMillis(getTimeout());
  }

  /**
   * 结束
   */
  public void stop() {
    ScheduledFuture<?> t = this.timer;
    if (t != null) {
      synchronized (this) {
        t.cancel(true);
        this.timer = null;
      }
    }
  }

  public long getInitialDelay() {
    return initialDelay;
  }

  public DefaultExpireChecker<Id, T> setInitialDelay(long initialDelay) {
    this.initialDelay = initialDelay;
    return this;
  }

  public long getDelay() {
    return delay;
  }

  public DefaultExpireChecker<Id, T> setDelay(long delay) {
    this.delay = delay;
    return this;
  }

  public long getTimeout() {
    return timeout;
  }

  public DefaultExpireChecker<Id, T> setTimeout(long timeout) {
    this.timeout = timeout;
    return this;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public DefaultExpireChecker<Id, T> setUnit(TimeUnit unit) {
    this.unit = unit;
    return this;
  }

  public DeviceManager<Id, T> getDeviceManager() {
    return deviceManager;
  }

  public DefaultExpireChecker<Id, T> setDeviceManager(DeviceManager<Id, T> deviceManager) {
    this.deviceManager = deviceManager;
    return this;
  }

}