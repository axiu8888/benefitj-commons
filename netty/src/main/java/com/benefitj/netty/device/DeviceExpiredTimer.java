package com.benefitj.netty.device;


import com.benefitj.core.EventLoop;

import java.time.Duration;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 设备超时检查调度
 */
public class DeviceExpiredTimer {
  /**
   * 全局唯一线程调度
   */
  static final EventLoop SINGLE = EventLoop.newSingle(true);

  final AtomicReference<ScheduledFuture<?>> expiredTimer = new AtomicReference<>();
  final AtomicBoolean lock = new AtomicBoolean(false);
  /**
   * 线程调度
   */
  private EventLoop executor;

  public DeviceExpiredTimer() {
    this(SINGLE);
  }

  public DeviceExpiredTimer(EventLoop executor) {
    this.executor = executor;
  }

  /**
   * 开始调度
   *
   * @param manager  设备管理类
   * @param interval 检查的间隔
   */
  public void start(DeviceManager<?, ?> manager, Duration interval) {
    long period = Math.max(interval.toSeconds(), 1);
    EventLoop.cancel(expiredTimer.getAndSet(executor.scheduleAtFixedRate(() -> {
      if (lock.compareAndSet(false, true)) {
        try {
          DeviceManager.removeInactive(manager);
        } finally {
          lock.set(false);
        }
      }
    }, period, period, TimeUnit.SECONDS)));
  }

  public void stop() {
    EventLoop.cancel(expiredTimer.getAndSet(null));
  }

  public EventLoop getExecutor() {
    return executor;
  }

  public void setExecutor(EventLoop executor) {
    this.executor = executor;
  }

}

