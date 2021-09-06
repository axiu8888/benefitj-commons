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
public class DefaultExpiredChecker<T extends Device> implements ExpiredChecker<T> {

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
  private DeviceManager<T> deviceManager;
  /**
   * 定时任务
   */
  private ScheduledFuture<?> timer;

  /**
   * 被移除的设备
   */
  private final ThreadLocal<Map<String, T>> localRemovalMap = ThreadLocal.withInitial(LinkedHashMap::new);

  public DefaultExpiredChecker(DeviceManager<T> deviceManager) {
    this.deviceManager = deviceManager;
  }

  public Map<String, T> getRemovalMap() {
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
      this.timer = EventLoop.io().scheduleAtFixedRate(
          () -> check(getDeviceManager()), getInitialDelay(), getDelay(), getUnit());
    }
  }

  @Override
  public void check(DeviceManager<T> manager) {
    if (manager.size() > 0) {
      final Map<String, T> removalMap = getRemovalMap();
      for (Map.Entry<String, T> entry : manager.getDevices().entrySet()) {
        final T device = entry.getValue();
        if (isExpired(device)) {
          removalMap.put(entry.getKey(), device);
        }
      }

      if (!removalMap.isEmpty()) {
        for (Map.Entry<String, T> entry : removalMap.entrySet()) {
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

  public void setInitialDelay(long initialDelay) {
    this.initialDelay = initialDelay;
  }

  public long getDelay() {
    return delay;
  }

  public void setDelay(long delay) {
    this.delay = delay;
  }

  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public TimeUnit getUnit() {
    return unit;
  }

  public void setUnit(TimeUnit unit) {
    this.unit = unit;
  }

  public DeviceManager<T> getDeviceManager() {
    return deviceManager;
  }

  public void setDeviceManager(DeviceManager<T> deviceManager) {
    this.deviceManager = deviceManager;
  }

}