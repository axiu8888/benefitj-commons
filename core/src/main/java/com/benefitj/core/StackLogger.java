package com.benefitj.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.lang.reflect.Field;

@Deprecated
public class StackLogger implements Logger {
  public static Logger getLogger() {
    return new StackLogger(LoggerFactory.getLogger("StackLogger"));
  }

  private final Logger log;

  private Field nameField;

  public StackLogger(Logger log) {
    this.log = log;
    this.nameField = ReflectUtils.getField(log.getClass(), "name");
  }

  @Override
  public String getName() {
    return log.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return log.isTraceEnabled();
  }

  private void execute(Runnable r) {
    if (nameField != null) {
      synchronized (this) {
        try {
          StackTraceElement stack = StackUtils.get(4);
          String methodName = stack.getMethodName();
          ReflectUtils.setFieldValue(nameField, log, String.format("%s.%s(%d)"
              , StackUtils.getSimpleClassName(stack)
              , methodName.startsWith("lambda") ? "lambda" : methodName
              , stack.getLineNumber()
          ));
          r.run();
        } finally {
          ReflectUtils.setFieldValue(nameField, log, "StackLogger");
        }
      }
    } else {
      r.run();
    }
  }

  @Override
  public void trace(String msg) {
    execute(() -> log.trace(msg));
  }

  @Override
  public void trace(String format, Object arg) {
    execute(() -> log.trace(format, arg));
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    execute(() -> log.trace(format, arg1, arg2));
  }

  @Override
  public void trace(String format, Object... arguments) {
    execute(() -> log.trace(format, arguments));
  }

  @Override
  public void trace(String msg, Throwable t) {
    execute(() -> log.trace(msg, t));
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return log.isTraceEnabled(marker);
  }

  @Override
  public void trace(Marker marker, String msg) {
    execute(() -> log.trace(marker, msg));
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    execute(() -> log.trace(marker, format, arg));
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    execute(() -> log.trace(marker, format, arg1, arg2));
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    execute(() -> log.trace(marker, format, argArray));
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    execute(() -> log.trace(marker, msg, t));
  }

  @Override
  public boolean isDebugEnabled() {
    return log.isDebugEnabled();
  }

  @Override
  public void debug(String msg) {
    execute(() -> log.debug(msg));
  }

  @Override
  public void debug(String format, Object arg) {
    execute(() -> log.debug(format, arg));
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    execute(() -> log.debug(format, arg1, arg2));
  }

  @Override
  public void debug(String format, Object... arguments) {
    execute(() -> log.debug(format, arguments));
  }

  @Override
  public void debug(String msg, Throwable t) {
    execute(() -> log.debug(msg, t));
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return log.isDebugEnabled(marker);
  }

  @Override
  public void debug(Marker marker, String msg) {
    execute(() -> log.debug(marker, msg));
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    execute(() -> log.debug(marker, format, arg));
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    execute(() -> log.debug(marker, format, arg1, arg2));
  }

  @Override
  public void debug(Marker marker, String format, Object... argArray) {
    execute(() -> log.debug(marker, format, argArray));
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    execute(() -> log.debug(marker, msg, t));
  }

  @Override
  public boolean isInfoEnabled() {
    return log.isInfoEnabled();
  }

  @Override
  public void info(String msg) {
    execute(() -> log.info(msg));
  }

  @Override
  public void info(String format, Object arg) {
    execute(() -> log.info(format, arg));
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    execute(() -> log.info(format, arg1, arg2));
  }

  @Override
  public void info(String format, Object... arguments) {
    execute(() -> log.info(format, arguments));
  }

  @Override
  public void info(String msg, Throwable t) {
    execute(() -> log.info(msg, t));
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return log.isInfoEnabled(marker);
  }

  @Override
  public void info(Marker marker, String msg) {
    execute(() -> log.info(marker, msg));
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    execute(() -> log.info(marker, format, arg));
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    execute(() -> log.info(marker, format, arg1, arg2));
  }

  @Override
  public void info(Marker marker, String format, Object... argArray) {
    execute(() -> log.info(marker, format, argArray));
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    execute(() -> log.info(marker, msg, t));
  }

  @Override
  public boolean isWarnEnabled() {
    return log.isWarnEnabled();
  }

  @Override
  public void warn(String msg) {
    execute(() -> log.warn(msg));
  }

  @Override
  public void warn(String format, Object arg) {
    execute(() -> log.warn(format, arg));
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    execute(() -> log.warn(format, arg1, arg2));
  }

  @Override
  public void warn(String format, Object... arguments) {
    execute(() -> log.warn(format, arguments));
  }

  @Override
  public void warn(String msg, Throwable t) {
    execute(() -> log.warn(msg, t));
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return log.isWarnEnabled(marker);
  }

  @Override
  public void warn(Marker marker, String msg) {
    execute(() -> log.warn(marker, msg));
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    execute(() -> log.warn(marker, format, arg));
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    execute(() -> log.warn(marker, format, arg1, arg2));
  }

  @Override
  public void warn(Marker marker, String format, Object... argArray) {
    execute(() -> log.warn(marker, format, argArray));
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    execute(() -> log.warn(marker, msg, t));
  }

  @Override
  public boolean isErrorEnabled() {
    return log.isErrorEnabled();
  }

  @Override
  public void error(String msg) {
    execute(() -> log.error(msg));
  }

  @Override
  public void error(String format, Object arg) {
    execute(() -> log.error(format, arg));
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    execute(() -> log.error(format, arg1, arg2));
  }

  @Override
  public void error(String format, Object... arguments) {
    execute(() -> log.error(format, arguments));
  }

  @Override
  public void error(String msg, Throwable t) {
    execute(() -> log.error(msg, t));
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return log.isErrorEnabled(marker);
  }

  @Override
  public void error(Marker marker, String msg) {
    execute(() -> log.error(marker, msg));
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    execute(() -> log.error(marker, format, arg));
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    execute(() -> log.error(marker, format, arg1, arg2));
  }

  @Override
  public void error(Marker marker, String format, Object... argArray) {
    execute(() -> log.error(marker, format, argArray));
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    execute(() -> log.error(marker, msg, t));
  }
}
