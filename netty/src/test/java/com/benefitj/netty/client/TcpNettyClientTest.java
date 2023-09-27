package com.benefitj.netty.client;

import com.benefitj.core.EventLoop;
import com.benefitj.netty.handler.InboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.concurrent.FutureListener;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpNettyClientTest {

  private TcpNettyClient client;

  @Before
  public void before() {
    client = new TcpNettyClient()
        .autoReconnect(true, 3, TimeUnit.SECONDS)
        .remoteAddress(new InetSocketAddress("127.0.0.1", 63015))
        ._self();
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

    EventLoop.awaitSeconds(1);

    for (int i = 0; i < 1000; i++) {
      client.writeAndFlush("ss " + i, (FutureListener<Void>) f -> log.info("send: " + f.isSuccess()));
      // wait
      EventLoop.awaitSeconds(1);
    }

    client.stop();
  }

  @After
  public void after() {
  }

}
