package com.benefitj.netty.server.device;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.socket.DatagramPacket;

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

  /**
   * 发送数据
   *
   * @param data 数据
   * @return 返回 ChannelFuture
   */
  @Override
  public ChannelFuture send(ByteBuf data) {
    return channel().writeAndFlush(new DatagramPacket(data, getRemoteAddress()));
  }

}
