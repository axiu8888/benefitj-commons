package com.benefitj.netty.adapter;

import io.netty.channel.ChannelHandler;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 标记为可复用的 SimpleChannelInboundHandler
 *
 * @param <I>
 */
@ChannelHandler.Sharable
public abstract class SharableSimpleInboundHandler<I> extends SimpleChannelInboundHandler<I> {
}
