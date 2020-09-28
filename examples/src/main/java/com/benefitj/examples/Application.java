package com.benefitj.examples;

import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.benefitj.spring.eventbus.EnableAutoEventBusPoster;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableAutoEventBusPoster
@EnableSpringCtxInit
@EnableAutoApplicationListener
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }
}
