package com.benefitj.netty.server.udpclient;

import com.benefitj.netty.server.device.UdpDevice;
import io.netty.channel.Channel;

/**
 * UDP设备的客户端
 */
public class UdpDeviceClient extends UdpDevice {
  /**
   * 上线时间
   */
  private long onlineTime = now();
  /**
   * 上次接收到数据的时间
   */
  private volatile long recvTime = -1;

  public UdpDeviceClient(Channel channel) {
    super(channel);
  }

  public UdpDeviceClient(String id, Channel channel) {
    super(id, channel);
  }

  public void setOnlineTime(long onlineTime) {
    this.onlineTime = onlineTime;
  }

  public long getOnlineTime() {
    return onlineTime;
  }

  /**
   * 获取接收数据包的时间
   */
  public long getRecvTime() {
    return recvTime;
  }

  /**
   * 设置接收数据包的时间
   *
   * @param recvTime 时间
   */
  public void setRecvTime(long recvTime) {
    this.recvTime = recvTime;
  }

  /**
   * 设置当前时间为最新的接收数据包的时间
   */
  public void resetRecvTimeNow() {
    this.setRecvTime(now());
  }

  static long now() {
    return System.currentTimeMillis();
  }
}
