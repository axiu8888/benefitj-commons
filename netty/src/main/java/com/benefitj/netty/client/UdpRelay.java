package com.benefitj.netty.client;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ShutdownHook;
import com.benefitj.netty.handler.InboundConsumer;
import com.benefitj.netty.handler.InboundHandler;
import com.benefitj.netty.handler.OutboundConsumer;
import com.benefitj.netty.handler.OutboundHandler;
import io.netty.channel.socket.DatagramPacket;
import io.netty.util.ReferenceCountUtil;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * UDP代理
 */
public class UdpRelay extends UdpClient {

  /**
   * 处理的操作
   */
  private final InboundConsumer<DatagramPacket> inbounds = ProxyUtils.newCopyListProxy(InboundConsumer.class);
  private final OutboundConsumer<DatagramPacket> outbounds = ProxyUtils.newCopyListProxy(OutboundConsumer.class);

  public UdpRelay() {
    setChannelInitializer(ch -> {
      ch.pipeline()
          .addLast(InboundHandler.newDatagramHandler((handler, ctx, msg) -> {
            if (getInbounds().isEmpty()) {
              ctx.fireChannelRead(ReferenceCountUtil.retain(msg));
            } else {
              inbounds.channelRead0(handler, ctx, msg);
            }
          }))
          .addLast(OutboundHandler.newDatagramHandler((handler, ctx, msg, promise) -> {
            if (getOutbounds().isEmpty()) {
              ctx.write(msg, promise);
            } else {
              outbounds.channelWrite0(handler, ctx, msg, promise);
            }
          }));
    });
  }

  /**
   * 消费者
   */
  public List<InboundConsumer<DatagramPacket>> getInbounds() {
    return (List<InboundConsumer<DatagramPacket>>) inbounds;
  }

  public UdpRelay addInbound(InboundConsumer<DatagramPacket> consumer) {
    if (consumer != null && !getInbounds().contains(consumer)) {
      getInbounds().add(consumer);
    }
    return this;
  }

  public UdpRelay removeInbound(InboundConsumer<DatagramPacket> consumer) {
    if (consumer != null) {
      getInbounds().remove(consumer);
    }
    return this;
  }

  /**
   * 消费者
   */
  public List<OutboundConsumer<DatagramPacket>> getOutbounds() {
    return (List<OutboundConsumer<DatagramPacket>>) outbounds;
  }

  public UdpRelay addOutbound(OutboundConsumer<DatagramPacket> consumer) {
    if (consumer != null && !getOutbounds().contains(consumer)) {
      getOutbounds().add(consumer);
    }
    return this;
  }

  public UdpRelay removeOutbound(OutboundConsumer<DatagramPacket> consumer) {
    if (consumer != null) {
      getOutbounds().remove(consumer);
    }
    return this;
  }

  public static UdpRelay startUdp() {
    return startUdp(new UdpRelay());
  }

  public static <T extends UdpRelay> T startUdp(T sender) {
    final CountDownLatch latch = new CountDownLatch(1);
    sender.start(f -> {
      if (f.isSuccess()) {
        ShutdownHook.register(sender::stop);
        latch.countDown();
      }
    });
    CatchUtils.ignore(() -> latch.await(1, TimeUnit.SECONDS));
    return sender;
  }

}
