package com.benefitj.core.cmd;

/**
 * 命令行执行器
 */
public final class CmdExecutorHolder {

  public static CmdExecutor getInstance() {
    return Instance.INSTANCE;
  }

  private static final class Instance {
    private static final CmdExecutor INSTANCE;
    static {
      INSTANCE = new CmdExecutor();
    }
  }

}
