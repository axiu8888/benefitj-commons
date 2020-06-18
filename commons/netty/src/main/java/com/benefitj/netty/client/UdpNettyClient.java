package com.benefitj.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * UDP客户端
 */
public class UdpNettyClient extends AbstractNettyClient<UdpNettyClient> {

  public UdpNettyClient() {
  }

  @Override
  public UdpNettyClient useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.executeWhileNull(this.group(), () -> group(new EpollEventLoopGroup()));
      this.executeWhileNull(this.channelFactory(), () -> channel(EpollDatagramChannel.class));
    } else {
      this.executeWhileNull(this.group(), () -> group(new NioEventLoopGroup()));
      this.executeWhileNull(this.channelFactory(), () -> channel(NioDatagramChannel.class));
    }
    this.executeWhileNull(localAddress(), () -> checkAndResetPort(null));
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, true);
    return self();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
    return bootstrap.bind().addListeners(listeners);
  }

}
