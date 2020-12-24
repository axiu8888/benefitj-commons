package com.benefitj.netty.server;

import com.benefitj.netty.AbstractNetty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.bootstrap.ServerBootstrapConfig;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;
import java.util.Map;

/**
 * Netty Server
 *
 * @param <S>
 */
public abstract class AbstractNettyServer<S extends AbstractNettyServer<S>> extends AbstractNetty<ServerBootstrap, S> implements INettyServer<S> {

  public AbstractNettyServer() {
  }

  @Override
  public ServerBootstrapConfig config() {
    return bootstrap().config();
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
    ChannelFuture cf = bootstrap.bind();
    setServeChannel(cf.channel());
    return cf.syncUninterruptibly()
        .addListener(f -> {
          SocketAddress socketAddress = bootstrap.config().localAddress();
          if (f.isSuccess()) {
            log.debug("Netty server started at localAddress: " + socketAddress);
          } else {
            log.debug("Netty server start failed at localAddress: " + socketAddress);
          }
        });
  }

  @Override
  public S stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    GenericFutureListener<? extends Future<Void>> l = f ->
        log.debug("Netty server stop at localAddress: " + config().localAddress());
    super.stop(copyListeners(l, listeners));
    if (!isStopped()) {
      shutdownGracefully(workerGroup(), true);
    }
    return self();
  }


  @Override
  public S group(EventLoopGroup bossGroup, EventLoopGroup workerGroup) {
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
  public <T> S childOption(ChannelOption<T> childOption, T value) {
    bootstrap().childOption(childOption, value);
    return self();
  }

  @Override
  public S childOptions(Map<ChannelOption<?>, Object> ops) {
    ops.forEach((option, value) -> childOption((ChannelOption) option, value));
    return self();
  }

  @Override
  public Map<ChannelOption<?>, Object> childOptions() {
    return config().childOptions();
  }

  @Override
  public <T> S childAttr(AttributeKey<T> key, T value) {
    bootstrap().childAttr(key, value);
    return self();
  }

  @Override
  public S childAttrs(Map<AttributeKey<?>, Object> childAttrs) {
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
  public S childHandler(ChannelHandler childHandler) {
    bootstrap().childHandler(childHandler);
    return self();
  }

}
