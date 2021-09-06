package com.benefitj.netty.server.device;

import com.benefitj.device.Device;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 设备
 */
public interface NettyDevice extends Device {

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
  void setLocalAddress(InetSocketAddress localAddr);

  /**
   * 获取设备的远程地址
   */
  InetSocketAddress getRemoteAddress();

  /**
   * 设置设备的远程地址
   *
   * @param remoteAddr 远程地址
   */
  void setRemoteAddress(InetSocketAddress remoteAddr);

  /**
   * 设置当前时间为最新的接收数据包的时间
   *
   * @return 返回设备对象
   */
  void setActiveTimeNow();

  /**
   * 通道
   */
  Channel channel();

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */
  ChannelFuture send(ByteBuf msg);

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */
  ChannelFuture send(byte[] msg);

  /**
   * 获取pipeline
   */
  ChannelPipeline pipeline();

  /**
   * 往pipeline中发送消息
   *
   * @param msg 消息
   */
  NettyDevice fireRead(Object msg);

  /**
   * 获取Channel的EventLoop
   */
  EventLoop eventLoop();

  /**
   * 是否在EventLoop中
   */
  boolean inEventLoop();

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

  /**
   * 执行调度任务
   *
   * @param command 任务
   * @param initialDelay   延迟时间
   * @param period  间隔
   * @param unit    时间单位
   * @return 返回 ScheduledFuture
   */
  ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

  /**
   * 关闭通道
   */
  ChannelFuture closeChannel();

}
