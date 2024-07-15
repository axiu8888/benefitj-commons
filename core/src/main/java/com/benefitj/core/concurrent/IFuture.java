package com.benefitj.core.concurrent;

import com.benefitj.core.AttributeMap;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface IFuture<V> extends Future<V> {

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


  static <V> Impl<V> wrap(Future<V> future) {
    return new Impl<>(future);
  }

  class Impl<V> implements IFuture<V>, AttributeMap {

    final Map<String, Object> attrs = new ConcurrentHashMap<>();

    Future<V> raw;

    public Impl() {
    }

    public Impl(Future<V> raw) {
      this.raw = raw;
    }

    @Override
    public Map<String, Object> attrs() {
      return attrs;
    }

    public Future<V> getRaw() {
      return raw;
    }

    public void setRaw(Future<V> raw) {
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
  }

}
