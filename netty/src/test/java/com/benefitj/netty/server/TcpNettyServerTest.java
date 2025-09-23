package com.benefitj.netty.server;

import com.benefitj.core.EventLoop;
import com.benefitj.core.HexUtils;
import com.benefitj.netty.NettyFactory;
import com.benefitj.netty.TcpServer;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.handler.OutboundHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

@Slf4j
public class TcpNettyServerTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testTcpServer() {
    TcpServer server = new TcpServer(7004)
        .setChannelInitializer(ch -> {
          ch.pipeline()
              .addLast(OutboundHandler.msgToByteBufHandler())
              .addLast(InboundHandler.msgToBytesHandler())
              .addLast(InboundHandler.newBytesHandler((handler, ctx, msg) -> {
                byte[] data = msg;
                log.info("data[{}] ===>: {}", data.length, HexUtils.bytesToHex(data));
                ctx.writeAndFlush(data);
              }));
        })
        .start(f -> log.info("tcp server start ...."));

    EventLoop.sleep(100000, TimeUnit.SECONDS);
    server.stop(f -> log.info("tcp server stop ...."));
    EventLoop.sleep(10, TimeUnit.SECONDS);

  }

  @Test
  public void testHttpServer() {
    TcpNettyServer server = NettyFactory.newTcpServer()
        .name("httpServer")
        .childHandler(ch -> {
          ch.pipeline()
              // http 编解码
              .addLast(new HttpServerCodec())
              // http 消息聚合器，512*1024为接收的最大contentlength
              .addLast(new HttpObjectAggregator(1024 << 1024))
              // 请求处理器
              .addLast(InboundHandler.newHandler(FullHttpRequest.class, (handler, ctx, msg) -> {
                //100 Continue
                if (HttpUtil.is100ContinueExpected(msg)) {
                  ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
                }

                StringBuilder sb = new StringBuilder();

                sb.append("uri: ").append(msg.uri()).append("\n");
                sb.append("method: ").append(msg.method().name()).append("\n");
                sb.append("protocolVersion: ").append(msg.protocolVersion()).append("\n");

                sb.append("\n------------------- headers -------------------\n");
                msg.headers().forEach(entry -> sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
                sb.append("\n------------------- headers -------------------\n");

                String str = sb.toString();
                // 创建http响应
                FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1,
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));
                ctx.writeAndFlush(response)
                    .addListener(ChannelFutureListener.CLOSE);
              }));
        })
        .localAddress(8080)
        .start(f -> log.info("start http server"));
    EventLoop.sleep(300, TimeUnit.SECONDS);
    server.stop(f -> log.info("stop http server, {}", EventLoop.getThreadName()));
  }


}