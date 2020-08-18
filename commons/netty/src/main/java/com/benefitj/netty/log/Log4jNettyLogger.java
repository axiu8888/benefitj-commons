package com.benefitj.netty.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 默认的日志实现
 */
public class Log4jNettyLogger implements INettyLogger {

  private final Logger logger;

  public Log4jNettyLogger() {
    this(Log4jNettyLogger.class);
  }

  public Log4jNettyLogger(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  public Log4jNettyLogger(String name) {
    this.logger = LoggerFactory.getLogger(name);
  }

  public Logger logger() {
    return logger;
  }

  @Override
  public void debug(String msg) {
    logger().debug(msg);
  }

  @Override
  public void debug(String format, Object arg) {
    logger().debug(format, arg);
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    logger().debug(format, arg1, arg2);
  }

  @Override
  public void debug(String format, Object... arguments) {
    logger().debug(format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    logger().debug(msg, t);
  }

  @Override
  public void info(String msg) {
    logger().info(msg);
  }

  @Override
  public void info(String format, Object arg) {
    logger().info(format, arg);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    logger().info(format, arg1, arg2);
  }

  @Override
  public void info(String format, Object... arguments) {
    logger().info(format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    logger().info(msg, t);
  }

  @Override
  public void warn(String msg) {
    logger().warn(msg);
  }

  @Override
  public void warn(String format, Object arg) {
    logger().warn(format, arg);
  }

  @Override
  public void warn(String format, Object... arguments) {
    logger().warn(format, arguments);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    logger().warn(format, arg1, arg2);
  }

  @Override
  public void warn(String msg, Throwable t) {
    logger().warn(msg, t);
  }

  @Override
  public void error(String msg) {
    logger().warn(msg);
  }

  @Override
  public void error(String format, Object arg) {
    logger().error(format, arg);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
  }

  @Override
  public void error(String format, Object... arguments) {
    logger().error(format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    logger().error(msg, t);
  }
}
