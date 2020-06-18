package com.benefitj.core.cmd;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CMD执行(bat/shell)
 */
public class CmdHelper {

  /**
   * 是否为windows
   */
  private static final boolean WINDOWS;
  public static final String CRLF;
  static {
    WINDOWS = System.getProperty("os.name").contains("Windows");
    CRLF = WINDOWS ? "\r\n" : "\n";
  }

  /**
   * 判断是否为Windows
   */
  public static boolean isWindows() {
    return WINDOWS;
  }

  private final AtomicReference<Object> sign = new AtomicReference<>();
  private final AtomicInteger aliveProcess = new AtomicInteger();

  private final Object lock = new Object();
  /**
   * 最大子进程数
   */
  private volatile int maxCallNum = 20;
  /**
   * 调度器
   */
  private volatile ScheduledExecutorService executor;
  /**
   * 等待中的执行命令
   */
  private final Map<String, CmdResponseFuture> waitForFutures = new ConcurrentHashMap<>();
  /**
   * 销毁的监听
   */
  private DestroyListener destroyListener = DestroyListener.EMPTY_LISTENER;

  public CmdHelper() {
  }

  public DestroyListener getDestroyListener() {
    return destroyListener;
  }

  public void setDestroyListener(DestroyListener destroyListener) {
    this.destroyListener = (destroyListener != null
        ? destroyListener : DestroyListener.EMPTY_LISTENER);
  }

  /**
   * 调度器
   */
  public ScheduledExecutorService getExecutor() {
    ScheduledExecutorService e = this.executor;
    if (e == null) {
      synchronized (this) {
        if ((e = this.executor) == null) {
          e = (this.executor = Executors.newScheduledThreadPool(1));
        }
      }
    }
    return e;
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
  protected Map<String, CmdResponseFuture> getWaitForFutures() {
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
  public CmdResponse call(String cmd) {
    return call(cmd, null, null, 0);
  }

  /**
   * 调用命令
   *
   * @param cmd     命令
   * @param timeout 超时时长
   * @return 返回结果的值
   */
  public CmdResponse call(String cmd, long timeout) {
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
  public CmdResponse call(String cmd, @Nullable List<String> envp, @Nullable File dir) {
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
  public CmdResponse call(String cmd, @Nullable List<String> envp, @Nullable File dir, long timeout) {
    return call(cmd, envp, dir, timeout, null);
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
  public CmdResponse call(String cmd, @Nullable List<String> envp, @Nullable File dir, long timeout, @Nullable Callback callback) {
    final Callback cb = callback != null ? callback : Callback.EMPTY_CALLBACK;
    final long start = now();
    final CmdResponse response = new CmdResponse(uuid());
    cb.onStart(response);
    try {
      return tryLockCall(timeout, start, () -> {
        String[] envparams = envp != null && !envp.isEmpty() ? envp.toArray(new String[0]) : null;
        response.setCmd(cmd);
        response.setCtxDir(dir);
        response.setEnvp(envparams);
        cb.onCallBefore(response, cmd, envparams, dir);
        final Process process = Runtime.getRuntime().exec(cmd, envparams, dir);
        response.setProcess(process);
        cb.onCallAfter(process, response);
        // 强制结束
        scheduleTimeout(response, timeout - (now() - start) + 5);
        cb.onWaitForBefore(process, response);
        try {
          // 等待
          int exitValue = process.waitFor();
          response.setCode(exitValue);
        } catch (InterruptedException e) {
          response.setCode(-1);
        }
        // 移除等待的缓存
        cancelTimeoutSchedule(response.getId());
        // 调用结束
        cb.onWaitForAfter(process, response);
        // 读取消息
        readMessage(process, response);

        return response;
      });
    } catch (Exception e) {
      throw new IllegalStateException(e);
    } finally {
      aliveProcess.decrementAndGet();
      // 唤醒等待的线程
      lock(Object::notify);
      cb.onFinish(response);
    }
  }

  private void readMessage(Process process, CmdResponse response) {
    try {
      Charset charset = Charset.defaultCharset();
      try (BufferedReader respBr = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
           BufferedReader errorBr = new BufferedReader(new InputStreamReader(process.getErrorStream(), charset));) {
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = respBr.readLine()) != null) {
          sb.append(line).append(CRLF);
        }
        response.setMessage(sb.toString());

        sb.setLength(0);
        while ((line = errorBr.readLine()) != null) {
          sb.append(line).append(CRLF);
        }
        response.setError(sb.toString());
      }
    } catch (IOException e) {
      response.setError(e.getMessage());
    }
  }

  /**
   * 取消超时时长的调度
   *
   * @param id 进程ID
   */
  protected void cancelTimeoutSchedule(String id) {
    final CmdResponseFuture crf = getWaitForFutures().remove(id);
    if (crf != null) {
      crf.cancel(true);
      CmdResponse response = crf.getOriginal();
      getDestroyListener().onCancel(response.getProcess(), response, true);
    }
  }

  /**
   * 延迟结束调用
   *
   * @param response CMD响应
   * @param timeout  超时时长
   */
  protected void scheduleTimeout(CmdResponse response, long timeout) {
    if (timeout > 0) {
      final CmdResponseFuture crf = new CmdResponseFuture(response, (id, f) -> {
        getWaitForFutures().remove(id);
        final CmdResponse cr = f.getOriginal();
        final Process p = cr.getProcess();
        if (p != null) {
          p.destroyForcibly();
          getDestroyListener().onDestroy(p, cr);
        }
        // 唤醒等待的线程
        lock(Object::notify);
      });
      getWaitForFutures().put(response.getId(), crf);
      ScheduledFuture<?> sf = schedule(crf, timeout, TimeUnit.MILLISECONDS);
      crf.setFuture(sf);
    }
  }

  /**
   * 加锁
   *
   * @param timeout 超时时长
   * @param start   开始时间
   * @return 返回是否加锁
   */
  private <V> V tryLockCall(long timeout, long start, Callable<V> call) throws Exception {
    final AtomicReference<Object> sign = this.sign;
    int maxProcess = getMaxCallNum();
    final Thread current = Thread.currentThread();
    for (;;) {
      if (sign.compareAndSet(null, current)) {
        // 执行的命令未达到最大值
        if (aliveProcess.get() < maxProcess) {
          aliveProcess.incrementAndGet();
          sign.set(null);
          return call.call();
        }
      }

      lock((lock) -> {
        sign.set(null);
        // 等待时长
        sign.wait(Math.max(timeout - (now() - start), 0));
      });
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


  public interface LockObserver {
    /**
     * 加锁中的处理
     *
     * @param lock 锁
     */
    void accept(Object lock) throws InterruptedException;
  }


  /**
   * 获取UUID
   */
  protected static String uuid() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
