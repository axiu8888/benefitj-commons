package com.benefitj.examples.server;

import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.spring.applicationevent.ApplicationListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

/**
 * 采集器开关
 */
@ConditionalOnProperty(name = "collector.enabled")
@Component
public class CollectorServerSwitcher extends ApplicationListenerAdapter {

  private static final Logger logger = LoggerFactory.getLogger(CollectorServerSwitcher.class);

  static {
    NettyLogger.INSTANCE.setLogger(new Log4jNettyLogger());
  }

  @Autowired
  private CollectorUdpServer server;

  @Value("#{ @environment['collector.server.port'] ?: 62014 }")
  private Integer port;

  @Override
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    server.readerTimeout(30);
    server.localAddress(port);
    server.start(future -> logger.info("collector server start local: {}", server.localAddress()));
  }

  @Override
  public void onContextClosedEvent(ContextClosedEvent event) {
    server.stop(future -> logger.info("collector server stop local: {}", server.localAddress()));
  }

}
