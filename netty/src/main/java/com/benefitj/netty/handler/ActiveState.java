package com.benefitj.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public enum ActiveState {
  /**
   * {@link ChannelDuplexHandler#channelActive(ChannelHandlerContext)}
   */
  ACTIVE,
  /**
   * {@link ChannelDuplexHandler#channelInactive(ChannelHandlerContext)}
   */
  INACTIVE;
}
