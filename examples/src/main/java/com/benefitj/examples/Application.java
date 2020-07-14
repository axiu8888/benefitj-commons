package com.benefitj.examples;

import com.benefitj.influxdb.spring.EnableAutoRxJavaInfluxDBConfiguration;
import com.benefitj.influxdb.spring.EnableInfluxWriteManager;
import com.benefitj.influxdb.template.RxJavaInfluxDBTemplate;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.listener.EnableSpringEventAutoListener;
import com.benefitj.spring.listener.IApplicationReadyEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

@EnableSpringCtxInit
@EnableSpringEventAutoListener
@EnableAutoRxJavaInfluxDBConfiguration
@EnableInfluxWriteManager
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }


  @Component
  public static class OnAppReadyListener implements IApplicationReadyEventListener {

    @Autowired
    private RxJavaInfluxDBTemplate template;

    @Override
    public void onEvent(ApplicationReadyEvent event) {
      System.err.println("measurements: \n" + String.join(",\n", template.getMeasurements()));
    }
  }
}
