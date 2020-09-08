package com.benefitj.netty.adapter;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

/**
 * UDP数据包处理程序
 */
public interface DatagramPacketAdapter {

  /**
   * 是否支持
   *
   * @param data 数据
   * @return 返回是否支持
   */
  boolean support(byte[] data);

  /**
   * 解析
   *
   * @param ctx    上下文
   * @param packet UDP数据包
   * @param data   原始数据
   */
  void process(ChannelHandlerContext ctx, DatagramPacket packet, byte[] data);

}
