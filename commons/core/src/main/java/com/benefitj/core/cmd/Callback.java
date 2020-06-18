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
   * @param response 响应
   */
  default void onStart(CmdResponse response) {
    // do nothing
  }

  /**
   * 调用 {@link Runtime#exec(String, String[], File)} 之前
   *
   * @param response 响应
   * @param command  命令
   * @param envp     环境变量参数
   * @param dir      目录
   */
  default void onCallBefore(CmdResponse response, String command, String[] envp, File dir) {
    // do nothing
  }

  /**
   * 调用 {@link Runtime#exec(String, String[], File)} 之后
   *
   * @param process  进程
   * @param response 响应
   */
  default void onCallAfter(Process process, CmdResponse response) {
    // do nothing
  }

  /**
   * 调用 {@link Process#waitFor(long, TimeUnit)} 之前
   *
   * @param process  进程
   * @param response 响应
   */
  default void onWaitForBefore(Process process, CmdResponse response) {
    // do nothing
  }

  /**
   * 调用 {@link Process#waitFor(long, TimeUnit)} 之前
   *
   * @param process  进程
   * @param response 响应
   */
  default void onWaitForAfter(Process process, CmdResponse response) {
    // do nothing
  }

  /**
   * 结束调用
   *
   * @param response 响应
   */
  default void onFinish(CmdResponse response) {
    // do nothing
  }

  final Callback EMPTY_CALLBACK = new Callback() {
    // do nothing
  };

}
