package com.benefitj.netty;

import io.netty.bootstrap.AbstractBootstrap;
import io.netty.bootstrap.AbstractBootstrapConfig;
import io.netty.channel.*;
import io.netty.channel.ChannelFactory;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.PlatformDependent;
import org.apache.commons.lang3.StringUtils;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ThreadFactory;

/**
 * 服务端/客户端 的Netty接口
 */
public interface INetty<B extends AbstractBootstrap<B, ? extends Channel>, S extends INetty<B, S>> {

  /**
   * 设置服务名
   *
   * @param name 名称
   * @return 返回当前对象
   */
  S name(String name);

  /**
   * 服务名
   */
  String name();

  /**
   * 创建的 AbstractBootstrap 子类的实例
   */
  B bootstrap();

  /**
   * config
   */
  AbstractBootstrapConfig<B, ? extends Channel> config();

  /**
   * 启动
   */
  ChannelFuture startForAwait();

  /**
   * 启动
   */
  ChannelFuture startForAwait(GenericFutureListener<? extends Future<Void>>... listener);

  /**
   * 启动
   */
  S start();

  /**
   * 启动
   *
   * @param listener 结果的监听
   * @return 返回当前对象
   */
  S start(GenericFutureListener<? extends Future<Void>>... listener);

  /**
   * 停止
   */
  S stop();

  /**
   * 停止
   *
   * @param listener 结果的监听
   * @return 返回当前对象
   */
  S stop(GenericFutureListener<? extends Future<Void>>... listener);

  /**
   * 设置端口
   *
   * @param port 端口
   * @return 返回当前对象
   */
  S localAddress(int port);

  /**
   * 设置本地地址
   *
   * @param address 地址
   * @return 返回当前对象
   */
  S localAddress(SocketAddress address);

  /**
   * @return 返回绑定本地端口的SocketAddress对象
   */
  SocketAddress localAddress();

  /**
   * 设置端口
   *
   * @param host 主机
   * @param port 端口
   * @return 返回当前对象
   */
  S remoteAddress(String host, int port);

  /**
   * 远程主机地址
   *
   * @param address 地址
   * @return 返回当前对象
   */
  S remoteAddress(SocketAddress address);

  /**
   * 远程主机地址
   */
  SocketAddress remoteAddress();

  /**
   * 设置线程组
   *
   * @param group 线程组
   * @return 返回当前对象
   */
  S group(EventLoopGroup group);

  /**
   * event loop
   */
  EventLoopGroup group();

  /**
   * 通道实例的class
   *
   * @param channelClass 通道的class
   * @return 返回当前对象
   */
  S channel(Class<? extends Channel> channelClass);

  /**
   * 实例化通道的工厂对象
   *
   * @param channelFactory 通道的工厂对象
   * @return 返回当前对象
   */
  S channelFactory(ChannelFactory<? extends Channel> channelFactory);

  /**
   * @return 返回ChannelFactory对象
   */
  ChannelFactory<? extends Channel> channelFactory();

  /**
   * 处理器
   *
   * @param handler 处理器对象
   * @return 返回当前对象
   */
  S handler(ChannelHandler handler);

  /**
   * 通道处理器
   *
   * @return 返回ChannelHandler对象
   */
  ChannelHandler handler();

  /**
   * 参数
   *
   * @param option 参数key
   * @param value  参数值
   * @param <T>    类型
   * @return 返回当前对象
   */
  <T> S option(ChannelOption<T> option, T value);

  /**
   * 参数
   *
   * @param options 配置可选项
   * @return 返回当前对象
   */
  S options(Map<ChannelOption<?>, Object> options);

  /**
   * 获取 ChannelOption 的Map集合
   */
  Map<ChannelOption<?>, Object> options();

  /**
   * 属性
   *
   * @param key   属性key
   * @param value 属性值
   * @param <T>   类型
   * @return 返回当前对象
   */
  <T> S attr(AttributeKey<T> key, T value);

  /**
   * 配置
   *
   * @param attrs 属性Map
   * @return 返回当前对象
   */
  S attrs(Map<AttributeKey<?>, Object> attrs);

  /**
   * 获取 AttributeKey 的Map集合
   */
  Map<AttributeKey<?>, Object> attrs();

  /**
   * 设置是否使用Linux的Epoll
   *
   * @param use true or false
   * @return 返回当前对象
   */
  S useLinuxNativeEpoll(boolean use);

  /**
   * 是否使用Linux的Epoll
   */
  boolean useLinuxNativeEpoll();

  /**
   * @return 获取主通道，主通道是bind或connect之后的channel对象
   */
  Channel getServeChannel();

  /**
   * 设置主通道，主通道是bind或connect之后的channel对象
   *
   * @param serverChannel 主通道
   * @return 返回当前对象
   */
  S setServeChannel(Channel serverChannel);

  /**
   * 使用主通道
   *
   * @param c 消费者回调
   * @return 返回是否使用了通道
   */
  boolean useServeChannel(NettyConsumer<Channel> c);

  /**
   * 是否已启动
   */
  boolean isStarted();

  /**
   * 是否已停止
   */
  boolean isStopped();

  /**
   * 创建Epoll模型的EventLoopGroup对象
   *
   * @param group 检查的EventLoopGroup对象
   * @return 返回EpollEventLoopGroup对象
   */
  EventLoopGroup useEpoll(EventLoopGroup group);

  @SuppressWarnings("unchecked")
  default void shutdownGracefully(EventLoopGroup group, boolean uninterruptibly) {
    shutdownGracefully(group, uninterruptibly, EMPTY_LISTENER);
  }

  @SuppressWarnings("unchecked")
  default void shutdownGracefully(final EventLoopGroup group,
                                  final boolean uninterruptibly,
                                  final GenericFutureListener<? extends Future<?>>... listeners) {
    if (group != null && !group.isShutdown()) {
      if (uninterruptibly) {
        group.shutdownGracefully().syncUninterruptibly().addListeners((GenericFutureListener[]) listeners);
      } else {
        try {
          group.shutdownGracefully().sync().addListeners((GenericFutureListener[]) listeners);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 如果对象为null，执行操作
   *
   * @param obj 检查对象
   * @param r   执行操作
   */
  default void whenNull(Object obj, Runnable r) {
    if (obj == null) {
      r.run();
    }
  }

  /**
   * 如果对象为null，执行操作
   *
   * @param obj 检查对象
   * @param r   执行操作
   */
  default void whenNotNull(Object obj, Runnable r) {
    if (obj != null) {
      r.run();
    }
  }

  /**
   * 要求非空
   *
   * @param obj      检查的对象
   * @param errorMsg 错误消息
   * @throws NullPointerException 空指针
   */
  default void requireNotNull(Object obj, String errorMsg) throws NullPointerException {
    if (obj == null) {
      throw new NullPointerException(errorMsg);
    }
  }

  /**
   * @return 是否为Linux系统
   */
  default boolean isLinux() {
    return getOsName().toLowerCase().contains("linux");
  }

  /**
   * @return 是否为Windows系统
   */
  default boolean isWindows() {
    return PlatformDependent.isWindows();
  }

  /**
   * @return 获取当前系统名
   */
  default String getOsName() {
    String osName = System.getProperties().getProperty("os.name");
    return osName != null ? osName : "";
  }

  default ThreadFactory newBoss(String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    return newThreadFactory(name + "-", "-boss-", false);
  }

  default ThreadFactory newWorker(String name) {
    if (StringUtils.isBlank(name)) {
      return null;
    }
    return newThreadFactory(name + "-", "-worker-", false);
  }

  default ThreadFactory newThreadFactory(String prefix, String suffix, boolean daemon) {
    return new DefaultThreadFactory(prefix, suffix, daemon);
  }

  GenericFutureListener<? extends Future<Void>> EMPTY_LISTENER = future -> { /*do nothing*/ };
}
