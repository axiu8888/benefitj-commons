package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;

public interface HandlerBiConsumer<I> {

  /**
   * 处理消息
   *
   * @param handler 处理器
   * @param ctx     上下文对象
   * @param msg     消息
   */
  void accept(ByteBufCopyInboundHandler<I> handler, ChannelHandlerContext ctx, I msg);

}
