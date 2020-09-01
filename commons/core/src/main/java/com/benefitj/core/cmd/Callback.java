package com.benefitj.core.cmd;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * 回调
 */
public interface Callback {
  /**
   * 调用之前
   *
   * @param call 调用
   */
  default void onStart(CmdCall call) {
    // do nothing
  }

  /**
   * 调用 {@link Runtime#exec(String, String[], File)} 之前
   *
   * @param call    调用
   * @param command 命令
   * @param envp    环境变量参数
   * @param dir     目录
   */
  default void onCallBefore(CmdCall call, String command, String[] envp, File dir) {
    // do nothing
  }

  /**
   * 调用 {@link Runtime#exec(String, String[], File)} 之后
   *
   * @param process 进程
   * @param call    调用
   */
  default void onCallAfter(Process process, CmdCall call) {
    // do nothing
  }

  /**
   * 调用 {@link Process#waitFor(long, TimeUnit)} 之前
   *
   * @param process 进程
   * @param call    调用
   */
  default void onWaitForBefore(Process process, CmdCall call) {
    // do nothing
  }

  /**
   * 调用 {@link Process#waitFor(long, TimeUnit)} 之前
   *
   * @param process 进程
   * @param call    调用
   */
  default void onWaitForAfter(Process process, CmdCall call) {
    // do nothing
  }

  /**
   * 结束调用
   *
   * @param call 调用
   */
  default void onFinish(CmdCall call) {
    // do nothing
  }

  final Callback EMPTY_CALLBACK = new Callback() {
    // do nothing
  };

}
