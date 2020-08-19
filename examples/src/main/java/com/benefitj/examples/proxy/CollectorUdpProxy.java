package com.benefitj.examples.proxy;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.DefaultThreadFactory;
import com.benefitj.netty.ByteBufReadCache;
import com.benefitj.netty.adapter.BiConsumerChannelInboundHandler;
import com.benefitj.netty.server.UdpNettyServer;
import com.benefitj.netty.server.udpdevice.*;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;

/**
 * 采集器代理
 */
@Component
public class CollectorUdpProxy extends UdpNettyServer {

  private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

  private ByteBufReadCache cache = new ByteBufReadCache();

  private final UdpDeviceClientManager<CollectorDeviceClient> clientManager;
  private OnlineDeviceExpireExecutor executor;

  public CollectorUdpProxy() {
    this.clientManager = new DefaultUdpDeviceClientManager<>();
    this.executor = new OnlineDeviceExpireExecutor(clientManager);
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    this.group(
        new NioEventLoopGroup(1, new DefaultThreadFactory("boss-", "-t-"))
        , new DefaultEventLoopGroup(new DefaultThreadFactory("worker-", "-t-")));


    clientManager.setStateChangeListener(new ClientStateChangeListener<CollectorDeviceClient>() {
      @Override
      public void onAddition(String id, CollectorDeviceClient newClient, @Nullable CollectorDeviceClient oldClient) {
        log.info("新客户端上线: {}, oldClient: {}", newClient, oldClient);
      }

      @Override
      public void onRemoval(String id, CollectorDeviceClient client) {
        log.info("客户端下线: {}", client);
      }
    });

    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new ChannelInboundHandlerAdapter() {

              @Override
              public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                executor.channelActive(ctx);
              }

              @Override
              public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                super.channelInactive(ctx);
                executor.channelInactive(ctx);
              }

            })
            .addLast(new BiConsumerChannelInboundHandler<>(ByteBuf.class, (ctx, msg) -> {
              byte[] data = cache.read(msg);

              String deviceId = PacketUtils.getHexDeviceId(data);
              CollectorDeviceClient client = clientManager.get(deviceId);
              if (client == null) {
                clientManager.put(deviceId, client = new CollectorDeviceClient(deviceId, ctx.channel()));
              }
              // 重置接收导数据的时间
              client.resetLastRecvTimeNow();

              log.info("send: {}, deviceId: {}, packageSn: {}, time: {}"
                  , ctx.channel().remoteAddress()
                  , deviceId
                  , PacketUtils.getPacketSn(data)
                  , DateFmtter.fmt(PacketUtils.getTime(data, 9 + 4, 9 + 9)));
            }));
      }
    });
    return super.useDefaultConfig();
  }


  /**
   * 采集器设备客户端
   */
  static class CollectorDeviceClient extends UdpDeviceClient {

    public CollectorDeviceClient(String id, Channel channel) {
      super(id, channel);
    }

    @Override
    public boolean equals(Object o) {
      return super.equals(o);
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }
  }

}
