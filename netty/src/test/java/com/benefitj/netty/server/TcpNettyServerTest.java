package com.benefitj.netty.server;

import com.benefitj.core.EventLoop;
import com.benefitj.netty.NettyFactory;
import com.benefitj.netty.handler.BiConsumerInboundHandler;
import com.benefitj.netty.handler.ByteBufCopyInboundHandler;
import com.benefitj.netty.handler.InboundHandlerBiConsumer;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpNettyServerTest {

  private final Logger log = LoggerFactory.getLogger(getClass());


  @Before
  public void setUp() throws Exception {

  }

  @Test
  public void testHttpServer() {
    TcpNettyServer server = NettyFactory.newTcpServer()
        .name("httpServer")
        .childHandler(new ChannelInitializer<Channel>() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ch.pipeline()
                // http 编解码
                .addLast(new HttpServerCodec())
                // http 消息聚合器，512*1024为接收的最大contentlength
                .addLast(new HttpObjectAggregator(512 * 1024))
                // 请求处理器
                .addLast(BiConsumerInboundHandler.newHandler(FullHttpRequest.class, new HttpRequestConsumer()))
            ;
          }
        })
        .localAddress(8080)
        .start(f -> log.info("start http server"));
    EventLoop.sleepSecond(30);
    server.stop(f -> log.info("stop http server, {}", EventLoop.threadName()));
  }

  @After
  public void tearDown() throws Exception {
  }


  static class HttpRequestConsumer implements InboundHandlerBiConsumer<FullHttpRequest> {
    @Override
    public void accept(ByteBufCopyInboundHandler<FullHttpRequest> handler, ChannelHandlerContext ctx, FullHttpRequest req) {
      //100 Continue
      if (HttpUtil.is100ContinueExpected(req)) {
        ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE));
      }

      StringBuilder sb = new StringBuilder();

      sb.append("uri: ").append(req.uri()).append("\n");
      sb.append("method: ").append(req.method().name()).append("\n");
      sb.append("protocolVersion: ").append(req.protocolVersion()).append("\n");

      sb.append("\n------------------- headers -------------------\n");
      req.headers().forEach(entry -> sb.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n"));
      sb.append("\n------------------- headers -------------------\n");

      String msg = sb.toString();
      // 创建http响应
      FullHttpResponse response = new DefaultFullHttpResponse(
          HttpVersion.HTTP_1_1,
          HttpResponseStatus.OK,
          Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8));
      ctx.writeAndFlush(response)
          .addListener(ChannelFutureListener.CLOSE);
    }
  }

}