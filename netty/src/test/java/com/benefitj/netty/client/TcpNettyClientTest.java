package com.benefitj.netty.client;

import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;


public class TcpNettyClientTest {
  public static void main(String[] args) throws Exception {
    TcpNettyClientTest test = new TcpNettyClientTest();


    test.before();

    test.testTcpClientAutoReconnect();

    test.after();
  }

  static {
    NettyLogger.INSTANCE.setLogger(new Log4jNettyLogger());
  }

  private final Logger log = LoggerFactory.getLogger(getClass());

  private TcpNettyClient client;

  @Before
  public void before() {
    client = new TcpNettyClient()
        .autoReconnect(true, 3, TimeUnit.SECONDS)
        .remoteAddress(new InetSocketAddress("127.0.0.1", 63015))
        .self();
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

    TimeUnit.SECONDS.sleep(1);

    for (int i = 0; i < 1000; i++) {
      Channel channel = client.getServeChannel();
      if (channel != null && channel.isActive()) {
        channel.writeAndFlush("ss " + i);
      } else {
        log.info(">>>: ~ " + i);
      }
      // wait
      TimeUnit.SECONDS.sleep(1);
    }

    client.stop();
  }

  @After
  public void after() {
  }

}
