package com.benefitj.netty.client;

import com.benefitj.netty.NettyHexUtils;
import com.benefitj.netty.handler.BiConsumerInboundHandler;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
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
        .addStartListeners(f ->
            log.info("client started, remote: {}, f: {}", client.remoteAddress(), f.isSuccess()))
        .addStopListeners(f ->
            log.info("client stopped, remote: {}, f: {}", client.remoteAddress(), f.isSuccess()))
        .self();
  }

  @Test
  public void testTcpClientAutoReconnect() throws Exception {
    client.handler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(BiConsumerInboundHandler.newByteBufHandler((handler, ctx, msg) -> {
              log.info("receive[{}], remote: {}, data: {}"
                  , ctx.channel().id().asShortText()
                  , ctx.channel().remoteAddress()
                  , NettyHexUtils.bytesToHex(handler.copy(msg))
              );
            }));
      }
    });
    client.start(f -> log.info("client started... "));
    // wait
    TimeUnit.MINUTES.sleep(5);
  }

  @After
  public void after() {
  }

}
