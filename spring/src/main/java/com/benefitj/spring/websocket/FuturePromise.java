package com.benefitj.spring.websocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Future
 *
 * @param <V>
 */
public interface FuturePromise<V> extends Future<V> {

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
  V get() throws InterruptedException, ExecutionException;

  @Override
  V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;

  /**
   * @return 是否成功，如果 isCancelled() == false && isDone() == true 表示成功
   */
  default boolean isSuccessful() {
    return !isCancelled() && isDone();
  }
}