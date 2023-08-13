package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public abstract class SimpleByteBufHandler<I> extends ByteBufCopyHandler<I> {

  public SimpleByteBufHandler() {
  }

  public SimpleByteBufHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public SimpleByteBufHandler(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  public SimpleByteBufHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
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
