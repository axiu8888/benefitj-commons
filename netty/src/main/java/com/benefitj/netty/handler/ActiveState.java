package com.benefitj.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

public enum ActiveState {
  /**
   * {@link ChannelDuplexHandler#channelActive(ChannelHandlerContext)}
   */
  ACTIVE(true),
  /**
   * {@link ChannelDuplexHandler#channelInactive(ChannelHandlerContext)}
   */
  INACTIVE(false);

  private final boolean active;

  ActiveState(boolean active) {
    this.active = active;
  }

  public boolean isActive() {
    return active;
  }

}

