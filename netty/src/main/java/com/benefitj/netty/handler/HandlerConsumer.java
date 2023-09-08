package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import lombok.NonNull;

public interface HandlerConsumer<T> {

  default void channelActive(T handler, @NonNull ChannelHandlerContext ctx) throws Exception {
    ctx.fireChannelActive();
  }

  default void channelInactive(T handler, @NonNull ChannelHandlerContext ctx) throws Exception {
    ctx.fireChannelInactive();
  }

  default void userEventTriggered(T handler, ChannelHandlerContext ctx, Object evt) {
    ctx.fireUserEventTriggered(evt);
  }

  default void exceptionCaught(T handler, ChannelHandlerContext ctx, Throwable cause) throws Exception {
    ctx.fireExceptionCaught(cause);
  }

}
