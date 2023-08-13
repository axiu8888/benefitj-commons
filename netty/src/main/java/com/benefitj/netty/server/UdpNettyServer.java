package com.benefitj.netty.server;

import com.benefitj.netty.server.channel.NioDatagramServerChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.unix.UnixChannelOption;

import java.util.HashMap;
import java.util.Map;

/**
 * UDP服务
 */
public class UdpNettyServer extends AbstractNettyServer<UdpNettyServer> {

  public UdpNettyServer() {
    super();
  }

  @Override
  protected UdpNettyServer useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.whenNull(bossGroup(), () ->
          this.group(new NioEventLoopGroup(1, newBoss(name())), new DefaultEventLoopGroup(newWorker(name()))));
      this.whenNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
      this.option(UnixChannelOption.SO_REUSEPORT, false);
    } else {
      this.whenNull(bossGroup(), () ->
          this.group(new NioEventLoopGroup(1, newBoss(name())), new DefaultEventLoopGroup(newWorker(name()))));
      this.whenNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
    }
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, false);
    Map<ChannelOption<?>, Object> options = new HashMap<>(options());
    // 默认4MB，数据量较大，缓冲区较小会导致丢包
    options.putIfAbsent(ChannelOption.SO_RCVBUF, (1024 << 10) * 4);
    options.putIfAbsent(ChannelOption.SO_SNDBUF, (1024 << 10) * 4);
    this.options(options);

    return _self();
  }

  /**
   * 设置接收的缓冲区大小
   */
  public UdpNettyServer soRcvbufSize(Integer soRcvbufSize) {
    this.option(ChannelOption.SO_RCVBUF, (1024 << 10) * soRcvbufSize);
    return _self();
  }

  /**
   * 设置发送的缓冲区大小
   */
  public UdpNettyServer soSndbufSize(Integer soSndbufSize) {
    this.option(ChannelOption.SO_SNDBUF, (1024 << 10) * soSndbufSize);
    return _self();
  }


  @Override
  protected ChannelFuture startOnly(ServerBootstrap bootstrap) {
    return super.startOnly(bootstrap);
  }

  /**
   * 服务端类型
   */
  @Override
  public final Type serverType() {
    return Type.UDP;
  }

}
