package com.benefitj.netty.handler;

import com.benefitj.netty.ByteBufCopy;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 具有本地缓存的 Handler
 *
 * @param <I>
 */
public abstract class ByteBufCopyInboundHandler<I> extends SimpleChannelInboundHandler<I>
  implements ByteBufCopyHandler<I> {

  private final ByteBufCopy bufCopy = new ByteBufCopy();

  public ByteBufCopyInboundHandler() {
  }

  public ByteBufCopyInboundHandler(boolean autoRelease) {
    super(autoRelease);
  }

  public ByteBufCopyInboundHandler(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  public ByteBufCopyInboundHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
    super(inboundMessageType, autoRelease);
  }

  @Override
  public ByteBufCopy getBufCopy() {
    return bufCopy;
  }

}
