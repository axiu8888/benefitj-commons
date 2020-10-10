package com.benefitj.mqtt;

import com.benefitj.core.SingletonSupplier;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

/**
 * MQTT服务端
 */
@SpringBootApplication
public class MqttServerApplication {
  public static void main(String[] args) {
    SpringApplication.run(MqttServerApplication.class, args);

//    // 结束程序
//    EventLoop.single().schedule(() ->
//        System.exit(0), 20, TimeUnit.SECONDS);

  }

  private static final Logger logger = LoggerFactory.getLogger(MqttServerApplication.class);

//  /**
//   * 线程组
//   */
//  private static final EventLoop loop = EventLoop.newEventLoop(2, false);

  /**
   * 服务端
   */
  private static final SingletonSupplier<MqttTcpServer> serverInstance = SingletonSupplier.of(MqttTcpServer::new);

  /**
   * 启动
   */
  @Component
  public static class OnMqttAppStarter implements ApplicationListener<ApplicationReadyEvent> {

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
      // 启动
      final MqttTcpServer server = serverInstance.get();
      server.start(f -> logger.info("MQTT server started, localAddr: {}", server.localAddress()));
    }
  }

  /**
   * 停止
   */
  @Component
  public static class OnMqttAppStopped implements ApplicationListener<ContextStoppedEvent> {

    @Override
    public void onApplicationEvent(ContextStoppedEvent event) {
      // 结束
      final MqttTcpServer server = serverInstance.get();
      server.stop(f -> {
        Channel channel = server.getServeChannel();
        logger.info("MQTT server stopped, localAddr: {}, active: {}, open: {}"
            , channel.localAddress()
            , channel.isActive()
            , channel.isOpen());
      });
    }
  }
}
