package com.benefitj.core.concurrent;

import java.util.concurrent.*;

/**
 * ScheduledFuture包装器
 *
 * @param <V>
 */
public class ScheduledFutureWrapper<V> implements ScheduledFuture<V> {

  private ScheduledFuture<V> original;

  public ScheduledFutureWrapper(ScheduledFuture<V> original) {
    this.original = original;
  }

  public ScheduledFuture<V> getOriginal() {
    return original;
  }

  public void setOriginal(ScheduledFuture<V> original) {
    this.original = original;
  }

  @Override
  public long getDelay(TimeUnit unit) {
    return getOriginal().getDelay(unit);
  }

  @Override
  public int compareTo(Delayed o) {
    return getOriginal().compareTo(o);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return getOriginal().cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return getOriginal().isCancelled();
  }

  @Override
  public boolean isDone() {
    return getOriginal().isDone();
  }

  @Override
  public V get() throws InterruptedException, ExecutionException {
    return getOriginal().get();
  }

  @Override
  public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return getOriginal().get(timeout, unit);
  }

}
