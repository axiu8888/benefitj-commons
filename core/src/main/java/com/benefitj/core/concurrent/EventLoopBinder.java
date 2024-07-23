package com.benefitj.core.concurrent;


import com.benefitj.core.EventLoop;
import com.benefitj.core.EventLoop.Single;
import com.benefitj.core.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.util.*;
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

  private AtomicReference<ScheduledFuture<?>> releaseTimer = new AtomicReference<>();

  public EventLoopBinder() {
    this(null, 50, 3, Duration.ofSeconds(30));
  }

  /**
   * 构造函数
   *
   * @param namePrefix  线程名前缀
   * @param maxBindSize 线程绑定的最大设备数
   * @param expired     线程超时释放
   */
  public EventLoopBinder(String namePrefix, int maxBindSize, int cacheSize, Duration expired) {
    this.namePrefix = namePrefix;
    this.maxBindSize = maxBindSize;
    this.cacheSize = cacheSize;
    this.expired = expired;
    // 释放资源
    releaseTimer.set(SINGLE.scheduleAtFixedRate(this::releaseLoop, 10, 10, TimeUnit.SECONDS));
  }

  private void releaseLoop() {
    Set<Holder> set = loops;
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
   * @return 返回线程池
   */
  public Single bind(K key) {
    Holder holder = getBindMap().get(key);
    if (holder == null) {
      synchronized (this) {
        Map<K, Holder> bps = getBindMap();
        if ((holder = bps.get(key)) != null) return holder.executor;
        holder = loops.stream().min(Comparator.comparingInt(o -> o.sizer.get())).orElse(null);//查找绑定数量最少的一个
        if (holder != null && holder.size() < getMaxBindSize()) {
          bps.put(key, holder.increment());
          return holder.executor;
        }
        ThreadFactory factory = threadFactory.get();
        if (factory == null) {
          final String prefix = StringUtils.getIfBlank(getNamePrefix(), () -> "loop" + threadNumber.getAndIncrement() + "-binder-");
          threadFactory.set(factory = EventLoop.newThreadFactory(prefix, true));
        }
        holder = new Holder(EventLoop.newSingle(factory));
        loops.add(holder);
        holder.increment();// 分配线程
        bps.put(key, holder);
      }
    }
    holder.setActiveAt(System.currentTimeMillis());
    return holder.executor;
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
