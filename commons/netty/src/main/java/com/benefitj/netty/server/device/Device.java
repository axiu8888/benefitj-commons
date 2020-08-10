//package com.benefitj.netty.server.device;
//
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelFuture;
//
//import java.net.InetSocketAddress;
//
///**
// * 设备
// */
//public interface Device {
//
//  /**
//   * 获取设备的唯一ID
//   */
//  String getId();
//
//  /**
//   * 设置设备的唯一ID
//   *
//   * @param id 设备的ID
//   */
//  void setId(String id);
//
//  /**
//   * 获取设备名
//   */
//  String getName();
//
//  /**
//   * 设置设备名
//   *
//   * @param name 设备名
//   */
//  void setName(String name);
//
//  /**
//   * 获取设备的远程地址
//   */
//  InetSocketAddress getRemoteAddress();
//
//  /**
//   * 设置设备的远程地址
//   *
//   * @param remoteAddress 地址
//   */
//  void setRemoteAddress(InetSocketAddress remoteAddress);
//
//  /**
//   * 获取设备通道
//   */
//  Channel getChannel();
//
//  /**
//   * 设置设备的通道
//   *
//   * @param channel 设备的通道
//   */
//  void setChannel(Channel channel);
//
//  /**
//   * 发送消息
//   *
//   * @param msg 消息
//   * @return 返回 ChannelFuture，监听状态
//   */
//  default ChannelFuture send(Object msg) {
//    return getChannel().writeAndFlush(msg);
//  }
//
//}
