package com.benefitj.netty.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的日志实现
 */
public class Log4jNettyLogger implements INettyLogger {

  private final Logger log = LoggerFactory.getLogger(Log4jNettyLogger.class);

  @Override
  public void debug(String msg) {
    log.debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    log.debug(format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    log.debug(format, arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    log.debug(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    log.debug(msg, t);
  }

  @Override
  public void info(String msg) {
    log.info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    log.info(format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    log.info(format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    log.info(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    log.info(msg, t);
  }

  @Override
  public void warn(String msg) {
    log.warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    log.warn(format, arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    log.warn(format, arguments);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    log.warn(format, arg1, arg2);
  }

  @Override
  public void warn(String msg, Throwable t) {
    log.warn(msg, t);
  }

  @Override
  public void error(String msg) {
    log.warn(msg);
  }

  @Override
  public void error(String format, Object arg) {
    log.error(format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
  }

  @Override
  public void error(String format, Object... arguments) {
    log.error(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    log.error(msg, t);
  }
}
