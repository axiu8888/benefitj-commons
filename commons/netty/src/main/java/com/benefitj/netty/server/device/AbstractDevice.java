package com.benefitj.netty.server.device;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoop;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的设备基类
 */
public abstract class AbstractDevice implements Device {

  private static InetSocketAddress ofAddr(SocketAddress addr) {
    return (InetSocketAddress) addr;
  }

  /**
   * 设备ID
   */
  private String id;
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
  /**
   * 属性
   */
  private Map<String, Object> attributes = new ConcurrentHashMap<>();
  /**
   * 上线时间
   */
  private long onlineTime;

  public AbstractDevice(Channel channel) {
    this(null, channel);
  }

  public AbstractDevice(String id, Channel channel) {
    this(id, channel, ofAddr(channel.localAddress()), ofAddr(channel.remoteAddress()));
  }

  public AbstractDevice(String id, Channel channel, InetSocketAddress remoteAddr) {
    this(id, channel, null, remoteAddr);
    if (channel != null) {
      this.setLocalAddress(ofAddr(channel.localAddress()));
    }
  }

  public AbstractDevice(String id, Channel channel, InetSocketAddress localAddr, InetSocketAddress remoteAddr) {
    this.id = id;
    this.channel = channel;
    this.setLocalAddress(localAddr);
    this.setRemoteAddress(remoteAddr);
    // 在线时间
    this.setOnlineTime(System.currentTimeMillis());
  }

  protected AbstractDevice self() {
    return this;
  }

  /**
   * 获取设备ID
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * 设置设备ID
   *
   * @param id ID
   * @return 返回设备对象
   */
  @Override
  public Device setId(String id) {
    this.id = id;
    return self();
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
   * @return 返回设备对象
   */
  @Override
  public Device setLocalAddress(InetSocketAddress localAddr) {
    this.localAddress = localAddr;
    return self();
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
   * @return 返回设备对象
   */
  @Override
  public Device setRemoteAddress(InetSocketAddress remoteAddr) {
    this.remoteAddress = remoteAddr;
    return self();
  }

  /**
   * 设置在线时间
   *
   * @param onlineTime 在线时间
   */
  @Override
  public Device setOnlineTime(long onlineTime) {
    this.onlineTime = onlineTime;
    return self();
  }

  /**
   * 获取在线时间
   */
  @Override
  public long getOnlineTime() {
    return onlineTime;
  }

  /**
   * 通道
   */
  @Override
  public Channel channel() {
    return channel;
  }

  /**
   * 是否为active
   */
  @Override
  public boolean isActive() {
    Channel ch = channel();
    return ch != null && ch.isActive();
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */
  @Override
  public ChannelFuture send(ByteBuf msg) {
    return channel().writeAndFlush(msg);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回 ChannelFuture
   */

  @Override
  public ChannelFuture send(byte[] msg) {
    return send(Unpooled.wrappedBuffer(msg));
  }

  /**
   * 获取Channel的EventLoop
   */
  @Override
  public EventLoop eventLoop() {
    return channel().eventLoop();
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

  @Override
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return eventLoop().schedule(command, delay, unit);
  }

  /**
   * 属性集合
   */
  @Override
  public Map<String, Object> attrs() {
    return attributes;
  }

  /**
   * 设置属性值
   *
   * @param key   属性键
   * @param value 属性值
   */
  @Override
  public void setAttr(String key, Object value) {
    attrs().put(key, value);
  }

  /**
   * 获取属性值
   *
   * @param key 属性键
   * @return 返回获取的属性值
   */
  @Override
  public <T> T getAttr(String key) {
    return (T) attrs().get(key);
  }

  /**
   * 移除属性值
   *
   * @param key 属性键
   * @return 返回被移除的属性值，如果没有，返回 NULL
   */
  @Override
  public <T> T removeAttr(String key) {
    return (T) attrs().remove(key);
  }

  /**
   * 清空所有属性值
   */
  @Override
  public void clearAttrs() {
    attrs().clear();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractDevice that = (AbstractDevice) o;
    return Objects.equals(getId(), that.getId())
        && Objects.equals(channel(), that.channel())
        && Objects.equals(getRemoteAddress(), that.getRemoteAddress());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getRemoteAddress());
  }

  @Override
  public String toString() {
    return new StringBuilder()
        .append(getClass().getSimpleName())
        .append("(")
        .append(getId())
        .append("#").append(getRemoteAddress())
        .append(")")
        .toString();
  }

}
