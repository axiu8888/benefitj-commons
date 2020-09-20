package com.benefitj.examples.server;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.EventLoop;
import com.benefitj.netty.server.udpclient.UdpDeviceClient;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 采集器设备客户端
 */
public class CollectorDeviceClient extends UdpDeviceClient {

  private static final Logger log = LoggerFactory.getLogger(CollectorDeviceClient.class.getSimpleName());

  private final AtomicInteger packageSn = new AtomicInteger();
  private ScheduledFuture<?> timer;
  /**
   * 设备号
   */
  private int deviceSn;

  public CollectorDeviceClient(String id, Channel channel) {
    super(id, channel);
  }

  public int getDeviceSn() {
    return deviceSn;
  }

  public void setDeviceSn(int deviceSn) {
    this.deviceSn = deviceSn;
  }

  public boolean refresh(int sn) {
    if (packageSn.compareAndSet(sn - 1, sn)) {
      return true;
    }
    if (packageSn.get() < sn) {
      packageSn.set(sn);
      return true;
    }
    return false;
  }

  @Override
  public boolean equals(Object o) {
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return getClass().getSimpleName()
        + "("
        + getId() + "#"
        + getRemoteAddress() + "#"
        + packageSn.get() + "#"
        + DateFmtter.fmt(getRecvTime())
        + ")";
  }

  public void startTimer() {
    cancelTimer();
    this.timer = EventLoop.single().schedule(() -> {
      if (DateFmtter.now() - getRecvTime() > 1000) {
        log.info("超过一秒未接收到数据: {}, onlineTime: {}"
            , CollectorDeviceClient.this, DateFmtter.fmt(getOnlineTime()));
      }
    }, 1100, TimeUnit.MILLISECONDS);
  }

  public void stopTimer() {
    cancelTimer();
  }

  private void cancelTimer() {
    ScheduledFuture<?> t = this.timer;
    if (t != null) {
      t.cancel(true);
      this.timer = null;
    }
  }
}
