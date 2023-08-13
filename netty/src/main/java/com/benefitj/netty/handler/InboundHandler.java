package com.benefitj.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * 处理的消息
 *
 * @param <I>
 */
@ChannelHandler.Sharable
public abstract class InboundHandler<I> extends SimpleByteBufHandler<I> {

  public InboundHandler() {
  }

  public InboundHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public InboundHandler(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  public InboundHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
  }

  @Override
  protected abstract void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception;

  /**
   * 创建Handler
   *
   * @param type     类型
   * @param consumer 消费者
   * @param <T>      消息类型
   * @return 返回Handler
   */
  public static <T> InboundHandler<T> newHandler(Class<T> type, InboundConsumer<T> consumer) {
    return new InboundHandler<T>(type, true) {
      @Override
      protected void channelRead0(ChannelHandlerContext ctx, T msg) throws Exception {
        consumer.accept(this, ctx, msg);
      }
    };
  }

  /**
   * 创建Handler
   *
   * @param consumer 消费者
   * @return 返回创建的Handler
   */
  public static InboundHandler<ByteBuf> newByteBufHandler(InboundConsumer<ByteBuf> consumer) {
    return newHandler(ByteBuf.class, consumer);
  }

  /**
   * 创建Handler
   *
   * @param consumer 消费者
   * @return 返回创建的Handler
   */
  public static InboundHandler<DatagramPacket> newDatagramHandler(InboundConsumer<DatagramPacket> consumer) {
    return newHandler(DatagramPacket.class, consumer);
  }

}
