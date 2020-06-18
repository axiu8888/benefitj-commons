package com.benefitj.netty.server.udp;

import java.net.InetSocketAddress;
import java.util.Objects;

/**
 * UDP客户端
 */
public class UdpClient {
  /**
   * 客户端ID
   */
  private final String id;
  /**
   * 远程地址
   */
  private InetSocketAddress remoteAddress;
  /**
   * 上次接收到数据的时间
   */
  private volatile long lastRecvTime = -1;
  /**
   * 上线时间
   */
  private long onlineTime = now();

  public UdpClient(String id) {
    this.id = id;
  }

  public UdpClient(String id, InetSocketAddress remoteAddress) {
    this(id);
    this.remoteAddress = remoteAddress;
  }

  public void setOnlineTime(long onlineTime) {
    this.onlineTime = onlineTime;
  }

  public long getOnlineTime() {
    return onlineTime;
  }

  public String getId() {
    return id;
  }

  public long getLastRecvTime() {
    return lastRecvTime;
  }

  public void setLastRecvTime(long lastRecvTime) {
    this.lastRecvTime = lastRecvTime;
  }

  public InetSocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  public void setRemoteAddress(InetSocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
  }

  /**
   * 判断是否接收数据超时
   *
   * @param interval 允许的间隔时间
   * @return 返回是否接收超时
   */
  public boolean isRecvTimeout(long interval) {
    return now() - getLastRecvTime() > interval;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UdpClient that = (UdpClient) o;
    return Objects.equals(id, that.id) && Objects.equals(remoteAddress, that.remoteAddress);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, remoteAddress);
  }

  @Override
  public String toString() {
    return "UdpClient{" +
        "deviceId='" + id + '\'' +
        ", lastRecvTime=" + lastRecvTime +
        ", remoteAddress=" + remoteAddress +
        '}';
  }


  private static long now() {
    return System.currentTimeMillis();
  }
}
