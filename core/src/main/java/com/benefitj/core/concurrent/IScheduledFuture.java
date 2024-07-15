package com.benefitj.core.concurrent;

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

  /**
   * 包裹值
   */
  static <V> IScheduledFuture<V> wrapValue(V value) {
    return new IScheduledFutureBase<V>() {
      @Override
      public boolean isDone() {
        return true;
      }

      @Override
      public V get() throws IllegalStateException {
        return value;
      }

      @Override
      public V get(long timeout, TimeUnit unit) throws IllegalStateException {
        return value;
      }
    };
  }

  /**
   * 包裹异常
   */
  static <V> IScheduledFuture<V> wrapFail(Throwable e) {
    return new IScheduledFutureBase<V>() {
      @Override
      public boolean isDone() {
        return true;
      }

      @Override
      public V get() throws IllegalStateException {
        throw new IllegalStateException(e);
      }

      @Override
      public V get(long timeout, TimeUnit unit) throws IllegalStateException {
        throw new IllegalStateException(e);
      }
    };
  }

  /**
   * 什么都不做
   */
  static <V> IScheduledFuture<V> nothing() {
    return (IScheduledFuture<V>) NOTHING;
  }


  static <V> Impl<V> wrap(ScheduledFuture<V> future) {
    return new Impl<>(future);
  }

  class Impl<V> implements IScheduledFuture<V> {

    ScheduledFuture<V> raw;

    public Impl() {
    }

    public Impl(ScheduledFuture<V> raw) {
      this.raw = raw;
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


  interface IScheduledFutureBase<V> extends IScheduledFuture<V> {
    @Override
    default boolean cancel(boolean mayInterruptIfRunning) {
      return false;
    }

    @Override
    default boolean isCancelled() {
      return false;
    }

    @Override
    default boolean isDone() {
      return false;
    }

    @Override
    default V get() throws IllegalStateException {
      return null;
    }

    @Override
    default V get(long timeout, TimeUnit unit) throws IllegalStateException {
      return null;
    }

    @Override
    default long getDelay(TimeUnit unit) {
      return 0;
    }

    @Override
    default int compareTo(Delayed o) {
      return 0;
    }
  }

  /**
   * 空对象
   */
  IScheduledFuture<Object> NOTHING = new IScheduledFutureBase<Object>() {
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return true;
    }

    @Override
    public boolean isCancelled() {
      return true;
    }

    @Override
    public boolean isDone() {
      return true;
    }
  };

}
