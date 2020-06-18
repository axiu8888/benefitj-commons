package com.benefitj.netty.server.udp;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 采集器客户端管理
 */
public class SimpleUdpClientManager<C extends UdpClient> implements UdpClientManager<C> {

  /**
   * 默认过期检查的实现
   */
  public static final ExpiredChecker DEFAULT_CHECKER = new SimpleExpiredChecker();
  /**
   * 客户端监听
   */
  public static final ClientStateChangeListener DEFAULT_LISTENER = new ClientStateChangeListener() {/* ignore */};

  /**
   * 延期检查的实现
   */
  private ExpiredChecker<C> expiredChecker = DEFAULT_CHECKER;
  /**
   * 移除监听
   */
  private ClientStateChangeListener<C> listener = DEFAULT_LISTENER;
  /**
   * 采集器客户端
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

  public SimpleUdpClientManager() {
  }

  public SimpleUdpClientManager(ClientStateChangeListener listener) {
    this.listener = listener;
  }

  public Map<String, C> getClients() {
    return clients;
  }

  protected C addNewClient(String key, C value) {
    C oldClient = getClients().put(key, value);
    getListener().onAddition(key, value, oldClient);
    return oldClient;
  }

  @Override
  public void expiredClient(String id) {
    UdpClient client = remove(id);
    if (client != null) {
      getListener().onRemoval(id, client);
    }
  }

  @Override
  public ClientStateChangeListener getListener() {
    return listener;
  }

  @Override
  public void setListener(ClientStateChangeListener listener) {
    this.listener = (listener != null ? listener : DEFAULT_LISTENER);
  }

  @Override
  public ExpiredChecker<C> getExpiredChecker() {
    return expiredChecker;
  }

  @Override
  public void setExpiredChecker(ExpiredChecker<C> checker) {
    this.expiredChecker = (checker != null ? checker : DEFAULT_CHECKER);
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
