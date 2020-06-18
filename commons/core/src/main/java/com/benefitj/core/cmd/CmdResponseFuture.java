package com.benefitj.core.cmd;

import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

/**
 * CmdResponse
 */
public class CmdResponseFuture implements Runnable {

  /**
   * 原响应对象
   */
  private final CmdResponse original;
  /**
   * Future
   */
  private ScheduledFuture<?> future;
  /**
   * 回调
   */
  private final BiConsumer<String, CmdResponseFuture> callback;

  public CmdResponseFuture(CmdResponse original, BiConsumer<String, CmdResponseFuture> callback) {
    this.original = original;
    this.callback = callback;
  }

  public CmdResponse getOriginal() {
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
