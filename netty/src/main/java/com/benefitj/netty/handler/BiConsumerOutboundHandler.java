package com.benefitj.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

/**
 * 处理简单的消息
 *
 * @param <I>
 */
@ChannelHandler.Sharable
public class BiConsumerOutboundHandler<I> extends ByteBufCopyOutboundHandler<I> {

  private OutboundHandlerBiConsumer<I> consumer;

  public BiConsumerOutboundHandler(Class<? extends I> inboundMessageType) {
    this(inboundMessageType, null);
  }

  public BiConsumerOutboundHandler(Class<? extends I> inboundMessageType,
                                   OutboundHandlerBiConsumer<I> consumer) {
    super(inboundMessageType);
    this.setConsumer(consumer);
  }

  @Override
  protected void channelWrite0(ChannelHandlerContext ctx, I msg, ChannelPromise promise) {
    getConsumer().accept(this, ctx, msg, promise);
  }

  public OutboundHandlerBiConsumer<I> getConsumer() {
    return consumer;
  }

  public void setConsumer(OutboundHandlerBiConsumer<I> consumer) {
    this.consumer = consumer;
  }

  /**
   * 创建 byte[] 的Handler
   *
   * @param consumer 处理回调
   * @return 返回创建的 Handler
   */
  public static BiConsumerOutboundHandler<byte[]> newBytesHandler(OutboundHandlerBiConsumer<byte[]> consumer) {
    return new BiConsumerOutboundHandler<>(byte[].class, consumer);
  }

  /**
   * 创建 ByteBuf 的Handler
   *
   * @param consumer 处理回调
   * @return 返回创建的 Handler
   */
  public static BiConsumerOutboundHandler<ByteBuf> newByteBufHandler(OutboundHandlerBiConsumer<ByteBuf> consumer) {
    return new BiConsumerOutboundHandler<>(ByteBuf.class, consumer);
  }

  /**
   * 创建 DatagramPacket 的Handler
   *
   * @param consumer 处理回调
   * @return 返回创建的 Handler
   */
  public static BiConsumerOutboundHandler<DatagramPacket> newDatagramHandler(OutboundHandlerBiConsumer<DatagramPacket> consumer) {
    return new BiConsumerOutboundHandler<>(DatagramPacket.class, consumer);
  }

  public static <T> BiConsumerOutboundHandler<T> newHandler(Class<T> type, OutboundHandlerBiConsumer<T> consumer) {
    return new BiConsumerOutboundHandler<>(type, consumer);
  }
}
