package com.benefitj.netty.device;

import com.benefitj.core.device.SimpleDevice;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoop;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的设备基类
 */
public abstract class AbstractNettyDevice extends SimpleDevice implements NettyDevice {

  /**
   * 本地地址
   */
  private InetSocketAddress localAddress;
  /**
   * 远程地址
   */
  private InetSocketAddress remoteAddress;
  /**
   * 通道
   */
  private Channel channel;

  public AbstractNettyDevice(String id) {
    super(id);
  }

  public AbstractNettyDevice(Channel channel) {
    this(null, channel);
  }

  public AbstractNettyDevice(String id, Channel channel) {
    this(id, channel, ofLocal(channel), ofRemote(channel));
  }

  public AbstractNettyDevice(String id, Channel channel, InetSocketAddress remoteAddr) {
    this(id, channel, ofLocal(channel), remoteAddr);
  }

  public AbstractNettyDevice(String id, Channel channel, InetSocketAddress localAddr, InetSocketAddress remoteAddr) {
    this(id);
    this.channel = channel;
    this.setLocalAddress(localAddr);
    this.setRemoteAddress(remoteAddr);
    // 在线时间
    this.setOnlineTime(System.currentTimeMillis());
    this.setActiveAt(System.currentTimeMillis());
  }

  protected AbstractNettyDevice self() {
    return this;
  }

  /**
   * 获取设备的本地地址
   */
  @Override
  public InetSocketAddress getLocalAddress() {
    return localAddress;
  }

  /**
   * 设置设备的本地地址
   *
   * @param localAddr 本地地址
   */
  @Override
  public void setLocalAddress(InetSocketAddress localAddr) {
    this.localAddress = localAddr;
  }

  /**
   * 获取设备的远程地址
   */
  @Override
  public InetSocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  /**
   * 设置设备的远程地址
   *
   * @param remoteAddr 远程地址
   */
  @Override
  public void setRemoteAddress(InetSocketAddress remoteAddr) {
    this.remoteAddress = remoteAddr;
  }

  /**
   * 通道
   */
  @Override
  public Channel getChannel() {
    return channel;
  }

  /**
   * 设置通道
   *
   * @param channel 通道
   * @return 返回当前设备
   */
  @Override
  public NettyDevice setChannel(Channel channel) {
    this.channel = channel;
    return self();
  }

  /**
   * 是否为active
   */
  @Override
  public boolean isActive() {
    Channel ch = getChannel();
    return ch != null && ch.isActive();
  }

  /**
   * 获取pipeline
   */
  @Override
  public ChannelPipeline pipeline() {
    return getChannel().pipeline();
  }

  /**
   * 往pipeline中发送消息
   *
   * @param msg 消息
   */
  @Override
  public NettyDevice fireRead(Object msg) {
    pipeline().fireChannelRead(msg);
    return self();
  }

  /**
   * 获取Channel的EventLoop
   */
  @Override
  public EventLoop eventLoop() {
    return getChannel().eventLoop();
  }

  /**
   * 是否在EventLoop中
   */
  @Override
  public boolean inEventLoop() {
    return eventLoop().inEventLoop();
  }

  /**
   * 执行调度任务
   *
   * @param task 任务
   */
  @Override
  public void execute(Runnable task) {
    eventLoop().execute(task);
  }

  /**
   * 执行调度任务
   *
   * @param command 任务
   * @param delay   延迟时间
   * @param unit    时间单位
   * @return 返回 ScheduledFuture
   */
  @Override
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return eventLoop().schedule(command, delay, unit);
  }

  /**
   * 执行调度任务
   *
   * @param command      任务
   * @param initialDelay 延迟时间
   * @param period       间隔
   * @param unit         时间单位
   * @return 返回 ScheduledFuture
   */
  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return eventLoop().scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  /**
   * 关闭通道
   */
  @Override
  public ChannelFuture closeChannel() {
    Channel ch = getChannel();
    return ch != null ? ch.close() : null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractNettyDevice that = (AbstractNettyDevice) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(getChannel(), that.getChannel())
        && Objects.equals(getRemoteAddress(), that.getRemoteAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getRemoteAddress());
  }

  @Override
  public String toString() {
    InetSocketAddress remote = getRemoteAddress();
    return new StringBuilder()
        .append(getClass().getSimpleName())
        .append("(")
        .append(getId())
        .append("#").append(remote.getHostString()).append(":").append(remote.getPort())
        .append("#online[").append(String.format("%.1fs", getDuration(getOnlineTime(), TimeUnit.SECONDS))).append("]")
        .append("#active[").append(String.format("%.1fs", getDuration(getActiveAt(), TimeUnit.SECONDS))).append("]")
        .append(")")
        .toString();
  }


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

  protected static float getDuration(long time, TimeUnit unit) {
    long duration = System.currentTimeMillis() - time;
    switch (unit) {
      case SECONDS:
        return duration / 1000.0f;
      case MINUTES:
        return duration / 60_000.f;
      case HOURS:
        return duration / 3600_000.f;
      case DAYS:
        return duration / 24.f / 3600_000.f;
      default:
        return duration;
    }
  }
}
