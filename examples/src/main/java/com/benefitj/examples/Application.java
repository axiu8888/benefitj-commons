package com.benefitj.examples;

import com.benefitj.examples.proxy.CollectorUdpProxy;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.spring.applicationevent.ApplicationListenerAdapter;
import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;


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

  private static final Logger logger = LoggerFactory.getLogger(Application.class);

  @Component
  public static class UdpProxyListener extends ApplicationListenerAdapter {

    @Autowired
    private CollectorUdpProxy udpProxy;

    @Override
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
      udpProxy.readTimeout(10);
      udpProxy.localAddress(62014);
      udpProxy.start(future -> logger.info("proxy start local: {}", udpProxy.localAddress()));
    }

    @Override
    public void onContextClosedEvent(ContextClosedEvent event) {
      udpProxy.stop(future -> logger.info("proxy stop local: {}", udpProxy.localAddress()));
    }

  }

}
