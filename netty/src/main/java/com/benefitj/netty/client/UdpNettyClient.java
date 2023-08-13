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

import java.util.HashMap;
import java.util.Map;

/**
 * UDP客户端
 */
public class UdpNettyClient extends AbstractNettyClient<UdpNettyClient> {

  public UdpNettyClient() {
  }

  @Override
  protected UdpNettyClient useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.whenNull(this.group(), () -> group(new EpollEventLoopGroup(newThreadFactory(name(), "-client-", false))));
      this.whenNull(this.channelFactory(), () -> channel(EpollDatagramChannel.class));
    } else {
      this.whenNull(this.group(), () -> group(new NioEventLoopGroup()));
      this.whenNull(this.channelFactory(), () -> channel(NioDatagramChannel.class));
    }
    this.whenNull(localAddress(), () -> checkAndResetPort(null));
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, true);

    Map<ChannelOption<?>, Object> options = new HashMap<>(16);
    options.putAll(options());
    // 默认4MB，数据量较大，缓冲区较小会导致丢包
    options.putIfAbsent(ChannelOption.SO_RCVBUF, (1024 << 10) * 4);
    options.putIfAbsent(ChannelOption.SO_SNDBUF, (1024 << 10) * 4);
    this.options(options);

    return _self();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
    return bootstrap.bind().syncUninterruptibly().addListeners(listeners);
  }

}
