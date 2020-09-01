package com.benefitj.core.cmd;

/**
 * 销毁的监听
 */
public interface DestroyListener {
  /**
   * 销毁时
   *
   * @param process  进程
   * @param response 响应
   */
  void onDestroy(Process process, CmdCall response);

  /**
   * 当被取消时
   *
   * @param process  进程
   * @param response 响应
   * @param timeout  是否超时
   */
  void onCancel(Process process, CmdCall response, boolean timeout);

  /**
   * 监听
   */
  final DestroyListener DISCARD = new DestroyListener() {
    @Override
    public void onDestroy(Process process, CmdCall response) {
      /*do nothing*/
    }

    @Override
    public void onCancel(Process process, CmdCall response, boolean timeout) {
      /*do nothing*/
    }
  };

}
