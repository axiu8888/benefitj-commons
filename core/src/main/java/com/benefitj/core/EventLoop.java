package com.benefitj.core;

import com.benefitj.core.concurrent.IFuture;
import com.benefitj.core.concurrent.IScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


/**
 * 线程池
 */
public interface EventLoop extends ExecutorService, ScheduledExecutorService {

  ScheduledExecutorService executor();

  @Override
  default IScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return wrap(executor().schedule(wrap(command), delay, unit));
  }

  @Override
  default <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
    return executor().schedule(wrap(callable), delay, unit);
  }

  @Override
  default IScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
    return wrap(executor().scheduleAtFixedRate(wrap(command), initialDelay, period, unit));
  }

  @Override
  default IScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
    return wrap(executor().scheduleWithFixedDelay(wrap(command), initialDelay, delay, unit));
  }

  @Override
  default void shutdown() {
    executor().shutdown();
  }

  @Override
  default List<Runnable> shutdownNow() {
    return executor().shutdownNow();
  }

  @Override
  default boolean isShutdown() {
    return executor().isShutdown();
  }

  @Override
  default boolean isTerminated() {
    return executor().isTerminated();
  }

  @Override
  default boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
    return executor().awaitTermination(timeout, unit);
  }

  @Override
  default <T> IFuture<T> submit(Callable<T> task) {
    return wrap(executor().submit(wrap(task)));
  }

  @Override
  default <T> IFuture<T> submit(Runnable task, T result) {
    return wrap(executor().submit(wrap(task), result));
  }

  @Override
  default IFuture<?> submit(Runnable task) {
    return wrap(executor().submit(wrap(task)));
  }

  @Override
  default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
    return executor().invokeAll(wrap(tasks));
  }

  @Override
  default <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
    return executor().invokeAll(wrap(tasks), timeout, unit);
  }

  @Override
  default <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
    return executor().invokeAny(wrap(tasks));
  }

  @Override
  default <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
    return executor().invokeAny(wrap(tasks), timeout, unit);
  }

  @Override
  default void execute(Runnable command) {
    executor().execute(wrap(command));
  }

  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */

  /**
   * 包裹 Runnable
   *
   * @param task 任务
   * @return 返回结果
   */
  static Runnable wrap(Runnable task) {
    return () -> {
      try {
        task.run();
      } catch (Throwable e) {
        Global.log.error(e.getMessage(), e);
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
  static <T> Callable<T> wrap(Callable<T> task) {
    return () -> {
      try {
        return task.call();
      } catch (Throwable e) {
        Global.log.error(e.getMessage(), e);
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
  static <T> Collection<? extends Callable<T>> wrap(Collection<? extends Callable<T>> tasks) {
    return tasks.stream().map(EventLoop::wrap).collect(Collectors.toList());
  }

  /**
   * 包裹 Future
   *
   * @param future 任务
   * @param <V>    返回类型
   * @return 返回结果
   */
  static <V> IFuture<V> wrap(Future<V> future) {
    return IFuture.wrap(future);
  }

  /**
   * 包裹 ScheduledFuture
   *
   * @param future 任务
   * @param <V>    返回类型
   * @return 返回结果
   */
  static <V> IScheduledFuture<V> wrap(ScheduledFuture<V> future) {
    return IScheduledFuture.wrap(future);
  }

  /**
   * 取消调度
   *
   * @param sf 调度任务
   * @return 返回是否取消
   */
  static boolean cancel(ScheduledFuture<?> sf) {
    return sf != null && sf.cancel(true);
  }

  /**
   * 创建线程池
   *
   * @param namePrefix 线程名称前缀
   * @param daemon     是否为守护线程
   * @return 返回线程工厂
   */
  static ThreadFactory newThreadFactory(String namePrefix, boolean daemon) {
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
  static ThreadFactory newThreadFactory(String namePrefix, boolean daemon, int priority) {
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

  /**
   * CPU数量
   */
  static int coreSize() {
    return Runtime.getRuntime().availableProcessors();
  }

  /**
   * 获取线程名
   */
  static String getThreadName() {
    return Thread.currentThread().getName();
  }

  /**
   * sleep
   *
   * @param duration 时长
   */
  static void sleepSecond(long duration) {
    sleep(duration * 1000L);
  }

  /**
   * sleep
   *
   * @param duration 时长
   */
  static void sleep(long duration) {
    sleep(duration, TimeUnit.MILLISECONDS);
  }

  /**
   * sleep
   *
   * @param duration 时长
   * @param unit     单位
   */
  static void sleep(long duration, TimeUnit unit) {
    try {
      Thread.sleep(unit.toMillis(duration));
    } catch (InterruptedException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 创建EventLoop
   */
  static EventLoop newCoreLoop(boolean daemon) {
    return newEventLoop(coreSize(), daemon);
  }

  /**
   * 创建EventLoop
   */
  static EventLoop newEventLoop(int corePoolSize, boolean daemon) {
    return newEventLoop(generateNamePrefix(), corePoolSize, daemon);
  }

  /**
   * 创建EventLoop
   */
  static EventLoop newEventLoop(String namePrefix, int corePoolSize, boolean daemon) {
    return new Impl(corePoolSize, newThreadFactory(namePrefix, daemon));
  }


  /**
   * 默认实现
   */
  public class Impl implements EventLoop {

    final ScheduledExecutorService executor;

    public Impl(String namePrefix, int corePoolSize) {
      this(namePrefix, corePoolSize, false);
    }

    public Impl(String namePrefix, int corePoolSize, boolean daemon) {
      this(corePoolSize, newThreadFactory(namePrefix, daemon));
    }

    public Impl(int corePoolSize, ThreadFactory threadFactory) {
      this(Executors.newScheduledThreadPool(corePoolSize, threadFactory));
    }

    public Impl(ScheduledExecutorService executor) {
      this.executor = executor;
    }

    public ScheduledExecutorService executor() {
      return executor;
    }

  }

  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */

  AtomicInteger ID = new AtomicInteger(1);

  /**
   * 生成线程名前缀
   */
  static String generateNamePrefix() {
    return "loop" + ID.incrementAndGet() + "-worker-";
  }

  /**
   * 全局线程池
   */
  public final class Global extends Impl {

    private Global(int corePoolSize, String suffix, boolean daemon) {
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

    static final SingletonSupplier<EventLoop> MAIN_EVENT_LOOP = SingletonSupplier.of(() -> new Global(1, "-main-", false));
    static final SingletonSupplier<EventLoop> MULTI_EVENT_LOOP = SingletonSupplier.of(() -> new Global(coreSize(), "-multi-", true));
    static final SingletonSupplier<EventLoop> SINGLE_EVENT_LOOP = SingletonSupplier.of(() -> new Global(1, "-single-", true));
    static final SingletonSupplier<EventLoop> IO_EVENT_LOOP = SingletonSupplier.of(() -> new Global(64, "-io-", true));

    static final Logger log = LoggerFactory.getLogger(EventLoop.class);

  }


  /**
   * 多线程事件
   */
  static EventLoop main() {
    return Global.MAIN_EVENT_LOOP.get();
  }

  /**
   * 多线程事件
   */
  static EventLoop multi() {
    return Global.MULTI_EVENT_LOOP.get();
  }

  /**
   * 单线程事件
   */
  static EventLoop single() {
    return Global.SINGLE_EVENT_LOOP.get();
  }

  /**
   * IO事件，128个线程
   */
  static EventLoop io() {
    return Global.IO_EVENT_LOOP.get();
  }

  static IScheduledFuture<?> asyncIO(Runnable task) {
    return asyncIO(task, 0);
  }

  static IScheduledFuture<?> asyncIO(Runnable task, long delay) {
    return asyncIO(task, delay, TimeUnit.MILLISECONDS);
  }

  static IScheduledFuture<?> asyncIO(Runnable task, long delay, TimeUnit unit) {
    return io().schedule(task, delay, unit);
  }

  static IScheduledFuture<?> asyncIOFixedRate(Runnable task, long period) {
    return asyncIOFixedRate(task, period, period);
  }

  static IScheduledFuture<?> asyncIOFixedRate(Runnable task, long initialDelay, long period) {
    return asyncIOFixedRate(task, initialDelay, period, TimeUnit.MILLISECONDS);
  }

  static IScheduledFuture<?> asyncIOFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) {
    return io().scheduleAtFixedRate(task, initialDelay, period, unit);
  }

  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */
  /* ******************************************************************************************************* */

  /**
   * 创建单线程的EventLoop
   */
  static Single newSingle(boolean daemon) {
    return newSingle(generateNamePrefix(), daemon);
  }

  /**
   * 创建单线程的EventLoop
   */
  static Single newSingle(String namePrefix, boolean daemon) {
    return new Single(namePrefix, daemon);
  }

  /**
   * 创建单线程的EventLoop
   */
  static Single newSingle(ThreadFactory factory) {
    return new Single(factory);
  }

  /**
   * 单线程调度
   */
  public class Single extends Impl {

    final AtomicReference<Thread> threadHolder = new AtomicReference<>();

    public Single(ThreadFactory factory) {
      super(Executors.newSingleThreadScheduledExecutor(factory));
      this.submit(() -> threadHolder.set(Thread.currentThread())).get();
    }

    public Single(String namePrefix, boolean daemon) {
      this(newThreadFactory(namePrefix, daemon));
    }

    public Thread getThread() {
      return threadHolder.get();
    }

    public boolean isInLoop() {
      return threadHolder.get() == Thread.currentThread();
    }

    /**
     * 在同一个线程执行
     */
    public IFuture<?> inLoop(Runnable task) {
      if (isInLoop()) {
        try {
          task.run();
          return IFuture.nothing();
        } catch (Throwable e) {
          return IFuture.wrapFail(e);
        }
      } else {
        return submit(task);
      }
    }

    /**
     * 在同一个线程执行
     */
    public <V> IFuture<V> inLoop(Callable<V> task) {
      if (isInLoop()) {
        try {
          return IFuture.wrapValue(task.call());
        } catch (Throwable e) {
          return IFuture.wrapFail(e);
        }
      } else {
        return submit(task);
      }
    }

  }

}
