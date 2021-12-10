package com.benefitj.netty.handler;

import com.benefitj.netty.ByteBufCopy;
import io.netty.channel.ChannelHandler;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 具有本地缓存的 Handler
 *
 * @param <I>
 */
@ChannelHandler.Sharable
public abstract class CopyInboundHandler<I> extends SimpleChannelInboundHandler<I> implements ByteBufCopy {

  private final ByteBufCopy bufCopy = ByteBufCopy.newByteBufCopy();

  public CopyInboundHandler() {
  }

  public CopyInboundHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public CopyInboundHandler(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  public CopyInboundHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
  }

  @Override
  public byte[] getCache(int size, boolean local) {
    return bufCopy.getCache(size, local);
  }

}
