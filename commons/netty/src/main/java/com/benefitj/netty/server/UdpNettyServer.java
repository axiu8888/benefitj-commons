package com.benefitj.netty.server;

import com.benefitj.netty.server.channel.NioDatagramServerChannel;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
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
  public UdpNettyServer useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.executeWhileNull(bossGroup(), () -> this.group(new EpollEventLoopGroup()));
      this.executeWhileNull(workerGroup(), () -> this.group(bossGroup(), new DefaultEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
      this.option(UnixChannelOption.SO_REUSEPORT, true);
    } else {
      this.executeWhileNull(bossGroup(), () -> this.group(new NioEventLoopGroup()));
      this.executeWhileNull(workerGroup(), () -> this.group(bossGroup(), new DefaultEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
    }
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, true);

    Map<ChannelOption<?>, Object> options = new HashMap<>(10);
    options.putAll(options());
    // 默认8个KB
    options.putIfAbsent(ChannelOption.SO_RCVBUF, 1024 * 8);
    options(options);
    return self();
  }

}
