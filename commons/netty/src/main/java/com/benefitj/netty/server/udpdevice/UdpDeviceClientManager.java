package com.benefitj.netty.server.udpdevice;

import com.benefitj.netty.server.device.DeviceManager;

import java.util.concurrent.TimeUnit;

/**
 * UDP客户端管理类
 */
public interface UdpDeviceClientManager<C extends UdpDeviceClient> extends DeviceManager<C> {

  /**
   * 使客户端过期
   *
   * @param id 客户端ID
   */
  void expire(String id);

  /**
   * 获取客户端状态监听
   */
  ClientStateChangeListener<C> getStateChangeListener();

  /**
   * 客户端状态监听
   *
   * @param listener 监听
   */
  void setStateChangeListener(ClientStateChangeListener<C> listener);

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
  long getExpired();

  /**
   * 设置过期时长， -1 表示不过期
   *
   * @param expired 时长
   */
  void setExpired(long expired);

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
