package com.benefitj.netty.client;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.netty.TcpClient;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.handler.OutboundHandler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
class TcpNettyClientTest {

  @BeforeEach
  void setup() {
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  public void testTcpClientAutoReconnect() throws Exception {
    TcpNettyClient client = new TcpNettyClient()
        //.setRemoteAddress(new InetSocketAddress("127.0.0.1", 62014))
        .autoReconnect(true, Duration.ofSeconds(5))
        .setSoSndBuf(8 * (1024 << 10))
        .setSoRcvBuf(8 * (1024 << 10))
        .handler(new ChannelInitializer<Channel>() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ch.pipeline()
                .addLast(InboundHandler.msgToStringHandler(StandardCharsets.UTF_8))
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

  @Test
  void test_TcpClient() throws Exception {
    TcpClient client = new TcpClient()
        .setRemoteAddress(new InetSocketAddress("127.0.0.1", 80))
        .setAutoConnect(true, Duration.ofSeconds(5))
        .setChannelInitializer(ch -> {
              ch.pipeline()
                  .addLast(new LoggingHandler(LogLevel.INFO))// 添加日志处理器
                  .addLast(new IdleStateHandler(120, 0, 0))// 添加空闲状态检测
                  .addLast(OutboundHandler.msgToByteBufHandler(true, StandardCharsets.UTF_8))
              ;
        })
        .start(f -> log.info("tcp client started... ｛｝", f.isSuccess()));

    for (int i = 0; i < 1000; i++) {
      if (client.isActive()) {
        client.write("send ==>: " + i, (success, cause) -> {
          log.info("send: {}, cause: {}", success, cause != null ? CatchUtils.getLogStackTrace(cause) : null);
        });
      }
      // wait
      EventLoop.sleepSecond(1);
    }

    EventLoop.sleepSecond(5);
    client.stop();
  }


}
