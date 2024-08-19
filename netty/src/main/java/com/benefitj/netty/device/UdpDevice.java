package com.benefitj.netty.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * UDP 设备
 */
public class UdpDevice extends AbstractNettyDevice {

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

  @Override
  public void send(byte[] msg, ChannelFutureListener... listeners) {
    send(msg, getRemoteAddress(), listeners);
  }

  @Override
  public void send(ByteBuf msg, ChannelFutureListener... listeners) {
    send(msg, getRemoteAddress(), listeners);
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   */
  public void send(byte[] msg, InetSocketAddress remote, ChannelFutureListener... listeners) {
    send(Unpooled.wrappedBuffer(msg), remote, listeners);
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   */
  public void send(ByteBuf msg, InetSocketAddress remote, ChannelFutureListener... listeners) {
    if (remote == null) throw new IllegalArgumentException("msg的远程地址不能为空!");
    send(new DatagramPacket(msg, remote), listeners);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   */
  public void send(DatagramPacket msg, ChannelFutureListener... listeners) {
    sendAny(msg, listeners);
  }

}
