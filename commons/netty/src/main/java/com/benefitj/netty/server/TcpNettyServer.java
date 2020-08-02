package com.benefitj.netty.server;

import com.benefitj.netty.AbstractNetty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.ServerBootstrapConfig;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.unix.UnixChannelOption;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * TCP 服务
 */
public class TcpNettyServer extends AbstractNetty<ServerBootstrap, TcpNettyServer> implements INettyServer<TcpNettyServer> {

  public static final Map<ChannelOption<?>, Object> DEFAULT_OPTIONS;
  public static final Map<ChannelOption<?>, Object> DEFAULT_CHILD_OPTIONS;

  static {
    // options
    Map<ChannelOption<?>, Object> optionMap = new HashMap<>();
    optionMap.put(ChannelOption.SO_REUSEADDR, true);
    optionMap.put(ChannelOption.SO_BACKLOG, 1024);
    DEFAULT_OPTIONS = Collections.unmodifiableMap(optionMap);

    // childOptions
    Map<ChannelOption<?>, Object> childOptionMap = new HashMap<>();
    childOptionMap.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    childOptionMap.put(ChannelOption.SO_RCVBUF, 1024 << 4);
    childOptionMap.put(ChannelOption.SO_SNDBUF, 1024 << 4);
    childOptionMap.put(ChannelOption.TCP_NODELAY, true);
    childOptionMap.put(ChannelOption.SO_KEEPALIVE, true);
    childOptionMap.put(ChannelOption.AUTO_READ, true);
    childOptionMap.put(ChannelOption.AUTO_CLOSE, true);
    childOptionMap.put(ChannelOption.ALLOW_HALF_CLOSURE, true);
    childOptionMap.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
    DEFAULT_CHILD_OPTIONS = Collections.unmodifiableMap(childOptionMap);
  }

  public TcpNettyServer() {
    super();
  }

  @Override
  public ServerBootstrapConfig config() {
    return bootstrap().config();
  }

  @Override
  public TcpNettyServer useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.executeWhileNotNull(bossGroup(), () -> this.group(new EpollEventLoopGroup()));
      this.executeWhileNotNull(workerGroup(), () -> this.group(bossGroup(), new EpollEventLoopGroup()));
      this.channel(EpollServerSocketChannel.class);
      this.option(UnixChannelOption.SO_REUSEPORT, true);
    } else {
      this.executeWhileNotNull(bossGroup(), () -> this.group(new NioEventLoopGroup()));
      this.executeWhileNotNull(workerGroup(), () -> this.group(bossGroup(), new NioEventLoopGroup()));
      this.executeWhileNull(channelFactory(), () -> channel(NioServerSocketChannel.class));
    }

    // add options
    Map<ChannelOption<?>, Object> oMap = DEFAULT_OPTIONS;
    Map<ChannelOption<?>, Object> options = new HashMap<>(oMap.size() + 10);
    options.putAll(oMap);
    options.putAll(options());
    options(options);

    // add child options
    Map<ChannelOption<?>, Object> coMap = DEFAULT_CHILD_OPTIONS;
    Map<ChannelOption<?>, Object> childOptions = new HashMap<>(coMap.size() + 10);
    childOptions.putAll(coMap);
    childOptions.putAll(childOptions());
    childOptions(childOptions);
    return self();
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
  public TcpNettyServer stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    GenericFutureListener<? extends Future<Void>> l = f ->
        log.info("Netty server stop at localAddress: " + config().localAddress());
    super.stop(copyListeners(l, listeners));
    if (!isStopped()) {
      shutdownGracefully(workerGroup(), true);
    }
    return self();
  }


  @Override
  public TcpNettyServer group(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
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
  public <T> TcpNettyServer childOption(ChannelOption<T> childOption, T value) {
    bootstrap().childOption(childOption, value);
    return self();
  }

  @Override
  public TcpNettyServer childOptions(Map<ChannelOption<?>, Object> ops) {
    ops.forEach((option, value) -> childOption((ChannelOption) option, value));
    return self();
  }

  @Override
  public Map<ChannelOption<?>, Object> childOptions() {
    return config().childOptions();
  }

  @Override
  public <T> TcpNettyServer childAttr(AttributeKey<T> key, T value) {
    bootstrap().childAttr(key, value);
    return self();
  }

  @Override
  public TcpNettyServer childAttrs(Map<AttributeKey<?>, Object> childAttrs) {
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
  public TcpNettyServer childHandler(ChannelHandler childHandler) {
    bootstrap().childHandler(childHandler);
    return self();
  }
}
