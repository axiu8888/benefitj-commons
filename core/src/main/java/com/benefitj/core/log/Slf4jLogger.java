package com.benefitj.core.log;

import com.benefitj.core.ProxyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLogger implements ILogger {

  public static void setIfPassable(LoggerHolder holder, String name) {
    try {
      Class.forName("org.slf4j.LoggerFactory");
      holder.setLog(create(name));
    } catch (ClassNotFoundException ignore) {/*(0_0)*/}
  }

  public static Slf4jLogger create(String name) {
    return new Slf4jLogger(LoggerFactory.getLogger(name));
  }

  public static ILogger newProxy(Logger log, Slf4jLevel level) {
    return ProxyUtils.newProxy(ILogger.class, (proxy, method, args) -> {
      Class<?>[] parameterTypes = method.getParameterTypes();
      if (parameterTypes.length == 1) {
        level.print(log, (String) args[0]);
      } else if (parameterTypes[1] == Throwable.class) {
        level.print(log, (String) args[0], (Throwable) args[1]);
      } else {
        level.print(log, (String) args[0], (Object[]) args[1]);
      }
      return null;
    });
  }

  public static final Slf4jLogger NONE = new Slf4jLogger(LoggerFactory.getLogger("vertx"));

  final Logger log;

  public Slf4jLogger(Logger log) {
    this.log = log;
  }

  @Override
  public void trace(String msg) {
    Slf4jLevel.TRACE.print(log, msg);
  }

  @Override
  public void trace(String format, Object... arguments) {
    Slf4jLevel.TRACE.print(log, format, arguments);
  }

  @Override
  public void trace(String msg, Throwable t) {
    Slf4jLevel.TRACE.print(log, msg, t);
  }

  @Override
  public void debug(String format) {
    Slf4jLevel.DEBUG.print(log, format);
  }

  @Override
  public void debug(String format, Object... arguments) {
    Slf4jLevel.DEBUG.print(log, format, arguments);
  }

  @Override
  public void debug(String msg, Throwable t) {
    Slf4jLevel.DEBUG.print(log, msg, t);
  }

  @Override
  public void info(String format) {
    Slf4jLevel.INFO.print(log, format);
  }

  @Override
  public void info(String format, Object... arguments) {
    Slf4jLevel.INFO.print(log, format, arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    Slf4jLevel.INFO.print(log, msg, t);
  }

  @Override
  public void warn(String format) {
    Slf4jLevel.WARN.print(log, format);
  }

  @Override
  public void warn(String format, Object... arguments) {
    Slf4jLevel.WARN.print(log, format, arguments);
  }

  @Override
  public void warn(String msg, Throwable t) {
    Slf4jLevel.WARN.print(log, msg, t);
  }

  @Override
  public void error(String format) {
    Slf4jLevel.ERROR.print(log, format);
  }

  @Override
  public void error(String format, Object... arguments) {
    Slf4jLevel.ERROR.print(log, format, arguments);
  }

  @Override
  public void error(String msg, Throwable t) {
    Slf4jLevel.ERROR.print(log, msg, t);
  }

}
