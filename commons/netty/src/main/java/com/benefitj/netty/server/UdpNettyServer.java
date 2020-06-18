package com.benefitj.netty.server;

import com.benefitj.netty.AbstractNetty;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.EpollDatagramChannel;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.unix.UnixChannelOption;

import java.net.SocketAddress;

/**
 * UDP服务
 */
public class UdpNettyServer extends AbstractNetty<Bootstrap, UdpNettyServer> {

  public UdpNettyServer() {
    super();
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.group(new EpollEventLoopGroup());
      this.channel(EpollDatagramChannel.class);
      this.option(UnixChannelOption.SO_REUSEPORT, true);
    } else {
      this.executeWhileNull(group(), () -> this.group(new NioEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> this.channel(NioDatagramChannel.class));
    }
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, true);
    return self();
  }

  /**
   * Bootstrap
   */
  @Override
  public Bootstrap createBootstrap() {
    return new Bootstrap();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap) {
    return bootstrap.bind().addListener(f -> {
      SocketAddress socketAddress = bootstrap.config().localAddress();
      if (f.isSuccess()) {
        log.info("Netty server started at localAddress: " + socketAddress);
      } else {
        log.info("Netty server start failed at localAddress: " + socketAddress);
      }
    });
  }

}
