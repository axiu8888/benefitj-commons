package com.benefitj.netty.server.udpdevice;

import com.benefitj.netty.server.device.DefaultDeviceManager;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * UDP设备客户端管理
 */
public class DefaultUdpDeviceClientManager<C extends UdpDeviceClient> extends DefaultDeviceManager<C> implements UdpDeviceClientManager<C> {

  /**
   * 延期检查的实现
   */
  private ExpireChecker<C> expireChecker = ExpireChecker.newInstance();
  /**
   * 移除监听
   */
  private ClientStateChangeListener<C> listener = ClientStateChangeListener.emptyListener();
  /**
   * 过期时间
   */
  private long expired = 5000;
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

  public DefaultUdpDeviceClientManager(ClientStateChangeListener<C> listener) {
    this.listener = listener;
  }

  protected C addNewClient(String key, C value) {
    C old = getDevices().put(key, value);
    getStateChangeListener().onAddition(key, value, old);
    return old;
  }

  @Override
  public void expire(String id) {
    C c = remove(id);
    if (c != null) {
      getStateChangeListener().onRemoval(id, c);
    }
  }

  @Override
  public ClientStateChangeListener<C> getStateChangeListener() {
    return listener;
  }

  @Override
  public void setStateChangeListener(ClientStateChangeListener<C> listener) {
    this.listener = (listener != null ? listener : ClientStateChangeListener.emptyListener());
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
  public long getExpired() {
    return expired;
  }

  @Override
  public void setExpired(long expired) {
    this.expired = expired;
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

  @Override
  public C put(String key, C value) {
    return addNewClient(key, value);
  }

  @Override
  public void putAll(Map<? extends String, ? extends C> m) {
    for (Entry<? extends String, ? extends C> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

}
