package com.benefitj.netty.server.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 现在设备检查
 */
public class OnlineClientChannelExecutor {

  private UdpClientManager<?> manager;

  private ScheduledFuture<?> expiredCheckerFuture;

  public OnlineClientChannelExecutor() {
  }

  public OnlineClientChannelExecutor(UdpClientManager<?> manager) {
    this.manager = manager;
  }

  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (getExpiredCheckerFuture() == null) {
      final UdpClientManager<?> m = getManager();
      long interval = m.getInterval();
      TimeUnit unit = m.getIntervalUnit();
      this.expiredCheckerFuture = ctx.executor()
          .scheduleAtFixedRate(m::checkExpiredClients, interval, interval, unit);
    }
  }

  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    ScheduledFuture<?> ecf = this.expiredCheckerFuture;
    if (ecf != null) {
      ecf.cancel(true);
      this.expiredCheckerFuture = null;
    }
  }

  public void setManager(UdpClientManager<?> manager) {
    this.manager = manager;
  }

  public UdpClientManager<?> getManager() {
    return manager;
  }

  public ScheduledFuture<?> getExpiredCheckerFuture() {
    return expiredCheckerFuture;
  }

}
