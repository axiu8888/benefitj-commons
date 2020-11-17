package com.benefitj.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
public class BiConsumerInboundHandler<I> extends ByteBufCopyInboundHandler<I> {

  private InboundHandlerBiConsumer<I> consumer;

  public BiConsumerInboundHandler(Class<? extends I> inboundMessageType) {
    this(inboundMessageType, null);
  }

  public BiConsumerInboundHandler(Class<? extends I> inboundMessageType,
                                  InboundHandlerBiConsumer<I> consumer) {
    super(inboundMessageType);
    this.setConsumer(consumer);
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception {
    getConsumer().accept(this, ctx, msg);
  }

  public InboundHandlerBiConsumer<I> getConsumer() {
    return consumer;
  }

  public void setConsumer(InboundHandlerBiConsumer<I> consumer) {
    this.consumer = consumer;
  }

  /**
   * 创建Handler
   *
   * @param consumer 消费者
   * @return 返回创建的Handler
   */
  public static BiConsumerInboundHandler<ByteBuf> newByteBufHandler(InboundHandlerBiConsumer<ByteBuf> consumer) {
    return new BiConsumerInboundHandler<>(ByteBuf.class, consumer);
  }

  /**
   * 创建Handler
   *
   * @param consumer 消费者
   * @return 返回创建的Handler
   */
  public static BiConsumerInboundHandler<DatagramPacket> newDatagramHandler(InboundHandlerBiConsumer<DatagramPacket> consumer) {
    return new BiConsumerInboundHandler<>(DatagramPacket.class, consumer);
  }

}
