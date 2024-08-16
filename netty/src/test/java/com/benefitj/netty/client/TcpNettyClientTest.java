package com.benefitj.netty.client;

import com.benefitj.core.EventLoop;
import com.benefitj.netty.handler.InboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class TcpNettyClientTest {

  private TcpNettyClient client;

  @Before
  public void before() {
    client = new TcpNettyClient()
        .autoReconnect(true, Duration.ofSeconds(5))
        .remoteAddress(new InetSocketAddress("127.0.0.1", 62014))
        ._self_();
  }

  @Test
  public void testTcpClientAutoReconnect() throws Exception {
    client.handler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new StringDecoder(StandardCharsets.UTF_8))
            .addLast(new StringEncoder(StandardCharsets.UTF_8))
            .addLast(InboundHandler.newByteBufHandler((handler, ctx, msg) -> {
              byte[] data = handler.copy(msg);
              log.info("receive[{}], remote: {}, data: {}"
                  , ctx.channel().id().asShortText()
                  , ctx.channel().remoteAddress()
                  , new String(data, StandardCharsets.US_ASCII)
              );
            }));
      }
    });
    client.start(f -> log.info("client started... "));

    EventLoop.sleepSecond(1);

    for (int i = 0; i < 1000; i++) {
      client.writeAndFlush("send ==>: " + i, f -> log.info("send: " + f.isSuccess()));
      // wait
      EventLoop.sleepSecond(1);
    }

    client.stop();
  }

  @After
  public void after() {
  }

}
