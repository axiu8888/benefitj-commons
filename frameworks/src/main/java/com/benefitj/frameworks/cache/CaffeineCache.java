package com.benefitj.frameworks.cache;

import com.benefitj.core.EventLoop;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.Scheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 缓存
 */
public interface CaffeineCache {

  static <K, V> Caffeine<K, V> newWriteCache(long duration, TimeUnit unit) {
    return newBuilder()
        .removalListener(CaffeineCache.<K, V>emptyRemovalListener())
        .expireAfterWrite(duration, unit);
  }

  static <K, V> Caffeine<K, V> newBuilder() {
    return (Caffeine<K, V>) Caffeine.newBuilder()
        .executor(EventLoop.io())
        .scheduler(CaffeineCache.SCHEDULER)
        ;
  }

  static <K, V> RemovalListener<K, V> emptyRemovalListener() {
    return (RemovalListener<K, V>) REMOVAL_LISTENER;
  }

  Scheduler SCHEDULER = new EventLoopScheduler();

  RemovalListener<Object, Object> REMOVAL_LISTENER = (o, o2, removalCause) -> {
    // nothing
  };

  class EventLoopScheduler implements Scheduler {

    @Override
    public Future<?> schedule(Executor executor, Runnable task, long delay, TimeUnit unit) {
      if (executor instanceof ScheduledExecutorService) {
        return ((ScheduledExecutorService) executor).schedule(task, delay, unit);
      } else {
        return EventLoop.io().schedule(task, delay, unit);
      }
    }

  }

}
