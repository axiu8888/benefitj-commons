package com.benefitj.core.cmd;

/**
 * 销毁的监听
 */
public interface DestroyListener {
  /**
   * 销毁时
   *
   * @param process 进程
   * @param call    命令调用
   */
  void onDestroy(Process process, CmdCall call);

  /**
   * 当被取消时
   *
   * @param process 进程
   * @param call    命令调用
   * @param timeout 是否超时
   */
  void onCancel(Process process, CmdCall call, boolean timeout);

  /**
   * 监听
   */
  final DestroyListener DISCARD = new DestroyListener() {
    @Override
    public void onDestroy(Process process, CmdCall call) {
      /*do nothing*/
    }

    @Override
    public void onCancel(Process process, CmdCall call, boolean timeout) {
      /*do nothing*/
    }
  };

}
