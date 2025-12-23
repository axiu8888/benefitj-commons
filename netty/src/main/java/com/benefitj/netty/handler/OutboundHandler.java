package com.benefitj.netty.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

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

  /**
   * 创建将消息转换为ByteBuf的Handler
   *
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<Object> msgToByteBufHandler() {
    return msgToByteBufHandler(false, StandardCharsets.UTF_8);
  }

  /**
   * 创建将消息转换为ByteBuf的Handler
   *
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<Object> msgToByteBufHandler(boolean strToBytes, Charset charset) {
    return newHandler(Object.class, (handler, ctx, msg, promise) -> {
      if (msg instanceof DatagramPacket) {
        ctx.write(((DatagramPacket) msg).content(), promise);
      } else if (msg instanceof byte[]) {
        ctx.write(Unpooled.wrappedBuffer((byte[]) msg), promise);
      } else {
        if (strToBytes && msg instanceof CharSequence) {
          ctx.write(Unpooled.wrappedBuffer(msg.toString().getBytes(charset)), promise);
        } else {
          ctx.write(msg, promise);
        }
      }
    });
  }

  /**
   * 创建将消息转换为Bytes的Handler
   *
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<Object> msgToBytesHandler() {
    return msgToBytesHandler(false, StandardCharsets.UTF_8);
  }

  /**
   * 创建将消息转换为Bytes的Handler
   *
   * @return 返回创建的 Handler
   */
  public static OutboundHandler<Object> msgToBytesHandler(boolean strToBytes, Charset charset) {
    return newHandler(Object.class, (handler, ctx, msg, promise) -> {
      if (msg instanceof DatagramPacket) {
        ctx.write(handler.copy((DatagramPacket) msg), promise);
      } else if (msg instanceof ByteBuf) {
        ctx.write(handler.copy((ByteBuf) msg), promise);
      } else {
        if (strToBytes && msg instanceof CharSequence) {
          ctx.write(msg.toString().getBytes(charset), promise);
        } else {
          ctx.write(msg, promise);
        }
      }
    });
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
      public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        consumer.userEventTriggered(this, ctx, evt);
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        consumer.exceptionCaught(this, ctx, cause);
      }
    };
  }

}
