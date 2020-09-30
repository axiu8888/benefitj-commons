package com.benefitj.netty.server.device;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;

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

  public TcpDevice(String id, Channel channel, InetSocketAddress remoteAddr) {
    super(id, channel, remoteAddr);
  }

  public TcpDevice(String id, Channel channel, InetSocketAddress localAddr, InetSocketAddress remoteAddr) {
    super(id, channel, localAddr, remoteAddr);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */
  @Override
  public ChannelFuture send(ByteBuf msg) {
    return channel().writeAndFlush(msg);
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
