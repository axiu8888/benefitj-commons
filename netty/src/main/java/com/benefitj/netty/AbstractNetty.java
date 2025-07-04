package com.benefitj.netty;

import com.benefitj.core.functions.IConsumer;
import com.benefitj.core.log.ILogger;
import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 抽象的Netty父类
 *
 * @param <B> 启动器
 * @param <S> 当前实现 self
 */
public abstract class AbstractNetty<B extends AbstractBootstrap<B, ? extends Channel>, S extends AbstractNetty<B, S>>
    implements INetty<B, S> {

  protected static final AtomicLong NAME_GENERATOR = new AtomicLong(0);

  protected static String defaultName(Class<?> klass) {
    return klass.getSimpleName() + "-" + NAME_GENERATOR.incrementAndGet();
  }

  protected final ILogger log = NettyLogger.get();

  /**
   * 服务名称
   */
  private String name = defaultName(getClass());
  /**
   * 启动器
   */
  private volatile B bootstrap;
  /**
   * 默认使用Linux的Epoll，如果可用
   */
  private volatile boolean useLinuxNativeEpoll = false;
  /**
   * 主通道，启动后返回的通道
   */
  private volatile Channel mainChannel;
  /**
   * 执行状态
   */
  private final AtomicReference<Thread.State> stateHolder = new AtomicReference<>(Thread.State.NEW);

  public AbstractNetty() {
  }

  protected S _self_() {
    return (S) this;
  }

  /**
   * 设置服务名
   *
   * @param name 名称
   * @return 返回当前对象
   */
  @Override
  public S name(String name) {
    this.name = name;
    return _self_();
  }

  /**
   * @return 返回服务名
   */
  @Override
  public String name() {
    return name;
  }

  /**
   * @return 返回创建的AbstractBootstrap实例
   */
  public abstract B createBootstrap();

  @Override
  public final B bootstrap() {
    B b = this.bootstrap;
    if (b == null) {
      synchronized (this) {
        if ((b = this.bootstrap) == null) {
          this.bootstrap = (b = createBootstrap());
        }
      }
    }
    return b;
  }

  @Override
  public AbstractBootstrapConfig<B, ? extends Channel> config() {
    return bootstrap().config();
  }

  /**
   * 默认配置
   */
  protected S useDefaultConfig() {
    return _self_();
  }

  private Channel start0(GenericFutureListener<? extends Future<Void>>... listeners) {
    if (expectAndSet(Thread.State.NEW, Thread.State.RUNNABLE)) {
      B b = bootstrap();
      useDefaultConfig();
      // 使用默认端口
      whenNotNull(localAddress(), () -> localAddress(checkAndResetPort(localAddress())));
      ChannelFuture cf = startOnly(b);
      if (cf != null) {
        cf.addListeners(listeners);
        setMainChannel(cf.channel());
      }
    } else {
      final Channel ch = getMainChannel();
      if (ch != null) {
        if (ch.isActive()) {
          ch.newSucceededFuture().addListeners(listeners);
        } else {
          ch.newFailedFuture(new IllegalStateException("yet stopped !")).addListeners(listeners);
        }
      }
    }
    return getMainChannel();
  }

  /**
   * @return 启动
   */
  @Override
  public ChannelFuture startForAwait() {
    return startForAwait(EMPTY_LISTENER);
  }

  @Override
  public ChannelFuture startForAwait(GenericFutureListener<? extends Future<Void>>... listeners) {
    return start0(listeners).closeFuture();
  }

  @Override
  public S start() {
    return start(EMPTY_LISTENER);
  }

  @Override
  public S start(GenericFutureListener<? extends Future<Void>>... listeners) {
    start0(listeners);
    return _self_();
  }

  /**
   * 启动，但不调用sync/await等方法
   *
   * @param bootstrap 启动器
   * @return 返回ChannelFuture
   */
  protected abstract ChannelFuture startOnly(B bootstrap);

  @Override
  public S stop() {
    return stop(EMPTY_LISTENER);
  }

  @Override
  public S stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    Thread.State newState = Thread.State.TERMINATED;
    if (expectAndSet(Thread.State.RUNNABLE, newState) || expectAndSet(Thread.State.NEW, newState)) {
      // 停止
      shutdownGracefully(group(), true
          , copyListeners(f -> setMainChannel(null), listeners));
    } else {
      final Channel c = getMainChannel();
      if (c != null) {
        if (c.isActive()) {
          // 停止
          c.close();
        }
        if (c.isActive()) {
          c.newFailedFuture(new IllegalStateException("not yet stop !")).addListeners(listeners);
        } else {
          c.newSucceededFuture().addListeners(listeners);
        }
      }
    }
    return _self_();
  }

  @Override
  public Channel getMainChannel() {
    return mainChannel;
  }

  @Override
  public S setMainChannel(Channel ch) {
    this.mainChannel = ch;
    return _self_();
  }

  public void closeMainChannel() {
    Channel ch = getMainChannel();
    if (ch != null && ch.isActive()) {
      ch.close();
    }
  }

  /**
   * 设置端口
   *
   * @param port 端口
   * @return 返回当前对象
   */
  @Override
  public S localAddress(int port) {
    InetSocketAddress address = new InetSocketAddress(port);
    return localAddress(address);
  }

  /**
   * 设置端口
   *
   * @param address 主机地址
   * @return 返回当前对象
   */
  @Override
  public S localAddress(SocketAddress address) {
    this.bootstrap().localAddress(address);
    return _self_();
  }

  /**
   * @return 返回绑定本地端口的SocketAddress对象
   */
  @Override
  public SocketAddress localAddress() {
    return this.config().localAddress();
  }

  /**
   * 设置端口
   *
   * @param host 主机
   * @param port 端口
   * @return 返回当前对象
   */
  @Override
  public S remoteAddress(String host, int port) {
    InetSocketAddress address = new InetSocketAddress(host, port);
    return remoteAddress(address);
  }

  /**
   * 远程主机地址
   *
   * @param address 地址
   * @return 返回当前对象
   */
  @Override
  public S remoteAddress(SocketAddress address) {
    final B b = this.bootstrap();
    if (b instanceof Bootstrap) {
      ((Bootstrap) b).remoteAddress(address);
    } else {
      throw new UnsupportedOperationException("不支持的操作");
    }
    return _self_();
  }

  /**
   * 远程主机地址
   */
  @Override
  public SocketAddress remoteAddress() {
    final B b = this.bootstrap();
    if (b instanceof Bootstrap) {
      return ((Bootstrap) b).config().remoteAddress();
    }
    return null;
  }

  /**
   * 设置线程组
   *
   * @param group 线程组
   * @return 返回当前对象
   */
  @Override
  public S group(EventLoopGroup group) {
    this.bootstrap().group(group);
    return _self_();
  }

  /**
   * 获取主线程组
   */
  @Override
  public EventLoopGroup group() {
    return this.config().group();
  }

  /**
   * 实例化通道的工厂对象
   *
   * @param channelFactory 通道的工厂对象
   * @return 返回当前对象
   */
  @Override
  public S channelFactory(ChannelFactory<? extends Channel> channelFactory) {
    this.bootstrap().channelFactory((io.netty.bootstrap.ChannelFactory) channelFactory);
    return _self_();
  }

  /**
   * @return 返回ChannelFactory对象
   */
  @Override
  public ChannelFactory<? extends Channel> channelFactory() {
    return (ChannelFactory<? extends Channel>) config().channelFactory();
  }

  @Override
  public S channel(Class<? extends Channel> channelClass) {
    channelFactory(new ReflectiveChannelFactory<>(channelClass));
    return _self_();
  }

  /**
   * 处理器
   *
   * @param handler 处理器对象
   * @return 返回当前对象
   */
  @Override
  public S handler(ChannelHandler handler) {
    this.bootstrap().handler(handler);
    return _self_();
  }

  /**
   * 通道处理器
   *
   * @return 返回ChannelHandler对象
   */
  @Override
  public ChannelHandler handler() {
    return config().handler();
  }

  @Override
  public <T> S option(ChannelOption<T> option, T value) {
    bootstrap().option(option, value);
    return _self_();
  }

  @Override
  public S options(Map<ChannelOption<?>, Object> options) {
    options.forEach((option, value) -> bootstrap().option((ChannelOption) option, value));
    return _self_();
  }

  /**
   * @return 返回 ChannelOption 的Map集合
   */
  @Override
  public Map<ChannelOption<?>, Object> options() {
    return config().options();
  }

  @Override
  public <T> S attr(AttributeKey<T> key, T value) {
    bootstrap().attr(key, value);
    return _self_();
  }

  @Override
  public S attrs(Map<AttributeKey<?>, Object> attrs) {
    attrs.forEach((key, value) -> bootstrap().attr((AttributeKey) key, value));
    return _self_();
  }

  @Override
  public Map<AttributeKey<?>, Object> attrs() {
    return config().attrs();
  }

  @Override
  public boolean useChannel(IConsumer<Channel> c) {
    final Channel channel = getMainChannel();
    if (channel != null) {
      try {
        c.accept(channel);
      } catch (Exception e) {
        log.warn("use server channel throws: " + e.getMessage(), e);
      }
    }
    return channel != null;
  }

  /**
   * 是否已启动过
   */
  @Override
  public boolean isStarted() {
    return state() != Thread.State.NEW;
  }

  /**
   * 是否已停止
   */
  @Override
  public boolean isStopped() {
    return state() == Thread.State.TERMINATED;
  }

  /**
   * 当前状态
   */
  protected Thread.State state() {
    return stateHolder().get();
  }

  /**
   * 比较，如果符合状态就设置新的状态
   *
   * @param state    比较的状态
   * @param newState 新的状态
   * @return 返回是否设置成功
   */
  protected boolean expectAndSet(Thread.State state, Thread.State newState) {
    return stateHolder().compareAndSet(state, newState);
  }

  /**
   * 获取状态
   */
  protected AtomicReference<Thread.State> stateHolder() {
    return stateHolder;
  }

  /**
   * 设置是否使用Linux的Epoll
   *
   * @param use true or false
   * @return 返回当前对象
   */
  @Override
  public S useLinuxNativeEpoll(boolean use) {
    this.useLinuxNativeEpoll = use;
    return _self_();
  }

  /**
   * @return 返回是否使用Linux的Epoll
   */
  @Override
  public boolean useLinuxNativeEpoll() {
    return useLinuxNativeEpoll;
  }

  @Override
  public EventLoopGroup useEpoll(EventLoopGroup group) {
    if (useLinuxNativeEpoll()
        && !(group instanceof EpollEventLoopGroup)
        && isLinux()) {
      return new EpollEventLoopGroup();
    }
    return group;
  }

  private static final InetSocketAddress ZERO_ADDR = new InetSocketAddress(0);

  /**
   * 检查并重置默认端口
   *
   * @param addr 地址
   * @return 返回新的地址
   */
  public static InetSocketAddress checkAndResetPort(SocketAddress addr) {
    InetSocketAddress address = addr != null ? (InetSocketAddress) addr : ZERO_ADDR;
    if (address.getPort() == 0) {
      try (DatagramSocket socket = new DatagramSocket(0);) {
        return new InetSocketAddress(address.getAddress(), socket.getLocalPort());
      } catch (IOException ignore) {
        /*do nothing*/
      }
    }
    return address;
  }

  protected static GenericFutureListener<? extends Future<Void>>[] copyListeners(GenericFutureListener<? extends Future<Void>> listener0,
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
