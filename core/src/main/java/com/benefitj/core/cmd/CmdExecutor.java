package com.benefitj.core.cmd;

import com.benefitj.core.*;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CMD执行(bat/shell)
 */
public class CmdExecutor {

  static final SingletonSupplier<CmdExecutor> single = SingletonSupplier.of(CmdExecutor::new);

  public static CmdExecutor get() {
    return single.get();
  }

  /**
   * 是否为windows
   */
  public static final String CRLF;

  static {
    CRLF = SystemOS.LOCALE.isWindows() ? "\r\n" : "\n";
  }

  /**
   * 判断是否为Windows
   */
  public static boolean isWindows() {
    return SystemOS.LOCALE.isWindows();
  }

  /**
   * 添加命令头
   *
   * @param cmd 命令
   * @return 返回新的命令
   */
  public static String cmdPrefix(String cmd) {
    return (isWindows() ? "cmd /c start /b " : "sh ") + cmd;
  }

  private final AtomicReference<Thread> sign = new AtomicReference<>();
  private final AtomicInteger aliveProcess = new AtomicInteger();

  private final Object lock = new Object();
  /**
   * 超时时长，5分钟
   */
  private long timeout = 300_000;
  /**
   * 最大子进程数
   */
  private volatile int maxCallNum = 20;
  /**
   * 调度器
   */
  private ScheduledExecutorService executor = EventLoop.io();
  /**
   * 等待中的执行命令
   */
  private final Map<String, CmdCallTask> waitForFutures = new ConcurrentHashMap<>();
  /**
   * 销毁的监听
   */
  private DestroyListener destroyListener = DestroyListener.DISCARD;

  public CmdExecutor() {
  }

  public DestroyListener getDestroyListener() {
    return destroyListener;
  }

  public void setDestroyListener(DestroyListener destroyListener) {
    this.destroyListener = (destroyListener != null
        ? destroyListener : DestroyListener.DISCARD);
  }

  /**
   * 调度器
   */
  public ScheduledExecutorService getExecutor() {
    return this.executor;
  }

  /**
   * Creates and executes a one-shot action that becomes enabled
   * after the given delay.
   *
   * @param command the task to execute
   * @param delay   the time from now to delay execution
   * @param unit    the time unit of the delay parameter
   * @return a ScheduledFuture representing pending completion of
   * the task and whose {@code get()} method will return
   * {@code null} upon completion
   */
  public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
    return getExecutor().schedule(command, delay, unit);
  }

  /**
   * 获取等待中的进程
   */
  protected Map<String, CmdCallTask> getWaitForFutures() {
    return waitForFutures;
  }

  /**
   * 是否处于等待中
   *
   * @param processId 进程ID
   * @return 是否等待
   */
  public boolean isWaitingFor(String processId) {
    return waitForFutures.containsKey(processId);
  }

  /**
   * 调用命令
   *
   * @param cmd 命令
   * @return 返回结果的值
   */
  public CmdCall call(String cmd) {
    return call(cmd, null, null, 0);
  }

  /**
   * 调用命令
   *
   * @param cmd     命令
   * @param timeout 超时时长
   * @return 返回结果的值
   */
  public CmdCall call(String cmd, long timeout) {
    return call(cmd, null, null, timeout);
  }

  /**
   * 调用命令
   *
   * @param cmd  命令
   * @param envp 环境变量
   * @param dir  上下文目录
   * @return 返回结果的值
   */
  public CmdCall call(String cmd, @Nullable List<String> envp, @Nullable File dir) {
    return call(cmd, envp, dir, 0);
  }

  /**
   * 调用命令
   *
   * @param cmd     命令
   * @param envp    环境变量
   * @param dir     上下文目录
   * @param timeout 超时时长
   * @return 执行命令后的响应
   */
  public CmdCall call(String cmd, @Nullable List<String> envp, @Nullable File dir, long timeout) {
    return call(cmd, envp, dir, timeout, null);
  }

  /**
   * 调用命令
   *
   * @param cmd      命令
   * @param timeout  超时时长
   * @param callback 回调
   * @return 执行命令后的响应
   */
  public CmdCall call(String cmd, long timeout, @Nullable Callback callback) {
    return call(cmd, null, null, timeout, callback);
  }

  /**
   * 调用命令
   *
   * @param cmd      命令
   * @param envp     环境变量
   * @param dir      上下文目录
   * @param timeout  超时时长
   * @param callback 回调
   * @return 执行命令后的响应
   */
  public CmdCall call(String cmd, @Nullable List<String> envp, @Nullable File dir, long timeout, @Nullable Callback callback) {
    final Callback cb = callback != null ? callback : Callback.EMPTY_CALLBACK;
    final long start = now();
    final String[] envparams = envp != null ? envp.toArray(new String[0]) : new String[0];
    final CmdCall call = createCmdCall(IdUtils.uuid());
    call.setCmd(cmd);
    call.setCtxDir(dir);
    call.setEnvp(envparams);
    cb.onStart(call);
    try {
      return safeCall(timeout, start, () -> {
        cb.onCallBefore(call, cmd, envparams, dir);
        final Process process = Runtime.getRuntime().exec(cmd, envparams, dir);
        call.setProcess(process);
        cb.onCallAfter(process, call);
        // 强制结束
        scheduleTimeout(call, timeout - (now() - start));
        cb.onWaitForBefore(process, call);
        // 处理消息
        handle(getExecutor(), process, call, cb);
        // 等待
        //int exitValue = process.waitFor();
        call.setCode(process.exitValue());
        // 移除等待的缓存
        cancelTimeoutSchedule(call.getId());
        // 调用结束
        cb.onWaitForAfter(process, call);
        return call;
      });
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      cb.onFinish(call);
    }
  }


  /**
   * 取消超时时长的调度
   *
   * @param id 进程ID
   */
  protected void cancelTimeoutSchedule(String id) {
    final CmdCallTask task = getWaitForFutures().remove(id);
    if (task != null) {
      task.cancel(true);
      CmdCall call = task.getOriginal();
      getDestroyListener().onCancel(call.getProcess(), call, true);
    }
  }

  /**
   * 延迟结束调用
   *
   * @param call    CMD响应
   * @param timeout 超时时长
   */
  protected void scheduleTimeout(CmdCall call, long timeout) {
    timeout = timeout > 0 ? timeout : getTimeout();
    final CmdCallTask task = new CmdCallTask(call, (id, f) -> {
      getWaitForFutures().remove(id);
      // 唤醒等待的线程
      lock(Object::notify);
      final CmdCall cr = f.getOriginal();
      final Process p = cr.getProcess();
      if (p != null) {
        p.destroyForcibly();
        getDestroyListener().onDestroy(p, cr);
      }
    });
    getWaitForFutures().put(call.getId(), task);
    ScheduledFuture<?> future = schedule(task, timeout, TimeUnit.MILLISECONDS);
    task.setFuture(future);
  }

  /**
   * 加锁
   *
   * @param timeout 超时时长
   * @param start   开始时间
   * @return 返回是否加锁
   */
  private <V> V safeCall(long timeout, long start, Callable<V> call) throws Exception {
    if (timeout <= 0) {
      return call.call();
    }
    final AtomicReference<Thread> sign = this.sign;
    int maxProcess = getMaxCallNum();
    final Thread current = Thread.currentThread();
    for (;;) {
      if (sign.compareAndSet(null, current)) {
        // 执行的命令未达到最大值
        if (aliveProcess.get() < maxProcess) {
          try {
            aliveProcess.incrementAndGet();
            // 释放锁
            sign.set(null);
            return call.call();
          } finally {
            aliveProcess.decrementAndGet();
            // 唤醒等待的线程
            lock(Object::notify);
          }
        }
      }

      if ((timeout - (now() - start)) > 0) {
        lock((lock) -> {
          sign.set(null);
          // 等待时长
          sign.wait(Math.max(timeout - (now() - start), 0));
        });
      }
      // 判断超时状态
      if (isTimeout(start, timeout)) {
        // 超时了，结束操作
        throw new TimeoutException("等待超时！");
      }
    }
  }


  protected final void lock(LockObserver observer) {
    synchronized (lock) {
      try {
        observer.accept(lock);
      } catch (InterruptedException e) {
        throw new IllegalStateException(e);
      }
    }
  }

  public CmdCall createCmdCall(String id) {
    return new CmdCall(id);
  }


  public long getTimeout() {
    return timeout;
  }

  public void setTimeout(long timeout) {
    this.timeout = timeout;
  }

  public int getMaxCallNum() {
    return maxCallNum;
  }

  public void setMaxCallNum(int maxCallNum) {
    this.maxCallNum = Math.min(maxCallNum, 1);
  }

  public int getAliveProcess() {
    return aliveProcess.get();
  }

  protected static boolean isTimeout(long start, long timeout) {
    return timeout > 0 && ((now() - start) >= timeout);
  }


  protected static long now() {
    return System.currentTimeMillis();
  }


  interface LockObserver {
    /**
     * 加锁中的处理
     *
     * @param lock 锁
     */
    void accept(Object lock) throws InterruptedException;
  }

  /**
   * 处理进程
   *
   * @param process 进程
   * @param cb      回调
   */
  public static void handle(Process process, Callback cb) {
    handle(EventLoop.io(), process, new CmdCall(IdUtils.uuid()), cb);
  }

  /**
   * 处理进程
   *
   * @param executor 线程池
   * @param process  进程
   * @param call     调用
   * @param cb       回调
   */
  public static void handle(Executor executor, Process process, CmdCall call, Callback cb) {
    try {
      // 读取消息
      Charset charset = Charset.forName(System.getProperty("sun.jnu.encoding"));
      try (BufferedReader pipeReader = IOUtils.wrapReader(process.getInputStream(), charset);
           BufferedReader errorReader = IOUtils.wrapReader(process.getErrorStream(), charset);) {
        List<String> msgLines = new LinkedList<>();
        List<String> errLines = new LinkedList<>();
        CountDownLatch latch = new CountDownLatch(2);
        executor.execute(() -> {
          try {
            IOUtils.readLines(pipeReader, line -> {
              msgLines.add(line);
              cb.onMessage(call, msgLines, line, false);
            });
          } finally {
            latch.countDown();
          }
        });
        executor.execute(() -> {
          try {
            IOUtils.readLines(errorReader, line -> {
              errLines.add(line);
              cb.onMessage(call, errLines, line, true);
            });
          } finally {
            latch.countDown();
          }
        });
        latch.await();
        call.setMessage(String.join(CRLF, msgLines));
        call.setError(String.join(CRLF, errLines));
      }
    } catch (InterruptedException e) {
      call.setCode(-1);
    } catch (IOException e) {
      call.setError(e.getMessage());
    }
  }
}
