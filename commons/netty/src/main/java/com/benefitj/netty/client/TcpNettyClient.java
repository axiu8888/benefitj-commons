package com.benefitj.netty.client;

import com.benefitj.netty.DefaultThreadFactory;
import com.benefitj.netty.adapter.ChannelShutdownEventHandler;
import com.benefitj.netty.log.Log4jNettyLogger;
import com.benefitj.netty.log.NettyLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * TCP客户端
 */
public class TcpNettyClient extends AbstractNettyClient<TcpNettyClient> {
  static {
    NettyLogger.INSTANCE.setLogger(new Log4jNettyLogger());
  }

  /**
   * 是否自动重连
   */
  private boolean autoReconnect = false;
  /**
   * 重连间隔，默认10秒
   */
  private int reconnectPeriod = 10;
  /**
   * 重连间隔的时间单位，默认是秒
   */
  private TimeUnit reconnectPeriodUnit = TimeUnit.SECONDS;
  /**
   * handler
   */
  private ChannelHandler handler;

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

    Map<ChannelOption<?>, Object> options = new HashMap<>(16);
    options.putAll(options());
    // 默认4MB，数据量较大，缓冲区较小会导致丢包
    options.putIfAbsent(ChannelOption.SO_RCVBUF, (1024 << 10) * 4);
    options.putIfAbsent(ChannelOption.SO_SNDBUF, (1024 << 10) * 4);
    this.options(options);

    if (autoReconnect()) {
      this.executeWhileNull(super.handler(), () -> super.handler(new AutoReconnectChannelInitializer(this)));
    } else {
      super.handler(handler);
    }

    return self();
  }

  @Override
  public ChannelHandler handler() {
    return handler;
  }

  @Override
  public TcpNettyClient handler(ChannelHandler handler) {
    this.handler = handler;
    return self();
  }

  @Override
  protected ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners) {
    return bootstrap.connect().addListeners(listeners);
  }

  @Override
  public TcpNettyClient stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    return super.stop(listeners);
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
    return self();
  }

  public boolean autoReconnect() {
    return autoReconnect;
  }

  public TcpNettyClient autoReconnect(boolean autoReconnect) {
    this.autoReconnect = autoReconnect;
    return self();
  }

  public int reconnectPeriod() {
    return reconnectPeriod;
  }

  public TcpNettyClient reconnectPeriod(int period) {
    this.reconnectPeriod = period;
    return self();
  }

  public TimeUnit reconnectPeriodUnit() {
    return reconnectPeriodUnit;
  }

  public TcpNettyClient reconnectPeriodUnit(TimeUnit periodUnit) {
    this.reconnectPeriodUnit = periodUnit;
    return self();
  }


  /**
   * 自动重连的初始化程序
   */
  static class AutoReconnectChannelInitializer extends ChannelInitializer<Channel> {

    private final NettyLogger log = NettyLogger.INSTANCE;
    /**
     * Netty TCP 客户端
     */
    private final TcpNettyClient client;
    private final int period;
    private final TimeUnit periodUnit;
    /**
     * 重连的线程
     */
    private final ScheduledExecutorService executor;
    /**
     * 定时器，每隔固定时间检查客户端状态
     */
    private ScheduledFuture<?> timer;

    public AutoReconnectChannelInitializer(TcpNettyClient client) {
      this.client = client;
      this.period = client.reconnectPeriod();
      this.periodUnit = client.reconnectPeriodUnit();

      ThreadFactory factory = new DefaultThreadFactory("tcp-", "-reconnect-", true);
      this.executor = Executors.newSingleThreadScheduledExecutor(factory);

      // 注册启动时的监听
      client.addStartListeners(f -> scheduleReconnectService());
      // 注册停止时的监听
      client.addStopListeners(f -> shutdownReconnectService());
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
      ch.pipeline()
          .addLast(ChannelShutdownEventHandler.INSTANCE)
          .addLast(client.handler());
    }

    /**
     * 开始重连
     */
    void scheduleReconnectService() {
      if (timer == null) {
        // 开始调度
        this.timer = executor.scheduleAtFixedRate(this::startReconnectTask, period, period, periodUnit);
      }
    }

    /**
     * 停止重连
     */
    void shutdownReconnectService() {
      log.info("shutdownReconnectService");
      if (timer != null) {
        this.timer.cancel(true);
        this.timer = null;
      }
      this.executor.shutdownNow();
    }

    /**
     * 开始重连任务
     */
    void startReconnectTask() {
      if (client.isConnected()) {
        return;
      }

      if (this.executor.isShutdown()) {
        return;
      }

      this.client.stateHolder().set(Thread.State.NEW);
      this.client.start();
      log.info("startReconnectTask 1..., isConnected: {}", client.isConnected());
    }

  }


}
