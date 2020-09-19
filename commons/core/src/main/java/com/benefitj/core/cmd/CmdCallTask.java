package com.benefitj.core.cmd;

import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

/**
 * cmd call task
 */
public class CmdCallTask implements Runnable {

  /**
   * 原命令调用对象
   */
  private final CmdCall original;
  /**
   * Future
   */
  private ScheduledFuture<?> future;
  /**
   * 回调
   */
  private final BiConsumer<String, CmdCallTask> callback;

  public CmdCallTask(CmdCall original, BiConsumer<String, CmdCallTask> callback) {
    this.original = original;
    this.callback = callback;
  }

  public CmdCall getOriginal() {
    return original;
  }

  public void setFuture(ScheduledFuture<?> future) {
    this.future = future;
  }

  public ScheduledFuture<?> getFuture() {
    return future;
  }

  public void cancel(boolean force) {
    getFuture().cancel(force);
  }

  @Override
  public void run() {
    callback.accept(getOriginal().getId(), this);
  }

}
