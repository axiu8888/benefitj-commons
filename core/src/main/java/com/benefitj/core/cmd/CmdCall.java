package com.benefitj.core.cmd;

import java.io.File;

/**
 * 调用命令行后的响应
 */
public class CmdCall {

  /**
   * CMD唯一ID
   */
  private String id;
  /**
   * 命令行
   */
  private String cmd;
  /**
   * 环境参数
   */
  private String[] envp;
  /**
   * 上下文目录
   */
  private File ctxDir;
  /**
   * 处理程序
   */
  private Process process;
  /**
   * 结果码
   */
  private int code = -1;
  /**
   * 结果信息
   */
  private String message;
  /**
   * 错误信息
   */
  private String error;

  public CmdCall() {
  }

  public CmdCall(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCmd() {
    return cmd;
  }

  public void setCmd(String cmd) {
    this.cmd = cmd;
  }

  public String[] getEnvp() {
    return envp;
  }

  public void setEnvp(String[] envp) {
    this.envp = envp;
  }

  public File getCtxDir() {
    return ctxDir;
  }

  public void setCtxDir(File ctxDir) {
    this.ctxDir = ctxDir;
  }

  public Process getProcess() {
    return process;
  }

  public void setProcess(Process process) {
    this.process = process;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public boolean isSuccessful() {
    return getCode() == 0 && (getError() == null || getError().trim().isEmpty());
  }

  /**
   * 打印
   *
   * @param tag 标记
   */
  public void print(String tag) {
    print(this, tag);
  }

  /**
   * 打印
   *
   * @param call 调用对象
   * @param tag  标记
   */
  public static void print(CmdCall call, String tag) {
    System.err.println("\n----------------- cmd[" + tag + "] -----------------");
    System.err.println("successful: " + call.isSuccessful());
    if (call.getMessage().endsWith("\n")) {
      call.setMessage(call.getMessage().substring(0, call.getMessage().length() - 1));
    }
    if (call.getError().endsWith("\n")) {
      call.setError(call.getError().substring(0, call.getError().length() - 1));
    }
    System.err.println("message: " + call.getMessage());
    System.err.println("error: " + call.getError());
    System.err.println("cmd: " + call.getCmd());
    System.err.println("exitCode: " + call.getCode());
    System.err.println("----------------- cmd[" + tag + "] -----------------\n");
  }
}
