package com.benefitj.core.concurrent;


import com.benefitj.core.EventLoop;
import com.benefitj.core.EventLoop.Single;
import com.benefitj.core.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 绑定到固定的EventLoop上
 */
public class EventLoopBinder<K> {

  private static final boolean DEFAULT_SLOW_MONITOR = Boolean.parseBoolean(
      System.getProperty("eventloop.binder.slow.monitor", "false"));
  private static final long DEFAULT_SLOW_THRESHOLD_MS = Long.getLong("eventloop.binder.slow.thresholdMs", 3000L);

  static final Single SINGLE = EventLoop.newSingle(EventLoop.generateNamePrefix(), true);
  /**
   * 线程ID生成
   */
  final AtomicInteger threadNumber = new AtomicInteger(1);
  /**
   * 全部的线程池
   */
  final Set<Holder> loops = new ConcurrentHashSet<>();
  /**
   * 线程池
   */
  private final Map<K, Holder> bindMap = new ConcurrentHashMap<>();
  /**
   * 线程工厂
   */
  private final AtomicReference<ThreadFactory> threadFactory = new AtomicReference<>();
  /**
   * 线程名前缀
   */
  private String namePrefix;
  /**
   * 线程绑定的最大设备数
   */
  private int maxBindSize;
  /**
   * 线程的缓存数
   */
  private int cacheSize;
  /**
   * 线程超时释放
   */
  private Duration expired;
  /**
   * 本实例是否监控 bind().inLoop 慢任务（各 EventLoopBinder 实例相互独立）
   */
  private volatile boolean slowMonitorEnabled = DEFAULT_SLOW_MONITOR;
  /**
   * 本实例慢任务阈值（毫秒）
   */
  private volatile long slowMonitorThresholdMs = DEFAULT_SLOW_THRESHOLD_MS;

  private AtomicReference<ScheduledFuture<?>> releaseTimer = new AtomicReference<>();

  public EventLoopBinder() {
    this(null, 50, 3, Duration.ofSeconds(120));
  }

  /**
   * 构造函数
   *
   * @param namePrefix  线程名前缀
   * @param maxBindSize 线程绑定的最大设备数
   * @param expired     线程超时释放
   */
  public EventLoopBinder(String namePrefix, int maxBindSize, int cacheSize, Duration expired) {
    this(namePrefix, maxBindSize, cacheSize, expired, DEFAULT_SLOW_MONITOR, DEFAULT_SLOW_THRESHOLD_MS);
  }

  /**
   * 构造函数
   *
   * @param namePrefix  线程名前缀
   * @param maxBindSize 线程绑定的最大设备数
   * @param expired     线程超时释放
   */
  public EventLoopBinder(String namePrefix, int maxBindSize, int cacheSize, Duration expired, boolean slowMonitorEnabled, long slowMonitorThresholdMs) {
    this.namePrefix = namePrefix;
    this.maxBindSize = maxBindSize;
    this.cacheSize = cacheSize;
    this.expired = expired;
    this.slowMonitorEnabled = slowMonitorEnabled;
    this.slowMonitorThresholdMs = slowMonitorThresholdMs;
    // 释放资源
    releaseTimer.set(SINGLE.scheduleAtFixedRate(this::releaseLoop, 10, 10, TimeUnit.SECONDS));
  }

  private void releaseLoop() {
    Set<Holder> set = getLoops();
    if (set.size() <= getCacheSize()) return;
    final long expiredMillis = getExpired().toMillis();
    final List<Holder> removed = new LinkedList<>();
    set.forEach(h -> {
      if (h.size() <= 0 && TimeUtils.diffNow(h.getActiveAt()) >= expiredMillis) {
        removed.add(h);
      }
    });
    if (!removed.isEmpty()) {
      removed.forEach(holder -> {
        set.remove(holder);
        holder.executor.shutdownNow();
      });
    }
  }

  /**
   * 绑定线程
   *
   * @param key 键
   * @return 绑定后的执行入口（支持 inLoop；开启慢监控时会采样栈）
   */
  public BoundLoop bind(K key) {
    Holder holder = getBindMap().get(key);
    if (holder == null) {
      synchronized (this) {
        Map<K, Holder> bps = getBindMap();
        if ((holder = bps.get(key)) != null) {
          return new BoundLoop(holder.executor, key);
        }
        holder = getLoops().stream().min(Comparator.comparingInt(o -> o.sizer.get())).orElse(null);//查找绑定数量最少的一个
        if (holder != null && holder.size() < getMaxBindSize()) {
          bps.put(key, holder.increment());
          return new BoundLoop(holder.executor, key);
        }
        ThreadFactory factory = threadFactory.get();
        if (factory == null) {
          final String prefix = StringUtils.getIfBlank(getNamePrefix(), () -> "loop" + threadNumber.getAndIncrement() + "-binder-");
          threadFactory.set(factory = EventLoop.newThreadFactory(prefix, true));
        }
        holder = new Holder(EventLoop.newSingle(factory));
        getLoops().add(holder);
        holder.increment();// 分配线程
        bps.put(key, holder);
        return new BoundLoop(holder.executor, key);
      }
    }
    holder.setActiveAt(System.currentTimeMillis());
    return new BoundLoop(holder.executor, key);
  }

  public Single unbind(K key) {
    Holder holder = bindMap.remove(key);
    return holder != null ? holder.decrement().executor : SINGLE;
  }

  public Set<Holder> getLoops() {
    return loops;
  }

  public Map<K, Holder> getBindMap() {
    return bindMap;
  }

  public String getNamePrefix() {
    return namePrefix;
  }

  public void setNamePrefix(String namePrefix) {
    this.namePrefix = namePrefix;
  }

  public int getMaxBindSize() {
    return maxBindSize;
  }

  public void setMaxBindSize(int maxBindSize) {
    this.maxBindSize = maxBindSize;
  }

  public int getCacheSize() {
    return cacheSize;
  }

  public void setCacheSize(int cacheSize) {
    this.cacheSize = cacheSize;
  }

  public Duration getExpired() {
    return expired;
  }

  public void setExpired(Duration expired) {
    this.expired = expired;
  }

  /**
   * 是否对本实例的 inLoop 做慢任务监控
   */
  public boolean isSlowMonitorEnabled() {
    return slowMonitorEnabled;
  }

  public void setSlowMonitorEnabled(boolean slowMonitorEnabled) {
    this.slowMonitorEnabled = slowMonitorEnabled;
  }

  /**
   * 本实例慢任务阈值（毫秒）
   */
  public long getSlowMonitorThresholdMs() {
    return slowMonitorThresholdMs;
  }

  public void setSlowMonitorThresholdMs(long slowMonitorThresholdMs) {
    this.slowMonitorThresholdMs = Math.max(slowMonitorThresholdMs, 1L);
  }

  /**
   * bind 后的执行入口；慢任务监控仅覆盖此处的 inLoop。
   */
  public final class BoundLoop {
    private final Single executor;
    private final K key;

    BoundLoop(Single executor, K key) {
      this.executor = executor;
      this.key = key;
    }

    public Single executor() {
      return executor;
    }

    public K key() {
      return key;
    }

    public IFuture<?> inLoop(Runnable task) {
      if (!slowMonitorEnabled) {
        return executor.inLoop(task);
      }
      final String taskName = taskName();
      final EventLoopBinder<K> binder = EventLoopBinder.this;
      return executor.inLoop(() -> EventLoopSlowTaskMonitor.run(binder, task, taskName));
    }

    public <V> IFuture<V> inLoop(Callable<V> task) {
      if (!slowMonitorEnabled) {
        return executor.inLoop(task);
      }
      final String taskName = taskName();
      final EventLoopBinder<K> binder = EventLoopBinder.this;
      return executor.inLoop(() -> EventLoopSlowTaskMonitor.call(binder, task, taskName));
    }

    private String taskName() {
      String prefix = StringUtils.defaultString(getNamePrefix());
      return prefix + String.valueOf(key);
    }
  }

  static class Holder {

    final AtomicInteger sizer = new AtomicInteger(0);

    final Single executor;

    final AtomicLong activeAt = new AtomicLong(System.currentTimeMillis());

    Holder(Single executor) {
      this.executor = executor;
    }

    Holder increment() {
      sizer.incrementAndGet();
      return this;
    }

    Holder decrement() {
      sizer.decrementAndGet();
      return this;
    }

    public int size() {
      return sizer.get();
    }

    long getActiveAt() {
      return this.activeAt.get();
    }

    void setActiveAt(long time) {
      this.activeAt.set(time);
    }
  }

}
