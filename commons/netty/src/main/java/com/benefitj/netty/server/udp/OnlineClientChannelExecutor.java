package com.benefitj.netty.server.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.ScheduledFuture;

import java.util.concurrent.TimeUnit;

/**
 * 在线设备检查
 */
public class OnlineClientChannelExecutor {

  private UdpClientManager<?> udpClientManager;

  private ScheduledFuture<?> expiredCheckerFuture;

  public OnlineClientChannelExecutor() {
  }

  public OnlineClientChannelExecutor(UdpClientManager<?> udpClientManager) {
    this.udpClientManager = udpClientManager;
  }

  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (getExpiredCheckerFuture() == null) {
      final UdpClientManager<?> m = getUdpClientManager();
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

  public void setUdpClientManager(UdpClientManager<?> udpClientManager) {
    this.udpClientManager = udpClientManager;
  }

  public UdpClientManager<?> getUdpClientManager() {
    return udpClientManager;
  }

  public ScheduledFuture<?> getExpiredCheckerFuture() {
    return expiredCheckerFuture;
  }

}
