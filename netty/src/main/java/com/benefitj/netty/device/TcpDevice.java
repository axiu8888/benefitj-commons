package com.benefitj.netty.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;

/**
 * TCP 设备
 */
public class TcpDevice extends AbstractNettyDevice {

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

  @Override
  public void send(byte[] msg, ChannelFutureListener... listeners) {
    send(Unpooled.wrappedBuffer(msg), listeners);
  }

  @Override
  public void send(ByteBuf msg, ChannelFutureListener... listeners) {
    getChannel().writeAndFlush(msg).addListeners(listeners);
  }

}
