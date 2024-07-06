package com.benefitj.mqtt;

import com.benefitj.core.log.ILogger;
import com.benefitj.core.log.LoggerHolder;

public class MqttLogger {

  static final LoggerHolder holder = new LoggerHolder(ILogger.get());

  public static ILogger get() {
    return holder;
  }

  public static void set(ILogger log) {
    holder.setLog(log);
  }

}
