package com.benefitj.mqtt;

import com.benefitj.core.DefaultThreadFactory;
import com.benefitj.core.HexUtils;
import com.benefitj.netty.ByteBufCopy;
import com.benefitj.netty.adapter.BiConsumerInboundHandler;
import com.benefitj.netty.adapter.ChannelShutdownEventHandler;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.netty.server.TcpNettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelOutputShutdownEvent;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * MQTT TCP 服务端
 */
public class MqttTcpServer extends TcpNettyServer {


  @Override
  public TcpNettyServer useDefaultConfig() {
    this.executeWhileNull(this.workerGroup(), () -> this.group(
        new NioEventLoopGroup(new DefaultThreadFactory("mqtt-", "-boss-"))
        , new NioEventLoopGroup(new DefaultThreadFactory("mqtt-", "-worker-"))));

    // 端口
    this.localAddress(1883);
    // 打印
    this.handler(new LoggingHandler(LogLevel.DEBUG));

    // 处理消息
    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        final ByteBufCopy copy = new ByteBufCopy();
        ch.pipeline()
            .addLast(ChannelShutdownEventHandler.INSTANCE)
            .addLast(new IdleStateHandler(20, 0, 0, TimeUnit.SECONDS) {
              @Override
              public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                log.info("userEventTriggered: " + evt);
                if (evt instanceof IdleStateEvent) {
                  log.info("state: " + ((IdleStateEvent) evt).state());
                  if (!ctx.channel().isActive()) {
                    ctx.channel().close();
                  } else {
                    ByteBuf msg = Unpooled.wrappedBuffer("ping".getBytes());
                    ctx.writeAndFlush(msg);
                  }
                } else {
                  super.userEventTriggered(ctx, evt);
                }
              }

              @Override
              public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                log.info("客户端上线, {}", ctx.channel().remoteAddress());
              }

              @Override
              public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
                log.info("客户端下线, {}", ctx.channel().remoteAddress());
              }

              @Override
              public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                if (cause instanceof IOException) {
                  log.error("exceptionCaught: " + cause.getMessage(), cause);
                } else {
                  //super.exceptionCaught(ctx, cause);
                }
                super.exceptionCaught(ctx, cause);
              }
            })
            .addLast(new BiConsumerInboundHandler<>(ByteBuf.class, (ctx, msg) -> {
              byte[] data = copy.copyAdnReset(msg);
              log.info("消息: {}, msg: {}", HexUtils.bytesToHex(data), new String(data));

              // 返回消息
              ctx.writeAndFlush(msg.retain());

            }));
      }
    });

    return super.useDefaultConfig();
  }

  static {
    NettyLogger.INSTANCE.setLogger(new Log4jNettyLogger("mqtt"));
  }

}
