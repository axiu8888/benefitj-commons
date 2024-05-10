package com.benefitj.core.cmd;

import com.benefitj.core.Utils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
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
  private int exitCode = -1;
  /**
   * 结果信息
   */
  private String message = "";
  /**
   * 错误信息
   */
  private String error = "";
  /**
   * 异常
   */
  private Throwable exception;

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

  public int getExitCode() {
    return exitCode;
  }

  public void setExitCode(int exitCode) {
    this.exitCode = exitCode;
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

  public Throwable getException() {
    return exception;
  }

  public void setException(Throwable exception) {
    this.exception = exception;
  }

  public boolean isSuccessful() {
    return getExitCode() == 0 && (getError() == null || getError().trim().isEmpty());
  }

  /**
   * 打印
   *
   * @param tag 标记
   */
  public String toPrintInfo(String tag, @Nullable File dir) {
    return toPrintInfo(this, tag, dir);
  }

  /**
   * 打印
   *
   * @param call 调用对象
   * @param tag  标记
   */
  public static String toPrintInfo(CmdCall call, String tag, File dir) {
    StringBuilder sb = new StringBuilder();
    sb.append("\n\n----------------- cmd[" + tag + "] -----------------");
    sb.append("\ncmd: " + call.getCmd());
    if (dir != null) {
      sb.append("\ndir: " + dir.getAbsolutePath());
    }
    if (call.getEnvp() != null) {
      sb.append("\nenvp: " + String.join(", ", call.getEnvp()));
    }
    sb.append("\nexitCode: " + call.getExitCode());
    sb.append("\nsuccessful: " + call.isSuccessful());
    if (call.getMessage().endsWith("\n")) {
      call.setMessage(call.getMessage().substring(0, call.getMessage().length() - 1));
    }
    if (call.getError().endsWith("\n")) {
      call.setError(call.getError().substring(0, call.getError().length() - 1));
    }
    sb.append("\nmessage: " + call.getMessage());
    sb.append("\nerror: " + call.getError());
    sb.append("\nexception: " + call.getException());
    sb.append("\n----------------- cmd[" + tag + "] -----------------\n\n");
    return sb.toString();
  }


}
