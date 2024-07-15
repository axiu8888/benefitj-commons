package com.benefitj.core.concurrent;

import com.benefitj.core.AttributeMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public interface IScheduledFuture<V> extends ScheduledFuture<V> {

  @Override
  boolean cancel(boolean mayInterruptIfRunning);

  @Override
  boolean isCancelled();

  @Override
  boolean isDone();

  @Override
  V get() throws IllegalStateException;

  @Override
  V get(long timeout, TimeUnit unit) throws IllegalStateException;

  @Override
  long getDelay(TimeUnit unit);

  @Override
  int compareTo(Delayed o);

  static <V> Impl<V> wrap(ScheduledFuture<V> future) {
    return new Impl<>(future);
  }

  class Impl<V> implements IScheduledFuture<V>, AttributeMap {

    final Map<String, Object> attrs = new ConcurrentHashMap<>();

    ScheduledFuture<V> raw;

    public Impl() {
    }

    public Impl(ScheduledFuture<V> raw) {
      this.raw = raw;
    }

    @Override
    public Map<String, Object> attrs() {
      return attrs;
    }

    public ScheduledFuture<V> getRaw() {
      return raw;
    }

    public void setRaw(ScheduledFuture<V> raw) {
      this.raw = raw;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return getRaw().cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return getRaw().isCancelled();
    }

    @Override
    public boolean isDone() {
      return getRaw().isDone();
    }

    @Override
    public V get() throws IllegalStateException {
      try {
        return getRaw().get();
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public V get(long timeout, TimeUnit unit) throws IllegalStateException {
      try {
        return getRaw().get(timeout, unit);
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }

    @Override
    public long getDelay(TimeUnit unit) {
      return getRaw().getDelay(unit);
    }

    @Override
    public int compareTo(Delayed o) {
      return getRaw().compareTo(o);
    }
  }

}
