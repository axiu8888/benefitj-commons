package com.benefitj.examples;

import com.benefitj.netty.adapter.BiConsumerChannelInboundHandler;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.netty.server.UdpNettyServer;
import com.benefitj.spring.applicationevent.ApplicationListenerAdapter;
import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
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

  // 定义 UdpNettyServer 子类
  @Component
  public static class UdpProxy extends UdpNettyServer {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public UdpNettyServer useDefaultConfig() {
      this.childHandler(new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          ch.pipeline()
              .addLast(new BiConsumerChannelInboundHandler<>(ByteBuf.class, (ctx, msg) -> {
                // 分发消息
                log.info("send: {}, readableBytes: {}", ctx.channel().remoteAddress(), msg.readableBytes());
              }));
        }
      });
      return super.useDefaultConfig();
    }
  }


  @Component
  public static class UdpProxyListener extends ApplicationListenerAdapter {

    @Autowired
    private UdpProxy udpProxy;

    @Override
    public void onApplicationReadyEvent(ApplicationReadyEvent event) {
      udpProxy.localAddress(15035);
      udpProxy.start();
    }

    @Override
    public void onContextClosedEvent(ContextClosedEvent event) {
      udpProxy.stop();
    }

  }

}
