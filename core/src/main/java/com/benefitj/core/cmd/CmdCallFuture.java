package com.benefitj.core.cmd;

import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

/**
 * cmd call task
 */
public class CmdCallFuture implements Runnable {

  /**
   * 原命令调用对象
   */
  private final CmdCall raw;
  /**
   * Future
   */
  private ScheduledFuture<?> sf;
  /**
   * 回调
   */
  private final BiConsumer<String, CmdCallFuture> callback;

  public CmdCallFuture(CmdCall raw, BiConsumer<String, CmdCallFuture> callback) {
    this.raw = raw;
    this.callback = callback;
  }

  public CmdCall getRaw() {
    return raw;
  }

  public void setSf(ScheduledFuture<?> sf) {
    this.sf = sf;
  }

  public ScheduledFuture<?> getSf() {
    return sf;
  }

  public void cancel(boolean force) {
    getSf().cancel(force);
  }

  @Override
  public void run() {
    callback.accept(getRaw().getId(), this);
  }

}
