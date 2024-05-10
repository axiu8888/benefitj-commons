package com.benefitj.core.cmd;

import java.io.File;
import java.util.List;
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
   * 调用 {@link Runtime#exec(String, String[], File)} 之前
   *
   * @param call  调用
   * @param lines 全局数据
   * @param line  接收的一行数据
   * @param error 是否为
   */
  default void onMessage(CmdCall call, List<String> lines, String line, boolean error) {
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
   * 出现异常
   *
   * @param call 调用
   * @param e    异常
   */
  default void onError(CmdCall call, Throwable e) {
    //e.printStackTrace();
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
