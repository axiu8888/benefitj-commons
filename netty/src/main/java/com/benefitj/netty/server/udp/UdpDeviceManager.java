package com.benefitj.netty.server.udp;

import com.benefitj.netty.server.device.DeviceManager;
import com.benefitj.netty.server.device.UdpDevice;

import java.util.concurrent.TimeUnit;

/**
 * UDP客户端管理类
 */
public interface UdpDeviceManager<C extends UdpDevice> extends DeviceManager<C> {

  /**
   * 使客户端过期
   *
   * @param id 客户端ID
   */
  void expire(String id);

  /**
   * 获取过期检查实现
   */
  ExpireChecker<C> getExpireChecker();

  /**
   * 设置过期检查的实现
   *
   * @param checker 过期检查对象
   */
  void setExpireChecker(ExpireChecker<C> checker);

  /**
   * 获取过期时长
   */
  long getExpire();

  /**
   * 设置过期时长， 小于等于0表示不过期
   *
   * @param expire 时长
   */
  void setExpire(long expire);

  /**
   * 获取检查间隔
   */
  long getDelay();

  /**
   * 设置检查过期客户端的间隔时长
   *
   * @param delay 间隔时长
   */
  void setDelay(long delay);

  /**
   * 获取延迟间隔时长的单位
   */
  TimeUnit getDelayUnit();

  /**
   * 设置延迟间隔时长单位
   *
   * @param delayUnit 时长单位
   */
  void setDelayUnit(TimeUnit delayUnit);

  /**
   * 检查过期客户端
   */
  default void autoCheckExpire() {
    getExpireChecker().check(this);
  }

}
