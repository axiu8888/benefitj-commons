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
  /**
   * 上线时间
   */
  private long onlineTime = System.currentTimeMillis();
  /**
   * 上次接收到数据的时间
   */
  private volatile long recvTime = -1;

  public UdpDevice(Channel channel) {
    super(channel);
  }

  public UdpDevice(String id, Channel channel) {
    super(id, channel);
  }

  public void setOnlineTime(long onlineTime) {
    this.onlineTime = onlineTime;
  }

  public long getOnlineTime() {
    return onlineTime;
  }

  /**
   * 获取接收数据包的时间
   */
  public long getRecvTime() {
    return recvTime;
  }

  /**
   * 设置接收数据包的时间
   *
   * @param recvTime 时间
   */
  public void setRecvTime(long recvTime) {
    this.recvTime = recvTime;
  }

  /**
   * 设置当前时间为最新的接收数据包的时间
   */
  public void setRecvTimeNow() {
    this.setRecvTime(System.currentTimeMillis());
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
