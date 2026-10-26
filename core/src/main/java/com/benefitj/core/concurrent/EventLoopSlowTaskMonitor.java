package com.hsrg.core.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * {@link EventLoopBinder} 慢任务监控实现：按 Binder 实例独立开关/阈值，执行中采样调用栈。
 * <p>
 * 看门狗线程共享；是否告警、阈值以各 {@link EventLoopBinder} 实例配置为准。
 */
final class EventLoopSlowTaskMonitor {

  private static final Logger log = LoggerFactory.getLogger(EventLoopSlowTaskMonitor.class);

  /**
   * 看门狗采样间隔（毫秒），全局共享
   */
  static volatile long SAMPLE_INTERVAL_MS = Long.getLong("eventloop.binder.slow.sampleIntervalMs", 500L);

  /**
   * 单次任务最多采样次数
   */
  static volatile int MAX_SAMPLES_PER_TASK = Integer.getInteger("eventloop.binder.slow.maxSamples", 3);

  /**
   * 打印栈帧上限
   */
  static volatile int MAX_STACK_DEPTH = Integer.getInteger("eventloop.binder.slow.stackDepth", 48);

  private static final AtomicLong TASK_SEQ = new AtomicLong();
  private static final Map<Long, RunningTask> RUNNING = new ConcurrentHashMap<>();
  private static final AtomicBoolean WATCHDOG_STARTED = new AtomicBoolean(false);

  private EventLoopSlowTaskMonitor() {
  }

  static void run(EventLoopBinder<?> binder, Runnable task, String taskName) {
    ensureWatchdog();
    long id = TASK_SEQ.incrementAndGet();
    RunningTask running = new RunningTask(id, binder, Thread.currentThread(), taskName, System.nanoTime());
    RUNNING.put(id, running);
    try {
      task.run();
    } finally {
      finish(running);
    }
  }

  static <T> T call(EventLoopBinder<?> binder, Callable<T> task, String taskName) throws Exception {
    ensureWatchdog();
    long id = TASK_SEQ.incrementAndGet();
    RunningTask running = new RunningTask(id, binder, Thread.currentThread(), taskName, System.nanoTime());
    RUNNING.put(id, running);
    try {
      return task.call();
    } finally {
      finish(running);
    }
  }

  private static void finish(RunningTask running) {
    running.done = true;
    RUNNING.remove(running.id);
    if (!running.binder.isSlowMonitorEnabled()) {
      return;
    }
    long costMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - running.startNanos);
    long threshold = running.binder.getSlowMonitorThresholdMs();
    if (costMs >= threshold) {
      log.warn("EventLoopBinder 慢任务结束: cost={}ms, threshold={}ms, binder={}, thread={}, task={}, samples={}, 请勿在 Binder EventLoop 上执行耗时/阻塞操作",
          costMs, threshold, binderLabel(running.binder), running.thread.getName(), running.taskName, running.sampleCount);
    }
  }

  private static void ensureWatchdog() {
    if (!WATCHDOG_STARTED.compareAndSet(false, true)) {
      return;
    }
    long interval = Math.max(SAMPLE_INTERVAL_MS, 100L);
    ThreadFactory factory = r -> {
      Thread t = new Thread(r, "eventloop-binder-slow-watchdog");
      t.setDaemon(true);
      return t;
    };
    ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(factory);
    scheduler.scheduleWithFixedDelay(EventLoopSlowTaskMonitor::sample, interval, interval, TimeUnit.MILLISECONDS);
  }

  private static void sample() {
    if (RUNNING.isEmpty()) {
      return;
    }
    long now = System.nanoTime();
    for (RunningTask task : RUNNING.values()) {
      if (task.done || !task.binder.isSlowMonitorEnabled() || task.sampleCount >= MAX_SAMPLES_PER_TASK) {
        continue;
      }
      long thresholdMs = Math.max(task.binder.getSlowMonitorThresholdMs(), 1L);
      long elapsedNanos = now - task.startNanos;
      if (elapsedNanos < TimeUnit.MILLISECONDS.toNanos(thresholdMs)) {
        continue;
      }
      task.sampleCount++;
      long costMs = TimeUnit.NANOSECONDS.toMillis(elapsedNanos);
      String stack = formatStack(task.thread.getStackTrace());
      log.warn("EventLoopBinder 慢任务执行中(采样#{}/{}): cost={}ms, threshold={}ms, state={}, binder={}, thread={}, task={}, 请勿在 Binder EventLoop 上执行耗时/阻塞操作\n{}",
          task.sampleCount, MAX_SAMPLES_PER_TASK, costMs, thresholdMs, task.thread.getState(),
          binderLabel(task.binder), task.thread.getName(), task.taskName, stack);
    }
  }

  private static String binderLabel(EventLoopBinder<?> binder) {
    String prefix = binder.getNamePrefix();
    return prefix == null || prefix.isEmpty() ? binder.toString() : prefix;
  }

  private static String formatStack(StackTraceElement[] stack) {
    if (stack == null || stack.length == 0) {
      return "\t<empty stack>";
    }
    StringBuilder sb = new StringBuilder(Math.min(stack.length, MAX_STACK_DEPTH) * 80);
    int printed = 0;
    for (StackTraceElement el : stack) {
      String cn = el.getClassName();
      if (cn.startsWith(EventLoopSlowTaskMonitor.class.getName())
          || cn.startsWith(EventLoopBinder.BoundLoop.class.getName())) {
        continue;
      }
      sb.append("\tat ").append(el).append('\n');
      if (++printed >= MAX_STACK_DEPTH) {
        int remain = stack.length - printed;
        if (remain > 0) {
          sb.append("\t... ").append(remain).append(" more");
        }
        break;
      }
    }
    return printed == 0 ? "\t<no frames>" : sb.toString();
  }

  static final class RunningTask {
    final long id;
    final EventLoopBinder<?> binder;
    final Thread thread;
    final String taskName;
    final long startNanos;
    volatile boolean done;
    volatile int sampleCount;

    RunningTask(long id, EventLoopBinder<?> binder, Thread thread, String taskName, long startNanos) {
      this.id = id;
      this.binder = binder;
      this.thread = thread;
      this.taskName = taskName;
      this.startNanos = startNanos;
    }
  }
}
