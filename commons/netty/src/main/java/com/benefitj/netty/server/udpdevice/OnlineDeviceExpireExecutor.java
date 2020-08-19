package com.benefitj.netty.server.udpdevice;

import io.netty.channel.ChannelHandlerContext;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 在线设备检查
 */
public class OnlineDeviceExpireExecutor {

  private UdpDeviceClientManager<?> clientManager;

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

  public OnlineDeviceExpireExecutor(UdpDeviceClientManager<?> clientManager) {
    this.clientManager = clientManager;
  }

  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.activeCount.incrementAndGet();
    if (this.future.get() == null) {
      synchronized (this) {
        if (this.future.get() == null) {
          final UdpDeviceClientManager<?> manager = getClientManager();
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

  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
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

  public void setClientManager(UdpDeviceClientManager<?> clientManager) {
    this.clientManager = clientManager;
  }

  public UdpDeviceClientManager<?> getClientManager() {
    return clientManager;
  }

  public ScheduledExecutorService executor() {
    return executor;
  }
}
