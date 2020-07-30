package com.benefitj.netty.server.udp;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 远程客户端管理类
 */
public interface UdpClientManager<C extends UdpClient> extends Map<String, C> {

  /**
   * 使客户端过期
   *
   * @param id 客户端ID
   */
  void expiredClient(String id);

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
  ExpiredChecker<C> getExpiredChecker();

  /**
   * 设置过期检查的实现
   *
   * @param checker 过期检查对象
   */
  void setExpiredChecker(ExpiredChecker<C> checker);

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
  long getInterval();

  /**
   * 设置检查过期客户端的间隔时长
   *
   * @param interval 间隔时长
   */
  void setInterval(long interval);

  /**
   * 获取间隔时长的单位
   */
  TimeUnit getIntervalUnit();

  /**
   * 设置间隔时长单位
   *
   * @param intervalUnit 时长单位
   */
  void setIntervalUnit(TimeUnit intervalUnit);

  /**
   * 检查过期客户端
   */
  default void checkExpiredClients() {
    getExpiredChecker().check(this);
  }

}
