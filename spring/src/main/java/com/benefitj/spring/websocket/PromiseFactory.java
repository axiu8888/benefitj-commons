package com.benefitj.spring.websocket;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Promise Factory
 */
public class PromiseFactory {

  /**
   * 创建 SuccessPromise
   */
  public static <V> SuccessPromise<V> newSuccess() {
    return new SuccessPromise<>();
  }

  /**
   * 创建 FailurePromise
   */
  public static <V> FailurePromise<V> newFailure() {
    return new FailurePromise<>();
  }

  /**
   * 创建 FailurePromise
   */
  public static <V> FailurePromise<V> newFailure(Throwable error) {
    return new FailurePromise<>(error);
  }

  /**
   * 创建Future的包装器
   */
  public static <V> PromiseWrapper<V> newFutureWrapper(Future<V> future) {
    return new PromiseWrapper<>(future);
  }

  /**
   * 成功
   */
  public static class SuccessPromise<V> implements FuturePromise<V> {
    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return true;
    }

    @Override
    public boolean isCancelled() {
      return false;
    }

    @Override
    public boolean isDone() {
      return true;
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
      return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return null;
    }
  }

  /**
   * 失败
   */
  public static class FailurePromise<V> implements FuturePromise<V> {

    private Throwable exception;

    public FailurePromise() {
    }

    public FailurePromise(Throwable exception) {
      this.exception = exception;
    }

    /**
     * @return 获取异常
     */
    public Throwable getException() {
      return exception;
    }

    /**
     * 设置异常
     *
     * @param exception 异常
     */
    public void setException(Throwable exception) {
      this.exception = exception;
    }

    /**
     * @return 错误信息
     */
    public String getErrorMessage() {
      return getException() != null ? getException().getMessage() : "";
    }

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

    @Override
    public V get() throws InterruptedException, ExecutionException {
      return null;
    }

    @Override
    public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return null;
    }
  }

  /**
   * Future包装器
   */
  public static class PromiseWrapper<V> implements FuturePromise<V> {

    private final Future<V> future;

    public PromiseWrapper(Future<V> future) {
      this.future = future;
    }

    public Future<V> getFuture() {
      return future;
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
      return getFuture().cancel(mayInterruptIfRunning);
    }

    @Override
    public boolean isCancelled() {
      return getFuture().isCancelled();
    }

    @Override
    public boolean isDone() {
      return getFuture().isDone();
    }

    @Override
    public V get() throws InterruptedException, ExecutionException {
      return getFuture().get();
    }

    @Override
    public V get(long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return getFuture().get(timeout, unit);
    }
  }
}
