package com.benefitj.netty.adapter;

import com.benefitj.netty.NettyBiConsumer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
public class BiConsumerChannelInboundHandler<I> extends SimpleChannelInboundHandler<I> {

  private final NettyBiConsumer<ChannelHandlerContext, I> consumer;

  public BiConsumerChannelInboundHandler(NettyBiConsumer<ChannelHandlerContext, I> consumer) {
    this.consumer = consumer;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
    consumer.accept(ctx, msg);
  }

}
