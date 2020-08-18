package com.benefitj.examples.proxy;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.DefaultThreadFactory;
import com.benefitj.netty.ByteBufReadCache;
import com.benefitj.netty.adapter.BiConsumerChannelInboundHandler;
import com.benefitj.netty.server.UdpNettyServer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 采集器代理
 */
@Component
public class CollectorUdpProxy extends UdpNettyServer {

  private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

  private ByteBufReadCache cache = new ByteBufReadCache();

  @Override
  public UdpNettyServer useDefaultConfig() {
    this.group(
        new NioEventLoopGroup(1, new DefaultThreadFactory("boss-", "-t-"))
        , new DefaultEventLoopGroup(new DefaultThreadFactory("worker-", "-t-")));
    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new ChannelInboundHandlerAdapter() {
              @Override
              public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
                super.channelRegistered(ctx);
                log.info("channelRegistered, remote: {}", ctx.channel().remoteAddress());
              }

              @Override
              public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
                super.handlerAdded(ctx);
                log.info("handlerAdded, remote: {}", ctx.channel().remoteAddress());
              }

              @Override
              public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                log.info("channelActive, remote: {}", ctx.channel().remoteAddress());
              }

              @Override
              public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
                log.info("channelInactive, remote: {}", ctx.channel().remoteAddress());
              }

              @Override
              public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                super.handlerRemoved(ctx);
                log.info("handlerRemoved, remote: {}", ctx.channel().remoteAddress());
              }

              @Override
              public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
                super.channelUnregistered(ctx);
                log.info("channelUnregistered, remote: {}", ctx.channel().remoteAddress());
              }
            })
            .addLast(new BiConsumerChannelInboundHandler<>(ByteBuf.class, (ctx, msg) -> {
              byte[] data = cache.read(msg);
              log.info("send: {}, deviceId: {}, packageSn: {}, time: {}, readableBytes: {}"
                  , ctx.channel().remoteAddress()
                  , PacketUtils.getHexDeviceId(data)
                  , PacketUtils.getPacketSn(data)
                  , DateFmtter.fmt(PacketUtils.getTime(data, 9 + 4, 9 + 9))
                  , data.length);
            }));
      }
    });
    return super.useDefaultConfig();
  }
}
