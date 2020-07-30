package com.benefitj.netty.server.udp;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 客户端管理
 */
public class DefaultUdpClientManager<C extends UdpClient> implements UdpClientManager<C> {

  /**
   * 延期检查的实现
   */
  private ExpiredChecker<C> expiredChecker = DefaultExpiredChecker.newInstance();
  /**
   * 移除监听
   */
  private ClientStateChangeListener<C> listener = ClientStateChangeListener.emptyListener() ;
  /**
   * 客户端
   */
  private final Map<String, C> clients = new ConcurrentHashMap<>();
  /**
   * 过期时间
   */
  private long expired = 5000;
  /**
   * 延迟检查的间隔
   */
  private long interval = 500;
  /**
   * 延迟时间单位类型
   */
  private TimeUnit intervalUnit = TimeUnit.MILLISECONDS;

  public DefaultUdpClientManager() {
  }

  public DefaultUdpClientManager(ClientStateChangeListener<C> listener) {
    this.listener = listener;
  }

  public Map<String, C> getClients() {
    return clients;
  }

  protected C addNewClient(String key, C value) {
    C oldClient = getClients().put(key, value);
    getStateChangeListener().onAddition(key, value, oldClient);
    return oldClient;
  }

  @Override
  public void expiredClient(String id) {
    C client = remove(id);
    if (client != null) {
      getStateChangeListener().onRemoval(id, client);
    }
  }

  @Override
  public ClientStateChangeListener<C> getStateChangeListener() {
    return listener;
  }

  @Override
  public void setStateChangeListener(ClientStateChangeListener<C> listener) {
    this.listener = (listener != null ? listener : ClientStateChangeListener.emptyListener() );
  }

  @Override
  public ExpiredChecker<C> getExpiredChecker() {
    return expiredChecker;
  }

  @Override
  public void setExpiredChecker(ExpiredChecker<C> checker) {
    if (checker == null) {
      throw new IllegalArgumentException("checker must not null");
    }
    this.expiredChecker = checker;
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
  public long getInterval() {
    return interval;
  }

  @Override
  public void setInterval(long interval) {
    this.interval = interval;
  }

  @Override
  public TimeUnit getIntervalUnit() {
    return intervalUnit;
  }

  @Override
  public void setIntervalUnit(TimeUnit intervalUnit) {
    this.intervalUnit = intervalUnit;
  }

  @Override
  public int size() {
    return getClients().size();
  }

  @Override
  public boolean isEmpty() {
    return getClients().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getClients().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getClients().containsValue(value);
  }

  @Override
  public C get(Object key) {
    return getClients().get(key);
  }

  @Override
  public C put(String key, C value) {
    return addNewClient(key, value);
  }

  @Override
  public C remove(Object key) {
    return getClients().remove(key);
  }

  @Override
  public void putAll(Map<? extends String, ? extends C> m) {
    for (Entry<? extends String, ? extends C> entry : m.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  @Override
  public void clear() {
    getClients().clear();
  }

  @Override
  public Set<String> keySet() {
    return getClients().keySet();
  }

  @Override
  public Collection<C> values() {
    return getClients().values();
  }

  @Override
  public Set<Entry<String, C>> entrySet() {
    return getClients().entrySet();
  }


}
