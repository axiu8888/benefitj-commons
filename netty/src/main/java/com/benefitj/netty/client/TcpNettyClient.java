package com.benefitj.netty.client;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
  private Watchdog watchdog = new Watchdog();
  /**
   * handler
   */
  private ChannelHandler handler;
  /**
   * 线程调度
   */
  private ScheduledExecutorService executor = EventLoop.io();
  /**
   * 执行状态
   */
  private final AtomicBoolean running = new AtomicBoolean(true);

  public TcpNettyClient() {
  }

  @Override
  public TcpNettyClient _self() {
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
    options.put(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
    this.options(options);

    if (autoReconnect()) {
      this.whenNull(super.handler(), () -> super.handler(watchdog));
    } else {
      super.handler(handler);
    }

    return _self();
  }

  @Override
  public ChannelHandler handler() {
    return handler;
  }

  @Override
  public TcpNettyClient handler(ChannelHandler handler) {
    this.handler = handler;
    return _self();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
    if (!isRunning()) {
      throw new IllegalStateException("yet stopped !");
    }
    return bootstrap.connect().addListeners(
        copyListeners(f -> {
          if (autoReconnect() && !f.isSuccess()) {
            // 注册启动时的监听
            watchdog.startReconnectSchedule();
          }
        }, listeners));
  }

  @Override
  public TcpNettyClient stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    if (isRunning()) {
      running.set(false);
      // 注册停止时的监听
      watchdog.stopReconnectSchedule();
      return super.stop(listeners);
    }
    return _self();
  }

  /**
   * 设置自动重连
   *
   * @param autoReconnect  是否自动重连
   * @param reconnectDelay 重连的间隔
   * @param unit           间隔的时间单位
   * @return 返回TCP客户端
   */
  public TcpNettyClient autoReconnect(boolean autoReconnect, int reconnectDelay, TimeUnit unit) {
    this.autoReconnect(autoReconnect);
    this.reconnectPeriod(reconnectDelay);
    this.reconnectPeriodUnit(unit);
    return _self();
  }

  public boolean autoReconnect() {
    return autoReconnect;
  }

  public TcpNettyClient autoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
    return _self();
  }

  public int reconnectPeriod() {
    return watchdog.period;
  }

  public TcpNettyClient reconnectPeriod(int period) {
    this.watchdog.period = period;
    return _self();
  }

  public TimeUnit reconnectPeriodUnit() {
    return watchdog.periodUnit;
  }

  public TcpNettyClient reconnectPeriodUnit(TimeUnit periodUnit) {
    this.watchdog.periodUnit = periodUnit;
    return _self();
  }

  public ScheduledExecutorService executor() {
    return executor;
  }

  public TcpNettyClient executor(ScheduledExecutorService executor) {
    this.executor = executor;
    return _self();
  }

  private boolean isRunning() {
    return this.running.get();
  }

  /**
   * 自动重连的初始化程序
   */
  final class Watchdog extends ChannelInitializer<Channel> implements ActiveHandler.ActiveStateListener {

    /**
     * 客户端连接的时间
     */
    private int period = 30;
    /**
     * 客户端连接的时间单位
     */
    private TimeUnit periodUnit = TimeUnit.SECONDS;
    /**
     * 定时器，每隔固定时间检查客户端状态
     */
    private ScheduledFuture<?> timer;

    public Watchdog() {
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
      ch.pipeline()
          .addLast(ShutdownEventHandler.INSTANCE)
          .addLast(ActiveHandler.newHandler(this))
          .addLast(handler);
    }

    /**
     * 开始重连
     */
    void startReconnectSchedule() {
      synchronized (this) {
        ScheduledFuture<?> t = this.timer;
        if (t == null || t.isCancelled()) {
          // 开始调度
          this.timer = executor().scheduleAtFixedRate(this::reconnect, period, period, periodUnit);
        }
      }
    }

    /**
     * 停止重连
     */
    void stopReconnectSchedule() {
      synchronized (this) {
        ScheduledFuture<?> t = this.timer;
        if (t != null) {
          t.cancel(true);
          this.timer = null;
        }
        CatchUtils.ignore(() -> executor().shutdownNow());
      }
    }

    /**
     * 开始重连任务
     */
    void reconnect() {
      synchronized (this) {
        if (!isConnected()) {
          stateHolder().set(Thread.State.NEW);
          start();
        }
      }
    }

    @Override
    public void onChanged(ActiveHandler handler, ChannelHandlerContext ctx, ActiveHandler.State state) {
      // 立刻重新尝试开启一个新的连接
      if (state == ActiveHandler.State.INACTIVE) {
        if (!executor().isShutdown() && autoReconnect()) {
          ScheduledFuture<?> t = this.timer;
          if (t == null) {
            executor().schedule(this::reconnect, period, TimeUnit.MILLISECONDS);
          }
        }
      } else {
        ScheduledFuture<?> t = this.timer;
        if (t != null) {
          t.cancel(true);
          this.timer = null;
        }
      }
    }
  }


}
