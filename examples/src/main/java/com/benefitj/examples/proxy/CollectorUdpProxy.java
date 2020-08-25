package com.benefitj.examples.proxy;

import com.benefitj.core.DateFmtter;
import com.benefitj.core.DefaultThreadFactory;
import com.benefitj.netty.ByteBufReadCache;
import com.benefitj.netty.adapter.BiConsumerChannelInboundHandler;
import com.benefitj.netty.server.UdpNettyServer;
import com.benefitj.netty.server.channel.NioDatagramServerChannel;
import com.benefitj.netty.server.device.DeviceStateChangeListener;
import com.benefitj.netty.server.udpclient.*;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 采集器代理
 */
@Component
public class CollectorUdpProxy extends UdpNettyServer {

  private final Logger log = LoggerFactory.getLogger(getClass().getSimpleName());

  private ByteBufReadCache cache = new ByteBufReadCache();

  private final UdpDeviceClientManager<CollectorDeviceClient> clientManager;
  private final OnlineDeviceExpireExecutor executor;

  public CollectorUdpProxy() {
    this.clientManager = UdpDeviceClientManager.newInstance();
    this.executor = new OnlineDeviceExpireExecutor(clientManager);
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    this.group(
        new NioEventLoopGroup(1, new DefaultThreadFactory("boss-", "-t-"))
        , new DefaultEventLoopGroup(new DefaultThreadFactory("worker-", "-t-")));

    clientManager.setDelay(2000);
    clientManager.setExpire(5000);

    clientManager.setStateChangeListener(new DeviceStateChangeListener<CollectorDeviceClient>() {
      @Override
      public void onAddition(String id, CollectorDeviceClient newDevice, @Nullable CollectorDeviceClient oldDevice) {
        log.info("新客户端上线: {}, oldClient: {}", newDevice, oldDevice);
      }

      @Override
      public void onRemoval(String id, CollectorDeviceClient device) {
        log.info("客户端下线: {}, duration: {}", device, DateFmtter.now() - device.getRecvTime());
      }
    });

    this.handler(new ChannelInboundHandlerAdapter() {

      ScheduledFuture<?> future;

      @Override
      public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        executor.start();
        future = ctx.executor().scheduleAtFixedRate(() ->
            log.info("\n设备数量: {}, children channel: {}\n"
                , clientManager.size()
                , ((NioDatagramServerChannel)ctx.channel()).children().size()
            ), 1, 5, TimeUnit.SECONDS);
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        executor.stop();
        future.cancel(true);
      }
    });

    this.childHandler(new ChannelInitializer<Channel>() {
      @Override
      protected void initChannel(Channel ch) throws Exception {
        ch.pipeline()
            .addLast(new BiConsumerChannelInboundHandler<>(ByteBuf.class, (ctx, msg) -> {
              byte[] data = cache.read(msg);

              String deviceId = CollectorHelper.getHexDeviceId(data);
              CollectorDeviceClient client = clientManager.get(deviceId);
              if (client == null) {
                clientManager.put(deviceId, client = new CollectorDeviceClient(deviceId, ctx.channel()));
              }
              // 重置接收导数据的时间
              client.resetRecvTimeNow();

              if (PacketType.isRealtime(CollectorHelper.getType(data))) {
                // 反馈
                ctx.writeAndFlush(Unpooled.wrappedBuffer(CollectorHelper.getRealtimeFeedback(data)));

                if (client.refresh(CollectorHelper.getPacketSn(data)) && "010003f6".equals(deviceId)) {
                  log.info("send: {}, deviceId: {}, packageSn: {}, time: {}, online: {}"
                      , ctx.channel().remoteAddress()
                      , deviceId
                      , CollectorHelper.getPacketSn(data)
                      , DateFmtter.fmt(CollectorHelper.getTime(data, 9 + 4, 9 + 9))
                      , DateFmtter.fmt(client.getOnlineTime())
                  );
                }
              }

            }));
      }
    });
    return super.useDefaultConfig();
  }


  /**
   * 采集器设备客户端
   */
  static class CollectorDeviceClient extends UdpDeviceClient {

    private final AtomicInteger packageSn = new AtomicInteger();

    public CollectorDeviceClient(String id, Channel channel) {
      super(id, channel);
    }

    public boolean refresh(int sn) {
      return packageSn.compareAndSet(sn - 1, sn);
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
