package com.benefitj.netty.server;

import com.benefitj.netty.AbstractNetty;
import com.benefitj.netty.server.channel.NioDatagramServerChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.ServerBootstrapConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;
import java.util.Map;

/**
 * UDP服务
 */
public class UdpNettyServer extends AbstractNetty<ServerBootstrap, UdpNettyServer> implements INettyServer<UdpNettyServer> {

  public UdpNettyServer() {
    super();
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.executeWhileNotNull(bossGroup(), () -> this.group(new EpollEventLoopGroup()));
      this.executeWhileNotNull(workerGroup(), () -> this.group(bossGroup(), new EpollEventLoopGroup()));
      this.channel(NioDatagramServerChannel.class);
      this.option(UnixChannelOption.SO_REUSEPORT, true);
    } else {
      this.executeWhileNotNull(bossGroup(), () -> this.group(new NioEventLoopGroup()));
      this.executeWhileNotNull(workerGroup(), () -> this.group(bossGroup(), new NioEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
    }
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, true);
    return self();
  }

  @Override
  public ServerBootstrapConfig config() {
    return bootstrap().config();
  }

  /**
   * 创建的 ServerBootstrap 实例
   */
  @Override
  public ServerBootstrap createBootstrap() {
    return new ServerBootstrap();
  }

  @Override
  protected ChannelFuture startOnly(ServerBootstrap bootstrap) {
    return bootstrap.bind().addListener(f -> {
      SocketAddress socketAddress = bootstrap.config().localAddress();
      if (f.isSuccess()) {
        log.info("Netty server started at localAddress: " + socketAddress);
      } else {
        log.info("Netty server start failed at localAddress: " + socketAddress);
      }
    });
  }

  @Override
  public UdpNettyServer stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    GenericFutureListener<? extends Future<Void>> l = f ->
        log.info("Netty server stop at localAddress: " + config().localAddress());
    super.stop(copyListeners(l, listeners));
    if (!isStopped()) {
      shutdownGracefully(workerGroup(), true);
    }
    return self();
  }


  @Override
  public UdpNettyServer group(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
    bootstrap().group(bossGroup, workerGroup);
    return self();
  }

  @Override
  public EventLoopGroup bossGroup() {
    return config().group();
  }

  @Override
  public EventLoopGroup workerGroup() {
    return config().childGroup();
  }

  @Override
  public <T> UdpNettyServer childOption(ChannelOption<T> childOption, T value) {
    bootstrap().childOption(childOption, value);
    return self();
  }

  @Override
  public UdpNettyServer childOptions(Map<ChannelOption<?>, Object> ops) {
    ops.forEach((option, value) -> childOption((ChannelOption) option, value));
    return self();
  }

  @Override
  public Map<ChannelOption<?>, Object> childOptions() {
    return config().childOptions();
  }

  @Override
  public <T> UdpNettyServer childAttr(AttributeKey<T> key, T value) {
    bootstrap().childAttr(key, value);
    return self();
  }

  @Override
  public UdpNettyServer childAttrs(Map<AttributeKey<?>, Object> childAttrs) {
    childAttrs.forEach((key, o) -> childAttr((AttributeKey) key, o));
    return self();
  }

  @Override
  public Map<AttributeKey<?>, Object> childAttrs() {
    return config().childAttrs();
  }

  @Override
  public ChannelHandler childHandler() {
    return config().childHandler();
  }

  @Override
  public UdpNettyServer childHandler(ChannelHandler childHandler) {
    bootstrap().childHandler(childHandler);
    return self();
  }

}
