package com.benefitj.core.concurrent;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.CatchUtils;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 可取消的调度
 *
 * @param <V>
 */
public class CancelableFuture<V> implements ScheduledFuture<V>, AttributeMap {

  private final ScheduledFuture<V> _raw_;
  private final Map<String, Object> _attrs_ = new ConcurrentHashMap<>();

  public CancelableFuture(ScheduledFuture<V> raw) {
    this._raw_ = raw;
  }

  public ScheduledFuture<V> _raw_() {
    return _raw_;
  }

  @Override
  public long getDelay(TimeUnit unit) {
    return _raw_().getDelay(unit);
  }

  @Override
  public int compareTo(Delayed o) {
    return _raw_().compareTo(o);
  }

  @Override
  public boolean cancel(boolean mayInterruptIfRunning) {
    return _raw_().cancel(mayInterruptIfRunning);
  }

  @Override
  public boolean isCancelled() {
    return _raw_().isCancelled();
  }

  @Override
  public boolean isDone() {
    return _raw_().isDone();
  }

  @Override
  public V get() {
    try {
      return _raw_().get();
    } catch (InterruptedException | ExecutionException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  @Override
  public V get(long timeout, TimeUnit unit) {
    try {
      return _raw_().get(timeout, unit);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  @Override
  public Map<String, Object> attrs() {
    return _attrs_;
  }
}
