package com.benefitj.influxdb.spring;

import com.benefitj.influxdb.write.InfluxWriteManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableAsync
@EnableScheduling
@Configuration
public class AsyncInfluxDBManagement {

  @Autowired
  private InfluxWriteManager manager;

  /**
   * 异步检查
   */
  @Async
  @Scheduled(fixedRate = 1000)
  public void autoCheck() {
    manager.checkFlush();
  }

}
