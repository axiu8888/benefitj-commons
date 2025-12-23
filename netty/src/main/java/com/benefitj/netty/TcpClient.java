package com.benefitj.netty;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.AutoConnectTimer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * TCP客户端
 */
public class TcpClient {

  protected final Logger log = LoggerFactory.getLogger(getClass());

  private final AtomicBoolean initialized = new AtomicBoolean(false);
  private final List<EventLoopGroup> groups = new ArrayList<>();
  private final Bootstrap bootstrap = new Bootstrap();

  private Consumer<SocketChannel> channelInitializer;
  private InetSocketAddress remoteAddress;

  private final AtomicReference<Channel> mainChannel = new AtomicReference<>();
  private final AttributeMap attrs = AttributeMap.newAttributeMap();

  private final AutoConnectTimer autoConnectTimer = new AutoConnectTimer(false, Duration.ofSeconds(30));
  private final AutoConnectTimer.Connector connector = new AutoConnectTimer.Connector() {
    @Override
    public boolean isConnected() {
      return isActive();
    }

    @Override
    public void doConnect() {
      connect0();
    }
  };

  public TcpClient() {
    initOptions(getBootstrap());
  }

  public TcpClient(InetSocketAddress remote) {
    this();
    setRemoteAddress(remote);
  }

  public AttributeMap attrs() {
    return attrs;
  }

  public Bootstrap getBootstrap() {
    return bootstrap;
  }

  Channel connect0(GenericFutureListener<? extends Future<?>>... listeners) {
    Channel channel = getBootstrap()
        .handler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            ch
                .pipeline()
                .addLast(new Watchdog());
            getChannelInitializer().accept(ch);
          }
        })
        .remoteAddress(remoteAddress)
        .connect()
        .addListeners((GenericFutureListener[]) listeners)
        .channel();
    mainChannel.set(channel);
    return channel;
  }

  void initAndStart(InetSocketAddress remoteAddress, GenericFutureListener<? extends Future<?>>... listeners) {
    if (initialized.compareAndSet(false, true)) {
      if (groups.isEmpty()) {
        groups.addAll(Arrays.asList(new NioEventLoopGroup(1)));
      }
      getBootstrap()
          .group(groups.get(0))
          .channel(NioSocketChannel.class);
      connect0(copyListeners(f -> {
        if (!isActive()) {
          autoConnectTimer.start(connector);
        }
        log.debug("tcp client start, remote: {}, success: {}", remoteAddress, f.isSuccess());
      }, (GenericFutureListener[]) listeners));
    }
  }

  public boolean isActive() {
    Channel ch = mainChannel.get();
    return ch != null && ch.isActive();
  }

  public TcpClient start(GenericFutureListener<? extends Future<?>>... listeners) {
    if (getChannelInitializer() == null) throw new IllegalStateException("还未设置channelInitializer");
    InetSocketAddress remote = getRemoteAddress();
    if (remote == null) throw new IllegalStateException("请设置 remoteAddress 的值(远程地址和端口)");
    initAndStart(remote, listeners);
    return this;
  }

  public TcpClient stop(GenericFutureListener<? extends Future<?>>... listeners) {
    if (initialized.compareAndSet(true, false)) {
      try {
        Channel channel = mainChannel.getAndSet(null);
        InetSocketAddress remote = (InetSocketAddress) channel.remoteAddress();
        groups.remove(0)
            .shutdownGracefully()
            .addListener(f -> log.debug("tcp client stop, remoteAddress: {}", remote))
            .addListeners((GenericFutureListener[]) listeners);
        groups.forEach(EventExecutorGroup::shutdownGracefully);
        groups.clear();
        channel.close();//关闭
      } finally {
        autoConnectTimer.stop();
      }
    }
    return this;
  }

  public InetSocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  public TcpClient setRemoteAddress(InetSocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
    return this;
  }

  public TcpClient setAutoConnect(boolean auto, Duration interval) {
    this.autoConnectTimer.setAutoConnect(auto, interval);
    return this;
  }

  public Consumer<SocketChannel> getChannelInitializer() {
    return channelInitializer;
  }

  public TcpClient setChannelInitializer(Consumer<SocketChannel> channelInitializer) {
    this.channelInitializer = channelInitializer;
    return this;
  }

  public TcpClient write(Object msg) {
    return write(msg, NOTHING);
  }

  public TcpClient write(Object msg, BiConsumer<Boolean, Throwable> result) {
    Channel ch = mainChannel.get();
    if (ch != null && ch.isActive()) {
      ch.writeAndFlush(msg).addListener(f -> result.accept(f.isSuccess(), f.cause()));
    } else {
      result.accept(false, new IllegalStateException("还未连接到服务端: " + getRemoteAddress()));
    }
    return this;
  }


  protected class Watchdog extends SimpleChannelInboundHandler {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      super.channelActive(ctx);
      autoConnectTimer.stop();//停止自动连接
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      super.channelInactive(ctx);
      autoConnectTimer.start(connector);//开始自动连接
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
      //ignore...
    }
  }

  public static final BiConsumer<Boolean, Throwable> NOTHING = (success, cause) -> {/* ^_^ */};
  public static final Map<ChannelOption<?>, Object> DEFAULT_OPTIONS;

  static {
    // options
    Map<ChannelOption<?>, Object> options = new HashMap<>(16);
    options.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    // 默认4MB，数据量较大，缓冲区较小会导致丢包
    options.putIfAbsent(ChannelOption.SO_RCVBUF, (1024 << 10) * 4);
    options.putIfAbsent(ChannelOption.SO_SNDBUF, (1024 << 10) * 4);
    options.put(ChannelOption.TCP_NODELAY, true);
    options.put(ChannelOption.SO_KEEPALIVE, true);
    options.put(ChannelOption.AUTO_READ, true);
    options.put(ChannelOption.AUTO_CLOSE, true);
    options.put(ChannelOption.ALLOW_HALF_CLOSURE, true);
    options.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);//客户端连接超时，3秒
    DEFAULT_OPTIONS = Collections.unmodifiableMap(options);
  }

  public static void initOptions(Bootstrap bootstrap) {
    DEFAULT_OPTIONS.forEach((option, value) -> bootstrap.option((ChannelOption) option, value));
  }

  public static GenericFutureListener<? extends Future<Void>>[] copyListeners(GenericFutureListener<? extends Future<Void>> listener0,
                                                                              GenericFutureListener<? extends Future<Void>>... listeners) {
    final List<GenericFutureListener<?>> list = new LinkedList<>();
    if (listeners != null) {
      for (GenericFutureListener<?> l : listeners) {
        if (l != null && !list.contains(l)) {
          list.add(l);
        }
      }
    }
    if (listener0 != null) {
      list.add(listener0);
    }
    return list.toArray(new GenericFutureListener[0]);
  }
}
