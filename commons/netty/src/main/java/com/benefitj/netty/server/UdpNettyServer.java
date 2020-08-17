package com.benefitj.netty.server;

import com.benefitj.netty.server.channel.NioDatagramServerChannel;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.unix.UnixChannelOption;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * UDP服务
 */
public class UdpNettyServer extends AbstractNettyServer<UdpNettyServer> {

  private long readTimeout = 60;
  private long writeTimeout = 0;
  private TimeUnit timeoutUnit = TimeUnit.SECONDS;

  public UdpNettyServer() {
    super();
  }

  @Override
  public UdpNettyServer useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.executeWhileNull(bossGroup(), () ->
          this.group(new EpollEventLoopGroup(1), new DefaultEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
      this.option(UnixChannelOption.SO_REUSEPORT, true);
    } else {
      this.executeWhileNull(bossGroup(), () ->
          this.group(new NioEventLoopGroup(1), new DefaultEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> this.channel(NioDatagramServerChannel.class));
    }
    this.option(ChannelOption.SO_BROADCAST, true);
    this.option(ChannelOption.SO_REUSEADDR, true);
    Map<ChannelOption<?>, Object> options = new HashMap<>(10);
    options.putAll(options());
    // 默认8个KB
    options.putIfAbsent(ChannelOption.SO_RCVBUF, 1024 * 8);
    this.options(options);

    return self();
  }

  @Override
  protected ChannelFuture startOnly(ServerBootstrap bootstrap) {
    return super.startOnly(bootstrap).addListener(future ->
        ((NioDatagramServerChannel) getServeChannel()).idle(readTimeout(), writeTimeout(), timeoutUnit()));
  }

  public long readTimeout() {
    return readTimeout;
  }

  public UdpNettyServer readTimeout(long readTimeout) {
    this.readTimeout = readTimeout;
    return self();
  }

  public long writeTimeout() {
    return writeTimeout;
  }

  public UdpNettyServer writeTimeout(long writeTimeout) {
    this.writeTimeout = writeTimeout;
    return self();
  }

  public TimeUnit timeoutUnit() {
    return timeoutUnit;
  }

  public UdpNettyServer timeoutUnit(TimeUnit timeoutUnit) {
    this.timeoutUnit = timeoutUnit;
    return self();
  }

  public UdpNettyServer idle(long read, long write, TimeUnit unit) {
    this.readTimeout(read);
    this.writeTimeout(write);
    this.timeoutUnit(unit);
    return self();
  }

}
