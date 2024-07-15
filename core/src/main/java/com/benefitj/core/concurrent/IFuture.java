package com.benefitj.core.concurrent;

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

  /**
   * 包裹值
   */
  static <V> IFuture<V> wrapValue(V value) {
    return new IFutureBase<V>() {
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
  static <V> IFuture<V> wrapFail(Throwable e) {
    return new IFutureBase<V>() {
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
   * 什么都不操作
   */
  static <V> IFuture<V> nothing() {
    return (IFuture<V>) NOTHING;
  }


  static <V> Impl<V> wrap(Future<V> future) {
    return new Impl<>(future);
  }

  class Impl<V> implements IFuture<V> {

    Future<V> raw;

    public Impl() {
    }

    public Impl(Future<V> raw) {
      this.raw = raw;
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


  interface IFutureBase<V> extends IFuture<V> {
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
  }


  /**
   * 空对象
   */
  IFuture<Object> NOTHING = new IFutureBase<Object>() {
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
