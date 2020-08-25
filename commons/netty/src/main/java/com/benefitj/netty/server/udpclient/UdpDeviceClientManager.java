package com.benefitj.netty.server.udpclient;

import com.benefitj.netty.server.device.DefaultDeviceManager;
import com.benefitj.netty.server.device.DeviceManager;
import com.benefitj.netty.server.device.DeviceStateChangeListener;

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


  /**
   * 创建UDP设备客户端管理
   *
   * @param <C> UDP设备的客户端
   * @return 返回UDP设备客户端管理对象
   */
  static <C extends UdpDeviceClient> UdpDeviceClientManager<C> newInstance() {
    return new DefaultUdpDeviceClientManager<C>();
  }

  /**
   * UDP设备客户端管理
   */
  static class DefaultUdpDeviceClientManager<C extends UdpDeviceClient>
      extends DefaultDeviceManager<C> implements UdpDeviceClientManager<C> {

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

    public DefaultUdpDeviceClientManager() {
    }

    public DefaultUdpDeviceClientManager(DeviceStateChangeListener<C> listener) {
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

}
