package com.benefitj.netty.handler;

import com.benefitj.netty.NettyBiConsumer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
@ChannelHandler.Sharable
public abstract class BiConsumerInboundHandler2<I> extends ByteBufCopyInboundHandler<I> {

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
