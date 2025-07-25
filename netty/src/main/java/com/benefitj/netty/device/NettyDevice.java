package com.benefitj.netty.device;

import com.benefitj.core.device.Device;
import com.benefitj.core.device.MessageSender;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;

import java.net.InetSocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 设备
 */
public interface NettyDevice extends Device<String> {

  /**
   * 获取设备的本地地址
   */
  InetSocketAddress getLocalAddress();

  /**
   * 设置设备的本地地址
   *
   * @param localAddr 本地地址
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
   * 通道
   */
  Channel getChannel();

  /**
   * 通道
   */
  NettyDevice setChannel(Channel ch);

  /**
   * 发送消息
   *
   * @param msg       消息
   * @param listeners 监听
   */
  void send(byte[] msg, ChannelFutureListener... listeners);

  /**
   * 发送消息
   *
   * @param msg       消息
   * @param listeners 监听
   */
  void send(ByteBuf msg, ChannelFutureListener... listeners);

  /**
   * 发送消息
   *
   * @param msg       消息
   * @param listeners 监听
   */
  default void sendAny(Object msg, ChannelFutureListener... listeners) {
    MessageSender sender = getMessageSender();
    if (sender != null) {
      sender.send(this, msg, (result, e) -> {
        if (e != null) {
          e.printStackTrace();
        } else {
          ((ChannelFuture) result).addListeners(listeners);
        }
      });
    } else {
      getChannel().writeAndFlush(msg).addListeners(listeners);
    }
  }

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
   * @param command      任务
   * @param initialDelay 延迟时间
   * @param period       间隔
   * @param unit         时间单位
   * @return 返回 ScheduledFuture
   */
  ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

  /**
   * 关闭通道
   */
  ChannelFuture closeChannel();

  /**
   * 本地地址
   */
  public static InetSocketAddress ofLocal(Channel ch) {
    return ch != null ? (InetSocketAddress) ch.localAddress() : null;
  }

  /**
   * 远程地址
   */
  public static InetSocketAddress ofRemote(Channel ch) {
    return ch != null ? (InetSocketAddress) ch.remoteAddress() : null;
  }

}
