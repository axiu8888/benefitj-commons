package com.benefitj.netty.server.device;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

/**
 * TCP 设备
 */
public class TcpDevice extends AbstractDevice {

  public TcpDevice(Channel channel) {
    super(channel);
  }

  public TcpDevice(String id, Channel channel) {
    super(id, channel);
  }

  /**
   * 发送数据
   *
   * @param data 数据
   * @return 返回 ChannelFuture
   */
  @Override
  public ChannelFuture send(ByteBuf data) {
    return channel().writeAndFlush(data);
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }
}
