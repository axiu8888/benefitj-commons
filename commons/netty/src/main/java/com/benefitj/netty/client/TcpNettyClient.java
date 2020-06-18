package com.benefitj.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

/**
 * TCP客户端
 */
public class TcpNettyClient extends AbstractNettyClient<TcpNettyClient> {

  public TcpNettyClient() {
  }

  @Override
  public TcpNettyClient self() {
    return this;
  }

  @Override
  public TcpNettyClient useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.group(new EpollEventLoopGroup());
      this.channel(EpollSocketChannel.class);
    } else {
      this.executeWhileNull(group(), () -> group(new NioEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> channel(NioSocketChannel.class));
    }
    return self();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
    return bootstrap.connect().addListeners(listeners);
  }
}
