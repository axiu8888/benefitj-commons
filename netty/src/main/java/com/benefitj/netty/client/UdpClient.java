package com.benefitj.netty.client;

import com.benefitj.core.AttributeMap;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.DatagramPacket;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * UDP客户端
 */
public class UdpClient implements AttributeMap {

  static final GenericFutureListener<Future<? super Void>> _default_future_listener = f -> { /* ~ */ };

  protected final Logger log = LoggerFactory.getLogger(getClass());
  /**
   * 本地IP和端口
   */
  private InetSocketAddress localAddress = new InetSocketAddress(0);
  /**
   * 远程地址
   */
  private InetSocketAddress remoteAddress;
  /**
   * 主线程组
   */
  private final NioEventLoopGroup group;
  /**
   * 配置
   */
  private final Bootstrap bootstrap;
  /**
   * 通道
   */
  private AtomicReference<Channel> channelRef = new AtomicReference<>();
  private AtomicReference<ChannelFuture> failureRef = new AtomicReference<>();

  private final Map<String, Object> attrs = new ConcurrentHashMap<>();

  public UdpClient() {
    this(new NioEventLoopGroup(1));
  }

  public UdpClient(NioEventLoopGroup bossGroup) {
    this.bootstrap = new Bootstrap();
    this.group = bossGroup;
    this.bootstrap
        .group(bossGroup)
        .option(ChannelOption.SO_BROADCAST, true)
        .option(ChannelOption.SO_REUSEADDR, true)
        .option(ChannelOption.SO_RCVBUF, (1024 << 10) * 2)
        .option(ChannelOption.SO_SNDBUF, (1024 << 10) * 2)
        .channel(NioDatagramChannel.class);
  }

  /**
   * 设置接收的缓冲区大小
   */
  public UdpClient soRcvbufSize(int soRcvbufSize) {
    getBootstrap().option(ChannelOption.SO_RCVBUF, (1024 << 10) * soRcvbufSize);
    return this;
  }

  /**
   * 设置发送的缓冲区大小
   */
  public UdpClient soSndbufSize(int soSndbufSize) {
    getBootstrap().option(ChannelOption.SO_SNDBUF, (1024 << 10) * soSndbufSize);
    return this;
  }

  @Override
  public Map<String, Object> attrs() {
    return attrs;
  }

  public NioEventLoopGroup getGroup() {
    return group;
  }

  public InetSocketAddress getLocalAddress() {
    return localAddress;
  }

  public UdpClient setLocalAddress(InetSocketAddress localAddress) {
    this.localAddress = localAddress;
    return this;
  }

  /**
   * 获取监听端口
   */
  public int getPort() {
    return getLocalAddress().getPort();
  }

  /**
   * 设置监听端口
   *
   * @param port 端口
   * @return 返回客户端对象
   */
  public UdpClient setPort(int port) {
    this.setLocalAddress(new InetSocketAddress(port));
    return this;
  }

  /**
   * 获取远程地址
   */
  public InetSocketAddress getRemoteAddress() {
    return remoteAddress;
  }

  /**
   * 设置远程地址
   *
   * @param remoteAddress 远程地址
   * @return 返回客户端对象
   */
  public UdpClient setRemoteAddress(InetSocketAddress remoteAddress) {
    this.remoteAddress = remoteAddress;
    return this;
  }

  /**
   * 获取启动配置
   */
  public Bootstrap getBootstrap() {
    return bootstrap;
  }

  /**
   * 设置启动配置
   *
   * @param consumer 配置
   * @return 返回客户端对象
   */
  public UdpClient setBootstrap(Consumer<Bootstrap> consumer) {
    consumer.accept(getBootstrap());
    return this;
  }

  /**
   * 设置启动配置
   *
   * @param initializer 初始化的Handler
   * @return 返回客户端对象
   */
  public UdpClient setChannelInitializer(ChannelInitializer<Channel> initializer) {
    getBootstrap().handler(initializer);
    return this;
  }

  /**
   * 设置启动配置
   *
   * @param initializer 初始化的Handler
   * @return 返回客户端对象
   */
  public UdpClient setChannelInitializer(Consumer<Channel> initializer) {
    return setChannelInitializer((androidUdpClient, ch) -> initializer.accept(ch));
  }

  /**
   * 设置启动配置
   *
   * @param initializer 初始化的Handler
   * @return 返回客户端对象
   */
  public UdpClient setChannelInitializer(BiConsumer<UdpClient, Channel> initializer) {
    getBootstrap()
        .handler(new ChannelInitializer<Channel>() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            initializer.accept(UdpClient.this, ch);
          }
        });
    return this;
  }

  /**
   * 启动UDP客户端
   *
   * @param listeners 监听
   * @return 返回结果
   */
  public UdpClient start(GenericFutureListener<Future<? super Void>>... listeners) {
    if (getChannel() == null) {
      final InetSocketAddress localAddr = obtainPort(getLocalAddress());
      ChannelFuture future = getBootstrap()
          .bind(localAddr)
          .addListener(f -> log.debug("start udp client, port: " + localAddress.getPort() + ", successful: " + f.isSuccess()))
          .addListeners(listeners)
          .syncUninterruptibly();
      Channel ch = future.channel();
      this.channelRef.set(ch);
      this.failureRef.set(ch.newFailedFuture(new Throwable("The client is closed!")));
    }
    return this;
  }

  /**
   * 停止UDP客户端
   *
   * @param listeners 监听
   */
  public UdpClient stop(GenericFutureListener<Future<? super Void>>... listeners) {
    Channel channel = getChannel();
    if (channel != null) {
      channelRef.compareAndSet(channel, null);
      channel.close()
          .addListener(f -> log.debug("stop udp client, port: " + localAddress.getPort() + ", successful: " + f.isSuccess()))
          .addListeners(listeners)
          .syncUninterruptibly()
          // 停止group
          .addListener(f -> getGroup().shutdownGracefully(1, 5, TimeUnit.MILLISECONDS));
    }
    return this;
  }

  /**
   * 获取通道
   */
  public Channel getChannel() {
    return channelRef.get();
  }

  /**
   * 是否可用
   */
  public boolean isActive() {
    Channel ch = getChannel();
    return ch != null && ch.isActive();
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回客户端对象
   */
  public UdpClient send(byte[] msg) {
    return send(msg, _default_future_listener);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回客户端对象
   */
  public UdpClient send(byte[] msg, GenericFutureListener<Future<? super Void>>... listeners) {
    if (getRemoteAddress() == null) {
      throw new IllegalStateException("远程地址不能为空");
    }
    return send(msg, getRemoteAddress(), listeners);
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   * @return 返回客户端对象
   */
  public UdpClient send(byte[] msg, InetSocketAddress remote) {
    return send(msg, remote, _default_future_listener);
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   * @return 返回客户端对象
   */
  public UdpClient send(byte[] msg,
                        InetSocketAddress remote,
                        GenericFutureListener<Future<? super Void>>... listeners) {
    return send(new DatagramPacket(Unpooled.wrappedBuffer(msg), remote), listeners);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回客户端对象
   */
  public UdpClient send(ByteBuf msg, GenericFutureListener<Future<? super Void>>... listeners) {
    if (getRemoteAddress() == null) {
      throw new IllegalStateException("远程地址不能为空");
    }
    return send(msg, getRemoteAddress(), listeners);
  }

  /**
   * 发送消息
   *
   * @param msg    消息
   * @param remote 远程地址
   * @return 返回客户端对象
   */
  public UdpClient send(ByteBuf msg,
                        InetSocketAddress remote,
                        GenericFutureListener<Future<? super Void>>... listeners) {
    return send(new DatagramPacket(msg, remote), listeners);
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回客户端对象
   */
  public UdpClient send(DatagramPacket msg,
                        GenericFutureListener<Future<? super Void>>... listeners) {
    Channel ch = getChannel();
    if (ch != null && ch.isActive()) {
      ch.writeAndFlush(msg).addListeners(listeners);
    } else {
      ChannelFuture failure = this.failureRef.get();
      if (failure == null) {
        throw new IllegalStateException("还未启动udp服务!");
      }
      for (GenericFutureListener<Future<? super Void>> listener : listeners) {
        try {
          listener.operationComplete(failure);
        } catch (Exception e) {
        }
      }
    }
    return this;
  }

  /**
   * 获取一个端口
   *
   * @param address 地址
   * @return 返回默认的端口或随机的端口
   */
  public static InetSocketAddress obtainPort(InetSocketAddress address) {
    address = address != null ? address : new InetSocketAddress(0);
    if (address.getPort() == 0) {
      try (DatagramSocket socket = new DatagramSocket(new InetSocketAddress(address.getAddress(), address.getPort()));) {
        return new InetSocketAddress(address.getAddress(), socket.getLocalPort());
      } catch (IOException ignore) { /*do nothing*/ }
    }
    return address;
  }


}
