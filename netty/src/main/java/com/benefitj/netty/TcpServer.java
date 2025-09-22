package com.benefitj.netty;


import com.benefitj.core.AttributeMap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;


/**
 * TCP服务端
 */
public class TcpServer {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private final List<EventLoopGroup> groups = new ArrayList<>();
  private final ServerBootstrap bootstrap = new ServerBootstrap();

  private Consumer<SocketChannel> channelInitializer;
  private InetSocketAddress localAddress;

  private final AtomicReference<Channel> serverChannel = new AtomicReference<>();
  private final AttributeMap attrs = AttributeMap.newAttributeMap();

  public TcpServer() {
    initOptions(getBootstrap());
  }

  public TcpServer(int port) {
    this();
    setLocalAddress(port);
  }

  public AttributeMap attrs() {
    return attrs;
  }

  public ServerBootstrap getBootstrap() {
    return bootstrap;
  }

  void initAndStart(InetSocketAddress address, GenericFutureListener<? extends Future<?>>... listeners) {
    if (initialized.compareAndSet(false, true)) {
      final NioEventLoopGroup bossGroup = new NioEventLoopGroup();
      final NioEventLoopGroup workerGroup = new NioEventLoopGroup();
      groups.addAll(Arrays.asList(bossGroup, workerGroup));
      Channel channel = getBootstrap()
          .group(bossGroup, workerGroup)
          .channel(NioServerSocketChannel.class)
          .childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
              getChannelInitializer().accept(ch);
            }
          })
          .localAddress(address)
          .bind(address)
          .addListener(f -> log.debug("tcp server start, local port: {}", address.getPort()))
          .addListeners((GenericFutureListener[]) listeners)
          .channel();
      serverChannel.set(channel);
    }
  }

  public boolean isActive() {
    Channel ch = serverChannel.get();
    return ch != null && ch.isActive();
  }

  public TcpServer start(GenericFutureListener<? extends Future<?>>... listeners) {
    if (getChannelInitializer() == null) throw new IllegalStateException("还未设置channelInitializer");
    InetSocketAddress address = getLocalAddress();
    if (address == null) throw new IllegalStateException("需要设置本地监听端口，请设置 localAddress 的值");
    initAndStart(address, listeners);
    return this;
  }

  public TcpServer stop(GenericFutureListener<? extends Future<?>>... listeners) {
    if (initialized.compareAndSet(true, false)) {
      InetSocketAddress address = (InetSocketAddress) serverChannel.getAndSet(null).localAddress();
      groups.remove(0)
          .shutdownGracefully()
          .addListener(f -> log.debug("tcp server stop, local port: {}", address.getPort()))
          .addListeners((GenericFutureListener[]) listeners);
      groups.forEach(EventExecutorGroup::shutdownGracefully);
      groups.clear();
    }
    return this;
  }

  public InetSocketAddress getLocalAddress() {
    return localAddress;
  }

  public TcpServer setLocalAddress(int port) {
    return setLocalAddress(new InetSocketAddress(port));
  }

  public TcpServer setLocalAddress(InetSocketAddress localAddress) {
    this.localAddress = localAddress;
    return this;
  }

  public Consumer<SocketChannel> getChannelInitializer() {
    return channelInitializer;
  }

  public TcpServer setChannelInitializer(Consumer<SocketChannel> channelInitializer) {
    this.channelInitializer = channelInitializer;
    return this;
  }


  public static final Map<ChannelOption<?>, Object> DEFAULT_OPTIONS;
  public static final Map<ChannelOption<?>, Object> DEFAULT_CHILD_OPTIONS;
  static {
    // options
    Map<ChannelOption<?>, Object> optionMap = new HashMap<>();
    optionMap.put(ChannelOption.SO_REUSEADDR, false);
    optionMap.put(ChannelOption.SO_BACKLOG, 1024);
    DEFAULT_OPTIONS = Collections.unmodifiableMap(optionMap);

    // childOptions
    Map<ChannelOption<?>, Object> childOptionMap = new HashMap<>();
    childOptionMap.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    childOptionMap.put(ChannelOption.SO_RCVBUF, 4 * (1024 << 4));
    childOptionMap.put(ChannelOption.SO_SNDBUF, 4 * (1024 << 4));
    childOptionMap.put(ChannelOption.TCP_NODELAY, true);
    childOptionMap.put(ChannelOption.SO_KEEPALIVE, true);
    childOptionMap.put(ChannelOption.AUTO_READ, true);
    childOptionMap.put(ChannelOption.AUTO_CLOSE, true);
    childOptionMap.put(ChannelOption.ALLOW_HALF_CLOSURE, true);
    childOptionMap.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
    DEFAULT_CHILD_OPTIONS = Collections.unmodifiableMap(childOptionMap);
  }

  public static void initOptions(ServerBootstrap bootstrap) {
    DEFAULT_OPTIONS.forEach((option, value) -> bootstrap.option((ChannelOption) option, value));
    DEFAULT_CHILD_OPTIONS.forEach((option, value) -> bootstrap.childOption((ChannelOption) option, value));
  }

}
