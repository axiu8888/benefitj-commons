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
public abstract class OutboundHandler<I> extends SimpleCopyHandler<I> {

  public OutboundHandler() {
  }

  public OutboundHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public OutboundHandler(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  public OutboundHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
  }

  @Override
  protected abstract void channelWrite0(ChannelHandlerContext ctx, I msg, ChannelPromise promise);

  /**
   * 创建 byte[] 的Handler
   *
   * @param consumer 处理回调
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<byte[]> newBytesHandler(OutboundConsumer<byte[]> consumer) {
    return newHandler(byte[].class, consumer);
  }

  /**
   * 创建 ByteBuf 的Handler
   *
   * @param consumer 处理回调
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<ByteBuf> newByteBufHandler(OutboundConsumer<ByteBuf> consumer) {
    return newHandler(ByteBuf.class, consumer);
  }

  /**
   * 创建 DatagramPacket 的Handler
   *
   * @param consumer 处理回调
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<DatagramPacket> newDatagramHandler(OutboundConsumer<DatagramPacket> consumer) {
    return newHandler(DatagramPacket.class, consumer);
  }

  public static <T> OutboundHandler<T> newHandler(Class<T> type, OutboundConsumer<T> consumer) {
    return new OutboundHandler<T>(type) {
      @Override
      protected void channelWrite0(ChannelHandlerContext ctx, T msg, ChannelPromise promise) {
        consumer.channelWrite0(this, ctx, msg, promise);
      }

      @Override
      public void channelActive(ChannelHandlerContext ctx) throws Exception {
        consumer.channelActive(this, ctx);
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        consumer.channelInactive(this, ctx);
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        consumer.exceptionCaught(this, ctx, cause);
      }
    };
  }
}
