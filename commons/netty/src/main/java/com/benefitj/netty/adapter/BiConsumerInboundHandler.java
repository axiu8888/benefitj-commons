package com.benefitj.netty.adapter;

import com.benefitj.netty.NettyBiConsumer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
public class BiConsumerInboundHandler<I> extends SimpleChannelInboundHandler<I> {

  private NettyBiConsumer<ChannelHandlerContext, I> consumer;

  public BiConsumerInboundHandler(Class<? extends I> inboundMessageType) {
    this(inboundMessageType, null);
  }

  public BiConsumerInboundHandler(Class<? extends I> inboundMessageType,
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

  /**
   * 创建Handler
   *
   * @param consumer 消费者
   * @return 返回创建的Handler
   */
  public static BiConsumerInboundHandler<ByteBuf> newByteBufHandler(NettyBiConsumer<ChannelHandlerContext, ByteBuf> consumer) {
    return new BiConsumerInboundHandler<>(ByteBuf.class, consumer);
  }

}
