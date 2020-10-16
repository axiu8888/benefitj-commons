package com.benefitj.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
public class BiConsumerInboundHandler<I> extends ByteBufCopyInboundHandler<I> {

  private HandlerBiConsumer<I> consumer;

  public BiConsumerInboundHandler(Class<? extends I> inboundMessageType) {
    this(inboundMessageType, null);
  }

  public BiConsumerInboundHandler(Class<? extends I> inboundMessageType,
                                  HandlerBiConsumer<I> consumer) {
    super(inboundMessageType);
    this.setConsumer(consumer);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
    getConsumer().accept(this, ctx, msg);
  }

  public HandlerBiConsumer<I> getConsumer() {
    return consumer;
  }

  public void setConsumer(HandlerBiConsumer<I> consumer) {
    this.consumer = consumer;
  }

  /**
   * 创建Handler
   *
   * @param consumer 消费者
   * @return 返回创建的Handler
   */
  public static BiConsumerInboundHandler<ByteBuf> newByteBufHandler(HandlerBiConsumer<ByteBuf> consumer) {
    return new BiConsumerInboundHandler<>(ByteBuf.class, consumer);
  }

}
