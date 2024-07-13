package com.benefitj.netty;

import com.benefitj.core.log.ILogger;
import com.benefitj.core.log.LoggerHolder;
import com.benefitj.core.log.Slf4jLogger;

public class NettyLogger {

  static final LoggerHolder holder = new LoggerHolder(ILogger.get());

  static {
    Slf4jLogger.setIfPassable(holder, "netty");
  }

  public static ILogger get() {
    return holder;
  }

  public static void set(ILogger log) {
    holder.setLog(log);
  }

}
