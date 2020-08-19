package com.benefitj.netty.server.udpdevice;

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
  private volatile long lastRecvTime = -1;

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
   * 最新接收数据包的时间
   */
  public long getLastRecvTime() {
    return lastRecvTime;
  }

  public void setLastRecvTime(long lastRecvTime) {
    this.lastRecvTime = lastRecvTime;
  }

  /**
   * 设置当前时间为最新的接收数据包的时间
   */
  public void resetLastRecvTimeNow() {
    this.setLastRecvTime(now());
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


  static long now() {
    return System.currentTimeMillis();
  }
}
