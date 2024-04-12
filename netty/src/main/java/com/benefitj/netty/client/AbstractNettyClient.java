package com.benefitj.netty.client;

import com.benefitj.netty.AbstractNetty;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.BootstrapConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;

/**
 * Netty的客戶端
 */
public abstract class AbstractNettyClient<S extends AbstractNettyClient<S>> extends AbstractNetty<Bootstrap, S> {

  public AbstractNettyClient() {
  }

  @Override
  protected abstract S useDefaultConfig();

  /**
   * 创建 Bootstrap 实例
   */
  @Override
  public Bootstrap createBootstrap() {
    return new Bootstrap();
  }

  @Override
  public BootstrapConfig config() {
    return bootstrap().config();
  }

  /**
   * 连接或绑定本地端口
   *
   * @param bootstrap 启动器
   * @return 返回ChannelFuture
   */
  @Override
  public final ChannelFuture startOnly(Bootstrap bootstrap) {
    ChannelFuture cf = startOnly(bootstrap, f -> {
      SocketAddress localAddress = config().localAddress();
      SocketAddress remoteAddress = config().remoteAddress();
      if (f.isSuccess()) {
        log.debug("Netty client start at localAddress: " + localAddress + ", remoteAddress: " + remoteAddress);
      } else {
        setMainChannel(null);
        log.debug("Netty client start failed at localAddress: " + localAddress + ", remoteAddress: " + remoteAddress);
      }
    });
    setMainChannel(cf.channel());
    return cf;
  }

  /**
   * 连接或绑定本地端口
   *
   * @param bootstrap 启动器
   * @return 返回ChannelFuture
   */
  protected abstract ChannelFuture startOnly(Bootstrap bootstrap, GenericFutureListener<? extends Future<Void>>... listeners);

  @Override
  public S stop(GenericFutureListener<? extends Future<Void>>... listeners) {
    GenericFutureListener<? extends Future<Void>> l = f ->
        log.debug("Netty client stop at localAddress: " + config().localAddress()
            + ", remoteAddress: " + config().remoteAddress());
    return super.stop(copyListeners(l, listeners));
  }

  /**
   * 是否已连接
   *
   * @return 如果已连接返回 true，否则返回 false
   */
  public boolean isConnected() {
    final Channel ch = getMainChannel();
    return ch != null && ch.isActive();
  }

  public S writeAndFlush(byte[] msg, GenericFutureListener<Future<Void>>... listeners) {
    return writeAndFlush(Unpooled.wrappedBuffer(msg), listeners);
  }

  public S writeAndFlush(ByteBuf msg, GenericFutureListener<Future<Void>>... listeners) {
    return writeAndFlush((Object) msg, listeners);
  }

  public S writeAndFlush(Object msg, GenericFutureListener<Future<Void>>... listeners) {
    Channel ch = getMainChannel();
    if (ch != null) {
      ch.writeAndFlush(msg).addListeners(listeners);
    }
    return _self_();
  }

}
