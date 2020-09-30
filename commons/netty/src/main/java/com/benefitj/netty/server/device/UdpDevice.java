package com.benefitj.netty.server.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * UDP 设备
 */
public class UdpDevice extends AbstractDevice {

  public UdpDevice(Channel channel) {
    super(channel);
  }

  public UdpDevice(String id, Channel channel) {
    super(id, channel);
  }

  public UdpDevice(String id, Channel channel, InetSocketAddress remoteAddr) {
    super(id, channel, remoteAddr);
  }

  public UdpDevice(String id, Channel channel, InetSocketAddress localAddr, InetSocketAddress remoteAddr) {
    super(id, channel, localAddr, remoteAddr);
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   * @return 返回 ChannelFuture
   */
  public ChannelFuture send(byte[] msg, InetSocketAddress remote) {
    return send(Unpooled.wrappedBuffer(msg), remote);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */
  @Override
  public ChannelFuture send(ByteBuf msg) {
    return send(msg, getRemoteAddress());
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   * @return 返回 ChannelFuture
   */
  public ChannelFuture send(ByteBuf msg, InetSocketAddress remote) {
    return send(new DatagramPacket(msg, remote));
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */
  public ChannelFuture send(DatagramPacket msg) {
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
