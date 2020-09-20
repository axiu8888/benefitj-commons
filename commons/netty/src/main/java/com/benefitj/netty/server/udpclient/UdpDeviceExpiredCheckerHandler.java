package com.benefitj.netty.server.udpclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * UDP设备过期检查
 */
public class UdpDeviceExpiredCheckerHandler extends ChannelInboundHandlerAdapter {

  /**
   * 检查过期
   */
  private final OnlineDeviceExpireExecutor executor;

  public UdpDeviceExpiredCheckerHandler(UdpDeviceClientManager<?> manager) {
    this.executor = new OnlineDeviceExpireExecutor(manager);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    executor.start();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    executor.stop();
  }

}
