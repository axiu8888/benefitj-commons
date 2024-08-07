package com.benefitj.core;

import com.benefitj.core.functions.IFuture;
import com.benefitj.core.functions.IScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * 事件循环
 */
public class EventLoop implements ExecutorService, ScheduledExecutorService {

  private static final int PROCESSOR_SIZE = Runtime.getRuntime().availableProcessors();
  /**
   * 主线程
   */
  private static final SingletonSupplier<EventLoop> MAIN_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(1, "-main-", false));
  private static final SingletonSupplier<EventLoop> MULTI_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(PROCESSOR_SIZE, "-multi-", true));
  private static final SingletonSupplier<EventLoop> SINGLE_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(1, "-single-", true));
  private static final SingletonSupplier<EventLoop> IO_EVENT_LOOP
      = SingletonSupplier.of(() -> new GlobalEventLoop(64, "-io-", true));

  private static final Logger log = LoggerFactory.getLogger(EventLoop.class);

  /**
   * 多线程事件
   */
  public static EventLoop main() {
    return MAIN_EVENT_LOOP.get();
  }

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

  private final int corePoolSize;
  private final ScheduledExecutorService executor;
  private final AtomicReference<Thread> loopSingle = new AtomicReference<>();

  public EventLoop(int corePoolSize) {
    this(corePoolSize, false);
  }

  public EventLoop(int corePoolSize, boolean daemon) {
    this(corePoolSize, newThreadFactory(nextThreadNamePrefix(), daemon));
  }

  public EventLoop(int corePoolSize, ThreadFactory threadFactory) {
    this.corePoolSize = corePoolSize;
    this.executor = Executors.newScheduledThreadPool(corePoolSize, threadFactory);
    if (isSingle()) {
      this.submit(() -> loopSingle.set(Thread.currentThread())).get();
    }
  }

  protected ScheduledExecutorService getExecutor() {
    return executor;
  }

  public boolean isSingle() {
    return corePoolSize == 1;
  }

  public boolean inLoopSingle() {
    return isSingle() && loopSingle.get() == Thread.currentThread();
  }

  @Override
  public IScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return wrap(getExecutor().schedule(wrap(command), delay, unit));
  }

  @Override
  public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return getExecutor().schedule(wrap(callable), delay, unit);
  }

  @Override
  public IScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return wrap(getExecutor().scheduleAtFixedRate(wrap(command), initialDelay, period, unit));
  }

  @Override
  public IScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return wrap(getExecutor().scheduleWithFixedDelay(wrap(command), initialDelay, delay, unit));
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
  public <T> IFuture<T> submit(Callable<T> task) {
    return wrap(getExecutor().submit(wrap(task)));
  }

  @Override
  public <T> IFuture<T> submit(Runnable task, T result) {
    return wrap(getExecutor().submit(wrap(task), result));
  }

  @Override
  public IFuture<?> submit(Runnable task) {
    return wrap(getExecutor().submit(wrap(task)));
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return getExecutor().invokeAll(wrap(tasks));
  }

  @Override
  public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return getExecutor().invokeAll(wrap(tasks), timeout, unit);
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return getExecutor().invokeAny(wrap(tasks));
  }

  @Override
  public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return getExecutor().invokeAny(wrap(tasks), timeout, unit);
  }

  @Override
  public void execute(Runnable command) {
    getExecutor().execute(wrap(command));
  }

  /**
   * 包裹 Runnable
   *
   * @param task 任务
   * @return 返回结果
   */
  public static Runnable wrap(Runnable task) {
    return () -> {
      try {
        task.run();
      } catch (Throwable e) {
        log.error(e.getMessage(), e);
        throw e;
      }
    };
  }

  /**
   * 包裹 Callable
   *
   * @param task 任务
   * @param <T>  返回类型
   * @return 返回结果
   */
  public static <T> Callable<T> wrap(Callable<T> task) {
    return () -> {
      try {
        return task.call();
      } catch (Throwable e) {
        log.error(e.getMessage(), e);
        throw e;
      }
    };
  }

  /**
   * 包裹 Callable
   *
   * @param tasks 任务
   * @param <T>   返回类型
   * @return 返回结果
   */
  public static <T> Collection<? extends Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
    return tasks.stream()
        .map(EventLoop::wrap)
        .collect(Collectors.toList());
  }

  /**
   * 包裹 Future
   *
   * @param future 任务
   * @param <V>    返回类型
   * @return 返回结果
   */
  public static <V> IFuture<V> wrap(Future<V> future) {
    return IFuture.wrap(future);
  }

  /**
   * 包裹 ScheduledFuture
   *
   * @param future 任务
   * @param <V>    返回类型
   * @return 返回结果
   */
  public static <V> IScheduledFuture<V> wrap(ScheduledFuture<V> future) {
    return IScheduledFuture.wrap(future);
  }

  /**
   * 创建单线程的EventLoop
   */
  public static EventLoop newSingle(boolean daemon) {
    return newEventLoop(1, daemon);
  }

  /**
   * 创建EventLoop
   */
  public static EventLoop newCoreLoop(boolean daemon) {
    int coreSize = Runtime.getRuntime().availableProcessors();
    return newEventLoop(coreSize, daemon);
  }

  /**
   * 创建EventLoop
   */
  public static EventLoop newEventLoop(int corePoolSize, boolean daemon) {
    return newEventLoop(nextThreadNamePrefix(), corePoolSize, daemon);
  }

  /**
   * 创建EventLoop
   */
  public static EventLoop newEventLoop(String namePrefix, int corePoolSize, boolean daemon) {
    return new EventLoop(corePoolSize, newThreadFactory(namePrefix, daemon));
  }

  private static final AtomicInteger ID = new AtomicInteger(1);

  static String nextThreadNamePrefix() {
    return "loop" + ID.incrementAndGet() + "-thread-";
  }

  static final class GlobalEventLoop extends EventLoop {

    private GlobalEventLoop(int corePoolSize, String suffix, boolean daemon) {
      super(corePoolSize, newThreadFactory("loop-" + ID.getAndIncrement() + suffix, daemon));
      if (!daemon) {
        ShutdownHook.register(super::shutdown);
      }
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

  /**
   * 取消调度
   *
   * @param sf 调度任务
   * @return 返回是否取消
   */
  public static boolean cancel(ScheduledFuture<?> sf) {
    return sf != null && sf.cancel(true);
  }

  public static String threadName() {
    return Thread.currentThread().getName();
  }

  /**
   * 等待
   *
   * @param seconds 时长(秒)
   */
  public static void awaitSeconds(int seconds) {
    await(TimeUnit.SECONDS.toMillis(seconds));
  }

  /**
   * 等待
   *
   * @param durationMillis 时长
   */
  public static void await(long durationMillis) {
    await(Thread.currentThread(), durationMillis);
  }

  /**
   * 等待
   *
   * @param t              等待的线程
   * @param durationMillis 时长
   */
  public static void await(Thread t, long durationMillis) {
    await(t, durationMillis, TimeUnit.MILLISECONDS);
  }

  /**
   * 等待
   *
   * @param duration 时长
   * @param unit     单位
   */
  public static void await(long duration, TimeUnit unit) {
    await(Thread.currentThread(), duration, unit);
  }

  /**
   * 等待
   *
   * @param t        等待的线程
   * @param duration 时长
   * @param unit     单位
   */
  public static void await(Thread t, long duration, TimeUnit unit) {
    try {
      t.join(unit.toNanos(duration));
    } catch (InterruptedException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * sleep
   *
   * @param duration 时长
   */
  public static void sleepMillis(long duration) {
    sleep(duration, TimeUnit.MILLISECONDS);
  }

  /**
   * sleep
   *
   * @param duration 时长
   */
  public static void sleepSecond(long duration) {
    sleep(duration, TimeUnit.SECONDS);
  }

  /**
   * sleep
   *
   * @param duration 时长
   * @param unit     单位
   */
  public static void sleep(long duration, TimeUnit unit) {
    try {
      Thread.sleep(unit.toMillis(duration));
    } catch (InterruptedException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  public static IScheduledFuture<?> asyncIO(Runnable task) {
    return asyncIO(task, 0);
  }

  public static IScheduledFuture<?> asyncIO(Runnable task, long delay) {
    return asyncIO(task, delay, TimeUnit.MILLISECONDS);
  }

  public static IScheduledFuture<?> asyncIO(Runnable task, long delay, TimeUnit unit) {
    return io().schedule(task, delay, unit);
  }

  public static IScheduledFuture<?> asyncIOFixedRate(Runnable task, long period) {
    return asyncIOFixedRate(task, period, period);
  }

  public static IScheduledFuture<?> asyncIOFixedRate(Runnable task, long initialDelay, long period) {
    return asyncIOFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
  }

  public static IScheduledFuture<?> asyncIOFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
    return io().scheduleAtFixedRate(task, initialDelay, period, unit);
  }


  /**
   * 创建线程池
   *
   * @param namePrefix 线程名称前缀
   * @param daemon     是否为守护线程
   * @return 返回线程工厂
   */
  public static ThreadFactory newThreadFactory(String namePrefix, boolean daemon) {
    return newThreadFactory(namePrefix, daemon, Thread.NORM_PRIORITY);
  }

  /**
   * 创建线程池
   *
   * @param namePrefix 线程名称前缀
   * @param daemon     是否为守护线程
   * @param priority   线程优先级
   * @return 返回线程工厂
   */
  public static ThreadFactory newThreadFactory(String namePrefix, boolean daemon, int priority) {
    final AtomicInteger threadNumber = new AtomicInteger(1);
    final ThreadGroup group = Thread.currentThread().getThreadGroup();
    return r -> {
      Thread t = new Thread(group
          , r
          , namePrefix + threadNumber.getAndIncrement()
          , 0
      );
      t.setDaemon(daemon);
      if (t.getPriority() != priority) t.setPriority(priority);
      return t;
    };
  }

}
