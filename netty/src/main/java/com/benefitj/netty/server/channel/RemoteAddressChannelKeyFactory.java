package com.benefitj.netty.server.channel;

import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;

/**
 * 远程地址作为key
 */
public class RemoteAddressChannelKeyFactory implements ChannelKeyFactory {

  /**
   * 获取 ChannelKey
   *
   * @param packet 数据包
   * @return 返回 Key
   */
  @Override
  public InetSocketAddress getChannelKey(DatagramPacket packet) {
    return packet.sender();
  }

}
