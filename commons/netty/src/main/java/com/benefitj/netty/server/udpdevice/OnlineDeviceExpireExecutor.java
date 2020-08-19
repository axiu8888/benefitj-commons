package com.benefitj.netty.server.udpdevice;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 在线设备检查
 */
public class OnlineDeviceExpireExecutor {

  private UdpDeviceClientManager<?> clientManager;

  private final AtomicReference<ScheduledFuture<?>> checkerFuture = new AtomicReference<>();
  /**
   * 统计 channelActive 调用次数
   */
  private final AtomicInteger activeCount = new AtomicInteger(0);

  public OnlineDeviceExpireExecutor() {
  }

  public OnlineDeviceExpireExecutor(UdpDeviceClientManager<?> clientManager) {
    this.clientManager = clientManager;
  }

  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    this.activeCount.incrementAndGet();
    if (this.checkerFuture.get() == null) {
      synchronized (this) {
        if (this.checkerFuture.get() == null) {
          final UdpDeviceClientManager<?> manager = getClientManager();
          ScheduledFuture<?> sf = ctx.executor().scheduleAtFixedRate(manager::autoCheckExpire
              , manager.getDelay()
              , manager.getDelay()
              , manager.getDelayUnit());
          if (!this.checkerFuture.compareAndSet(null, sf)) {
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
        ScheduledFuture<?> sf = checkerFuture.get();
        if (sf != null && checkerFuture.compareAndSet(sf, null)) {
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

}
