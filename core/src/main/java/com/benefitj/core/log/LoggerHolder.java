package com.benefitj.core.log;

public class LoggerHolder implements ILogger {

  volatile ILogger log;

  public LoggerHolder(ILogger log) {
    this.log = log;
  }

  public ILogger getLog() {
    return log;
  }

  public void setLog(ILogger log) {
    this.log = log;
  }

  @Override
  public void trace(String format) {
    getLog().trace(format);
  }

  @Override
  public void trace(String format, Object... arguments) {
    getLog().trace(format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    getLog().trace(msg, t);
  }

  @Override
  public void debug(String format) {
    getLog().debug(format);
  }

  @Override
  public void debug(String format, Object... arguments) {
    getLog().debug(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    getLog().debug(msg, t);
  }

  @Override
  public void info(String format) {
    getLog().info(format);
  }

  @Override
  public void info(String format, Object... arguments) {
    getLog().info(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    getLog().info(msg, t);
  }

  @Override
  public void warn(String format) {
    getLog().warn(format);
  }

  @Override
  public void warn(String format, Object... arguments) {
    getLog().warn(format, arguments);
  }

  @Override
  public void warn(String msg, Throwable t) {
    getLog().warn(msg, t);
  }

  @Override
  public void error(String format) {
    getLog().error(format);
  }

  @Override
  public void error(String format, Object... arguments) {
    getLog().error(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    getLog().error(msg, t);
  }
}
