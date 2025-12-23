package com.benefitj.netty.client;

import com.benefitj.core.AutoConnectTimer;
import com.benefitj.core.NetworkUtils;
import com.benefitj.netty.handler.ActiveHandler;
import com.benefitj.netty.handler.ShutdownEventHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


/**
 * TCP客户端
 */
public class TcpNettyClient extends AbstractNettyClient<TcpNettyClient> {

  /**
   * 是否自动重连
   */
  private boolean autoReconnect = false;
  /**
   * watchdog
   */
  private final Watchdog watchdog = new Watchdog();
  /**
   * handler
   */
  private ChannelHandler handler;

  public TcpNettyClient() {
  }

  public TcpNettyClient(InetSocketAddress remoteAddress) {
    this(remoteAddress, false);
  }

  public TcpNettyClient(InetSocketAddress remoteAddress, boolean autoReconnect) {
    this.remoteAddress(remoteAddress);
    this.autoReconnect = autoReconnect;
  }

  @Override
  public TcpNettyClient _self_() {
    return this;
  }

  @Override
  protected TcpNettyClient useDefaultConfig() {
    if (useLinuxNativeEpoll()) {
      this.group(new EpollEventLoopGroup());
      this.channel(EpollSocketChannel.class);
    } else {
      this.whenNull(group(), () -> group(new NioEventLoopGroup(newThreadFactory(name(), "-client-", false))));
      this.whenNull(channelFactory(), () -> channel(NioSocketChannel.class));
    }

    Map<ChannelOption<?>, Object> options = new HashMap<>(16);
    options.putAll(options());
    options.put(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
    // 默认4MB，数据量较大，缓冲区较小会导致丢包
    options.putIfAbsent(ChannelOption.SO_RCVBUF, (1024 << 10) * 4);
    options.putIfAbsent(ChannelOption.SO_SNDBUF, (1024 << 10) * 4);
    options.put(ChannelOption.TCP_NODELAY, true);
    options.put(ChannelOption.SO_KEEPALIVE, true);
    options.put(ChannelOption.AUTO_READ, true);
    options.put(ChannelOption.AUTO_CLOSE, true);
    options.put(ChannelOption.ALLOW_HALF_CLOSURE, true);
    options.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000);
    this.options(options);

    if (autoReconnect()) {
      this.whenNull(super.handler(), () -> super.handler(watchdog));
    } else {
      super.handler(handler);
    }

    return _self_();
  }

  public TcpNettyClient setSoRcvBuf(int size) {
    bootstrap().option(ChannelOption.SO_RCVBUF, size);
    return _self_();
  }

  public TcpNettyClient setSoSndBuf(int size) {
    bootstrap().option(ChannelOption.SO_SNDBUF, size);
    return _self_();
  }

  @Override
  public ChannelHandler handler() {
    return handler;
  }

  @Override
  public TcpNettyClient handler(ChannelHandler handler) {
    this.handler = handler;
    return _self_();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
    return connect(bootstrap, listeners);
  }

  @Override
  public TcpNettyClient stop() {
    return this.stop(EMPTY_LISTENER);
  }

  @Override
  public TcpNettyClient stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    autoConnectTimer.setAutoConnect(false);
    autoConnectTimer.stop();
    return super.stop(listeners);
  }

  /**
   * 设置自动重连
   *
   * @param autoReconnect     是否自动重连
   * @param reconnectInterval 重连的间隔
   * @return 返回TCP客户端
   */
  public TcpNettyClient autoReconnect(boolean autoReconnect, Duration reconnectInterval) {
    this.autoReconnect(autoReconnect);
    this.reconnectInterval(reconnectInterval);
    return _self_();
  }

  public boolean autoReconnect() {
    return autoReconnect;
  }

  public TcpNettyClient autoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
    this.autoConnectTimer.setAutoConnect(autoReconnect);
    return _self_();
  }

  public Duration reconnectInterval() {
    return autoConnectTimer.getInterval();
  }

  public TcpNettyClient reconnectInterval(Duration interval) {
    this.autoConnectTimer.setInterval(interval);
    return _self_();
  }

  ChannelFuture connect(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>> ...listeners) {
    if (NetworkUtils.isConnectable(remoteAddress(), 500)) {
      try {
        autoConnectTimer.setAutoConnect(autoReconnect);
        ChannelFuture future = bootstrap.connect(remoteAddress());
        future.addListener(f -> autoConnectTimer.start(connector)).sync().addListeners(listeners);
        if (future.isSuccess()) {
          setMainChannel(future.channel());
        }
        return future;
      } catch (Exception e) {
        log.error("Failed to connect to TcpNettyClient: " + e.getMessage(), e);
      }
    }
    return null;
  }

  /**
   * 自动重连器
   */
  private final AutoConnectTimer autoConnectTimer = new AutoConnectTimer();
  private final AutoConnectTimer.Connector connector = new AutoConnectTimer.Connector() {

    @Override
    public boolean isConnected() {
      return TcpNettyClient.this.isConnected();
    }

    @Override
    public void doConnect() {
      connect(bootstrap(), f -> {
        log.info("Connected to TcpNettyClient: {}, {}", remoteAddress(), f.isSuccess());
      });
    }
  };

  /**
   * 自动重连的初始化程序
   */
  final class Watchdog extends ChannelInitializer<Channel> implements ActiveHandler.ActiveStateListener {

    public Watchdog() {
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
      ch.pipeline()
          .addLast(ShutdownEventHandler.INSTANCE)
          .addLast(ActiveHandler.newHandler(this))
          .addLast(handler);
    }

    @Override
    public void onChanged(ActiveHandler handler, ChannelHandlerContext ctx, ActiveHandler.State state) {
      // 立刻重新尝试开启一个新的连接
      if (state == ActiveHandler.State.INACTIVE) {
        autoConnectTimer.start(connector);
      } else {
        autoConnectTimer.stop();
      }
    }

  }

}
