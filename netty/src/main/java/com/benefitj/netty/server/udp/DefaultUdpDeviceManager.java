package com.benefitj.netty.server.udp;

import com.benefitj.netty.server.device.DefaultDeviceManager;
import com.benefitj.netty.server.device.DeviceStateListener;
import com.benefitj.netty.server.device.UdpDevice;

import java.util.concurrent.TimeUnit;

/**
 * UDP设备客户端管理
 */
public class DefaultUdpDeviceManager<C extends UdpDevice> extends DefaultDeviceManager<C>
    implements UdpDeviceManager<C> {

  /**
   * 创建UDP设备客户端管理
   *
   * @param <C> UDP设备的客户端
   * @return 返回UDP设备客户端管理对象
   */
  public static <C extends UdpDevice> UdpDeviceManager<C> newInstance() {
    return new DefaultUdpDeviceManager<>();
  }

  /**
   * 延期检查的实现
   */
  private ExpireChecker<C> expireChecker = ExpireChecker.newInstance();
  /**
   * 过期时间
   */
  private long expire = 5000;
  /**
   * 延迟检查的间隔
   */
  private long delay = 1000;
  /**
   * 延迟时间单位类型
   */
  private TimeUnit delayUnit = TimeUnit.MILLISECONDS;

  public DefaultUdpDeviceManager() {
  }

  public DefaultUdpDeviceManager(DeviceStateListener<C> listener) {
    super(listener);
  }

  @Override
  public void expire(String id) {
    super.remove(id, true);
  }

  @Override
  public ExpireChecker<C> getExpireChecker() {
    return expireChecker;
  }

  @Override
  public void setExpireChecker(ExpireChecker<C> checker) {
    if (checker == null) {
      throw new IllegalArgumentException("checker must not null");
    }
    this.expireChecker = checker;
  }

  @Override
  public long getExpire() {
    return expire;
  }

  @Override
  public void setExpire(long expire) {
    this.expire = expire;
  }

  @Override
  public long getDelay() {
    return delay;
  }

  @Override
  public void setDelay(long delay) {
    this.delay = delay;
  }

  @Override
  public TimeUnit getDelayUnit() {
    return delayUnit;
  }

  @Override
  public void setDelayUnit(TimeUnit delayUnit) {
    this.delayUnit = delayUnit;
  }

}
