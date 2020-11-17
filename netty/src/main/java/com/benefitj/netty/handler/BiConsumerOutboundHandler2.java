package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public abstract class BiConsumerOutboundHandler2<I> extends ByteBufCopyOutboundHandler<I> {

  private OutboundHandlerBiConsumer<I> consumer;

  public BiConsumerOutboundHandler2() {
  }

  public BiConsumerOutboundHandler2(OutboundHandlerBiConsumer<I> consumer) {
    super();
    this.setConsumer(consumer);
  }

  @Override
  protected void channelWrite0(ChannelHandlerContext ctx, I msg, ChannelPromise promise) {
    OutboundHandlerBiConsumer<I> consumer = getConsumer();
    if (consumer != null) {
      consumer.accept(this, ctx, msg, promise);
    } else {
      ctx.write(msg, promise);
    }
  }

  public OutboundHandlerBiConsumer<I> getConsumer() {
    return consumer;
  }

  public void setConsumer(OutboundHandlerBiConsumer<I> consumer) {
    this.consumer = consumer;
  }
}
