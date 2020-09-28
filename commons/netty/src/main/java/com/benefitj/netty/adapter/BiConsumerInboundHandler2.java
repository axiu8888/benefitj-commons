package com.benefitj.netty.adapter;

import com.benefitj.netty.NettyBiConsumer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
public abstract class BiConsumerInboundHandler2<I> extends SimpleChannelInboundHandler<I> {

  private NettyBiConsumer<ChannelHandlerContext, I> consumer;

  public BiConsumerInboundHandler2(NettyBiConsumer<ChannelHandlerContext, I> consumer) {
    super(true);
    this.setConsumer(consumer);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
    getConsumer().accept(ctx, msg);
  }

  public NettyBiConsumer<ChannelHandlerContext, I> getConsumer() {
    return consumer;
  }

  public void setConsumer(NettyBiConsumer<ChannelHandlerContext, I> consumer) {
    this.consumer = consumer;
  }

}
