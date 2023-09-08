package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public abstract class SimpleCopyHandler<I> extends AbstractCopyHandler<I> {

  public SimpleCopyHandler() {
  }

  public SimpleCopyHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public SimpleCopyHandler(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  public SimpleCopyHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
    ctx.fireChannelRead(msg);
  }

  @Override
  protected void channelWrite0(ChannelHandlerContext ctx, I msg, ChannelPromise promise) {
    ctx.write(msg, promise);
  }

}
