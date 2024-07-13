package com.benefitj.vertx;

import com.benefitj.core.log.ILogger;
import com.benefitj.core.log.LoggerHolder;
import com.benefitj.core.log.Slf4jLogger;

public class VertxLogger {

  static final LoggerHolder holder = new LoggerHolder(ILogger.get());

  static {
    Slf4jLogger.setIfPassable(holder, "vertx-mqtt");
  }

  public static ILogger get() {
    return holder;
  }

  public static void set(ILogger log) {
    holder.setLog(log);
  }

}
