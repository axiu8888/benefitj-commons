package com.benefitj.netty.server.channel;

import io.netty.channel.socket.DatagramPacket;

import java.io.Serializable;

public interface ChannelKeyFactory {

  /**
   * 获取ChannelKey
   *
   * @param packet 数据包
   * @return 返回 ChannelKey
   */
  Serializable getChannelKey(DatagramPacket packet);

}
