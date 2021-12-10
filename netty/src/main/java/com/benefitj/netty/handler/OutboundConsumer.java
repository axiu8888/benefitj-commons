package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public interface OutboundConsumer<I> {

  /**
   * 处理消息
   *
   * @param handler 处理器
   * @param ctx     上下文对象
   * @param msg     消息
   */
  void accept(OutboundHandler<I> handler, ChannelHandlerContext ctx, I msg, ChannelPromise promise);

}
