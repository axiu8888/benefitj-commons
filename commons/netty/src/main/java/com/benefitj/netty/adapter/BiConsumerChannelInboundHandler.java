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

  private NettyBiConsumer<ChannelHandlerContext, I> consumer;

  public BiConsumerChannelInboundHandler(Class<? extends I> inboundMessageType) {
    this(inboundMessageType, null);
  }

  public BiConsumerChannelInboundHandler(Class<? extends I> inboundMessageType,
                                         NettyBiConsumer<ChannelHandlerContext, I> consumer) {
    super(inboundMessageType);
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
