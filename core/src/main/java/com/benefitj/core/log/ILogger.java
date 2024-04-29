package com.benefitj.core.log;

import java.util.concurrent.atomic.AtomicReference;

public interface ILogger {

  static final AtomicReference<LoggerHolder> holder = new AtomicReference<>(new LoggerHolder(Slf4jLogger.NONE));

  public static LoggerHolder get() {
    return holder.get();
  }

  public static void set(LoggerHolder log) {
    holder.set(log);
  }

  void trace(String format);

  void trace(String format, Object... arguments);

  void trace(String msg, Throwable t);

  void debug(String format);

  void debug(String format, Object... arguments);

  void debug(String msg, Throwable t);

  void info(String format);

  void info(String format, Object... arguments);

  void info(String msg, Throwable t);

  void warn(String format);

  void warn(String format, Object... arguments);

  void warn(String msg, Throwable t);

  void error(String format);

  void error(String format, Object... arguments);

  void error(String msg, Throwable t);

}
