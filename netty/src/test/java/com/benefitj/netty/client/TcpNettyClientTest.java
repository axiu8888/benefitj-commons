package com.benefitj.netty.client;

import com.benefitj.core.EventLoop;
import com.benefitj.netty.handler.InboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class TcpNettyClientTest {

  @Test
  public void testTcpClientAutoReconnect() throws Exception {
    TcpNettyClient client = new TcpNettyClient()
        .setRemoteAddress(new InetSocketAddress("127.0.0.1", 62014))
        .autoReconnect(true, Duration.ofSeconds(5))
        .setSoSndBuf(8 * (1024 << 10))
        .setSoRcvBuf(8 * (1024 << 10))
        .handler(new ChannelInitializer<Channel>() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ch.pipeline()
                .addLast(new StringDecoder(StandardCharsets.UTF_8))
                .addLast(new StringEncoder(StandardCharsets.UTF_8))
                .addLast(InboundHandler.newHandler(String.class, (handler, ctx, msg) -> {
                  log.info("receive[{}], remote: {}, data: {}"
                      , ctx.channel().id().asShortText()
                      , ctx.channel().remoteAddress()
                      , msg
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
