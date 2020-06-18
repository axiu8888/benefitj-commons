package com.benefitj.netty.adapter;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 发送处理器
 */
public class UdpSendHandler extends ChannelDuplexHandler {

  private final UdpSender sender;

  public UdpSendHandler(UdpSender sender) {
    this.sender = sender;
  }

  public UdpSender getSender() {
    return sender;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    final UdpSender s = getSender();
    if (!s.getRunState() && s.getState() != Thread.State.TERMINATED) {
      s.startNow(ctx.channel());
    }
    super.channelActive(ctx);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    getSender().stopNow();
  }
}
