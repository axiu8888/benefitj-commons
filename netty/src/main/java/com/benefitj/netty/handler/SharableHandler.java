package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandler;

/**
 * 标记为可复用的 SimpleChannelInboundHandler
 *
 * @param <I>
 */
@ChannelHandler.Sharable
public abstract class SharableHandler<I> extends SimpleByteBufHandler<I> {
}
