package com.benefitj.mqtt;

import com.benefitj.core.DefaultThreadFactory;
import com.benefitj.core.HexUtils;
import com.benefitj.netty.handler.*;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import com.benefitj.netty.server.TcpNettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

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
        ch.pipeline()
            .addLast(ChannelShutdownEventHandler.INSTANCE)
            .addLast(IdleStateEventHandler.newReaderHandler(20_000, (ctx, evt) -> {
              log.info("state: " + evt.state());
              if (!ctx.channel().isActive()) {
                ctx.channel().close();
              } else {
                ByteBuf msg = Unpooled.wrappedBuffer("ping".getBytes());
                ctx.writeAndFlush(msg);
              }
            }))
            .addLast(ActiveChangeChannelHandler.newHandler((state, ctx, handler) ->
                log.info("客户端{}, {}"
                    , state == ActiveState.ACTIVE ? "上线" : "下线"
                    , ctx.channel().remoteAddress())))
            .addLast(BiConsumerInboundHandler.newByteBufHandler((handler, ctx, msg) -> {
              byte[] data = handler.copyAndReset(msg, true);
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
