package com.benefitj.netty.server.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 设备
 */
public interface Device {

  /**
   * 获取设备ID
   */
  String getId();

  /**
   * 设置设备ID
   *
   * @param id ID
   * @return 返回设备对象
   */
  Device setId(String id);

  /**
   * 获取设备的本地地址
   */
  InetSocketAddress getLocalAddress();

  /**
   * 设置设备的本地地址
   *
   * @param localAddr 本地地址
   * @return 返回设备对象
   */
  Device setLocalAddress(InetSocketAddress localAddr);

  /**
   * 获取设备的远程地址
   */
  InetSocketAddress getRemoteAddress();

  /**
   * 设置设备的远程地址
   *
   * @param remoteAddr 远程地址
   * @return 返回设备对象
   */
  Device setRemoteAddress(InetSocketAddress remoteAddr);

  /**
   * 通道
   */
  Channel channel();

  /**
   * 发送数据
   *
   * @param data 数据
   * @return 返回 ChannelFuture
   */
  ChannelFuture send(ByteBuf data);

  /**
   * 发送数据
   *
   * @param data 数据
   * @return 返回 ChannelFuture
   */
  default ChannelFuture send(byte[] data) {
    return send(Unpooled.wrappedBuffer(data));
  }

  /**
   * 获取Channel的EventLoop
   */
  EventLoop eventLoop();

  /**
   * 执行调度任务
   *
   * @param task 任务
   */
  void execute(Runnable task);

  /**
   * 执行调度任务
   *
   * @param command 任务
   * @param delay   延迟时间
   * @param unit    时间单位
   * @return 返回 ScheduledFuture
   */
  ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit);

}
