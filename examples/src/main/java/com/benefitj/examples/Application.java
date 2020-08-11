package com.benefitj.examples;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.DefaultThreadFactory;
import com.benefitj.netty.ByteBufReadCache;
import com.benefitj.netty.adapter.BiConsumerChannelInboundHandler;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.netty.server.UdpNettyServer;
import com.benefitj.spring.applicationevent.ApplicationListenerAdapter;
import com.benefitj.spring.applicationevent.EnableAutoApplicationListener;
import com.benefitj.spring.ctx.EnableSpringCtxInit;
import com.hsrg.collector.PacketUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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

    private ByteBufReadCache cache = new ByteBufReadCache();

    @Override
    public UdpNettyServer useDefaultConfig() {
      this.group(new NioEventLoopGroup(new DefaultThreadFactory("boss-", "-t-"))
          , new DefaultEventLoopGroup(new DefaultThreadFactory("worker-", "-t-")));
      this.childHandler(new ChannelInitializer<Channel>() {
        @Override
        protected void initChannel(Channel ch) throws Exception {
          ch.pipeline()
              .addLast(new BiConsumerChannelInboundHandler<>(ByteBuf.class, (ctx, msg) -> {
                // 分发消息
                byte[] data = cache.read(msg, true, false);
                log.info("send: {}, readableBytes: {}, packageSn: {}, time: {}", ctx.channel().remoteAddress()
                    , data.length, PacketUtils.getPacketSn(data), DateFmtter.fmt(PacketUtils.getTime(data, 9 + 4, 9 + 9)));
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
      udpProxy.localAddress(62014);
      udpProxy.start();
    }

    @Override
    public void onContextClosedEvent(ContextClosedEvent event) {
      udpProxy.stop();
    }

  }

}
