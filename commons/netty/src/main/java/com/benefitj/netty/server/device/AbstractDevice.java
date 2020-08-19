package com.benefitj.netty.server.device;

import com.benefitj.netty.server.udpdevice.UdpDeviceClient;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.Objects;

public abstract class AbstractDevice implements Device {

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

  public AbstractDevice(Channel channel) {
    this(null, channel);
  }

  public AbstractDevice(String id, Channel channel) {
    this.id = id;
    this.channel = channel;

    if (channel != null) {
      this.setLocalAddress((InetSocketAddress) channel.localAddress());
      this.setRemoteAddress((InetSocketAddress) channel.remoteAddress());
    }
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
   * 通道
   */
  @Override
  public Channel channel() {
    return channel;
  }

  /**
   * 发送数据
   *
   * @param data 数据
   * @return 返回 ChannelFuture
   */
  @Override
  public abstract ChannelFuture send(ByteBuf data);

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UdpDeviceClient that = (UdpDeviceClient) o;
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
    return getClass().getSimpleName() + "(" + getId() + "#" + getRemoteAddress() + ")";
  }

}
