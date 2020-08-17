package com.benefitj.examples;

import com.benefitj.examples.proxy.CollectorUdpProxy;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.spring.applicationevent.ApplicationListenerAdapter;
import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@EnableSpringCtxInit
@EnableAutoApplicationListener
@SpringBootApplication
public class Application {
  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  static {
    NettyLogger.INSTANCE.setLogger(new Log4jNettyLogger());
  }

  @Component
  public static class UdpProxyListener extends ApplicationListenerAdapter {

    @Autowired
    private CollectorUdpProxy udpProxy;

    @Override
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
      udpProxy.localAddress(62014);
      udpProxy.idle(20, 0, TimeUnit.SECONDS);
      udpProxy.start();
    }

    @Override
    public void onContextClosedEvent(ContextClosedEvent event) {
      udpProxy.stop();
    }

  }

}
