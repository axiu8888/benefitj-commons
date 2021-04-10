package com.benefitj.netty.server.device;

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
   * 设置在线时间
   *
   * @param onlineTime 在线时间
   * @return 返回设备对象
   */
  Device setOnlineTime(long onlineTime);

  /**
   * 获取在线时间
   */
  long getOnlineTime();

  /**
   * 获取接收数据包的时间
   */
  long getRcvTime();

  /**
   * 设置接收数据包的时间
   *
   * @param rcvTime 时间
   * @return 返回设备对象
   */
  Device setRcvTime(long rcvTime);

  /**
   * 设置当前时间为最新的接收数据包的时间
   *
   * @return 返回设备对象
   */
  Device setRecvTimeNow();

  /**
   * 通道
   */
  Channel channel();

  /**
   * 是否为active
   */
  boolean isActive();

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
  Device fireRead(Object msg);

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
   * 属性集合
   */
  Map<String, Object> attrs();

  /**
   * 属性数量
   */
  int attributeSize();

  /**
   * 是否有某个属性
   *
   * @param key 属性键
   * @return 返回判断结果
   */
  boolean hasAttr(String key);

  /**
   * 设置属性值
   *
   * @param key   属性键
   * @param value 属性值
   */
  void setAttr(String key, Object value);

  /**
   * 获取属性值
   *
   * @param key 属性键
   * @param <T> 属性值类型
   * @return 返回获取的属性值
   */
  <T> T getAttr(String key);

  /**
   * 移除属性值
   *
   * @param key 属性键
   * @param <T> 属性值类型
   * @return 返回被移除的属性值，如果没有，返回 NULL
   */
  <T> T removeAttr(String key);

  /**
   * 清空所有属性值
   */
  void clearAttrs();

  /**
   * 关闭通道
   */
  ChannelFuture closeChannel();

}
