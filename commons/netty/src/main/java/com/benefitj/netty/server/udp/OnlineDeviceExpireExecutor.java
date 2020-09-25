package com.benefitj.netty.server.udp;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 在线设备检查
 */
public class OnlineDeviceExpireExecutor {

  private UdpDeviceManager<?> clientManager;

  private final AtomicReference<ScheduledFuture<?>> future = new AtomicReference<>();
  /**
   * 统计 channelActive 调用次数
   */
  private final AtomicInteger activeCount = new AtomicInteger(0);
  /**
   * 在线状态检查执行调度器
   */
  private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

  public OnlineDeviceExpireExecutor() {
  }

  public OnlineDeviceExpireExecutor(UdpDeviceManager<?> clientManager) {
    this.clientManager = clientManager;
  }

  public void start() {
    this.activeCount.incrementAndGet();
    if (this.future.get() == null) {
      synchronized (this) {
        if (this.future.get() == null) {
          final UdpDeviceManager<?> manager = getClientManager();
          ScheduledFuture<?> sf = executor().scheduleAtFixedRate(manager::autoCheckExpire
              , manager.getDelay()
              , manager.getDelay()
              , manager.getDelayUnit());
          if (!this.future.compareAndSet(null, sf)) {
            sf.cancel(true);
          }
        }
      }
    }
  }

  public void stop() {
    this.activeCount.decrementAndGet();
    if (this.activeCount.get() <= 0) {
      synchronized (this) {
        ScheduledFuture<?> sf = future.get();
        if (sf != null && future.compareAndSet(sf, null)) {
          getClientManager().autoCheckExpire();
          sf.cancel(true);
          this.activeCount.set(0);
        }
      }
    }
  }

  public void setClientManager(UdpDeviceManager<?> clientManager) {
    this.clientManager = clientManager;
  }

  public UdpDeviceManager<?> getClientManager() {
    return clientManager;
  }

  public ScheduledExecutorService executor() {
    return executor;
  }
}
