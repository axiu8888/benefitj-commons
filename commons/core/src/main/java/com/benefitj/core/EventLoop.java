package com.benefitj.core;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 执行事件
 */
public class EventLoop implements ScheduledExecutorService {

  private static final int PROCESSOR_SIZE = Runtime.getRuntime().availableProcessors();
  /**
   * 全局事件实例
   */
  private static final SingletonSupplier<EventLoop> MULTI_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(PROCESSOR_SIZE, "-multi-"));
  private static final SingletonSupplier<EventLoop> SINGLE_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(1, "-single-"));
  private static final SingletonSupplier<EventLoop> IO_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(128, "-io-"));

  /**
   * 多线程事件
   */
  public static EventLoop multi() {
    return MULTI_EVENT_LOOP.get();
  }

  /**
   * 单线程事件
   */
  public static EventLoop single() {
    return SINGLE_EVENT_LOOP.get();
  }

  /**
   * IO事件，128个线程
   */
  public static EventLoop io() {
    return IO_EVENT_LOOP.get();
  }

  private final ScheduledExecutorService executor;

  public EventLoop(int corePoolSize) {
    this(corePoolSize, defaultThreadFactory());
  }

  public EventLoop(int corePoolSize, ThreadFactory threadFactory) {
    this.executor = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
  }

  protected ScheduledExecutorService getExecutor() {
    return executor;
  }

  @Override
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return getExecutor().schedule(command, delay, unit);
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return getExecutor().schedule(callable, delay, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return getExecutor().scheduleAtFixedRate(command, initialDelay, period, unit);
  }

  @Override
  public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return getExecutor().scheduleWithFixedDelay(command, initialDelay, delay, unit);
  }

  @Override
  public void shutdown() {
    getExecutor().shutdown();
  }

  @Override
  public List<Runnable> shutdownNow() {
    return getExecutor().shutdownNow();
  }

  @Override
  public boolean isShutdown() {
    return getExecutor().isShutdown();
  }

  @Override
  public boolean isTerminated() {
    return getExecutor().isTerminated();
  }

  @Override
  public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return getExecutor().awaitTermination(timeout, unit);
  }

  @Override
  public <T> Future<T> submit(Callable<T> task) {
    return getExecutor().submit(task);
  }

  @Override
  public <T> Future<T> submit(Runnable task, T result) {
    return getExecutor().submit(task, result);
  }

  @Override
  public Future<?> submit(Runnable task) {
    return getExecutor().submit(task);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return getExecutor().invokeAll(tasks);
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return getExecutor().invokeAll(tasks, timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return getExecutor().invokeAny(tasks);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return getExecutor().invokeAny(tasks, timeout, unit);
  }

  @Override
  public void execute(Runnable command) {
    getExecutor().execute(command);
  }

  private static final AtomicInteger ID = new AtomicInteger(0);

  private static ThreadFactory defaultThreadFactory() {
    String prefix = String.format("eventLoop%d-", ID.incrementAndGet());
    return new DefaultThreadFactory(prefix, "-T-");
  }

  static final class GlobalEventLoop extends EventLoop {

    private GlobalEventLoop(int corePoolSize, String suffix) {
      super(corePoolSize, new DefaultThreadFactory("global-", suffix));
      ShutdownHook.register(super::shutdown);
    }

    @Override
    public void shutdown() {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<Runnable> shutdownNow() {
      throw new UnsupportedOperationException();
    }

  }
}
