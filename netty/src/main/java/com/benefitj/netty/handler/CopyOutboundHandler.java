package com.benefitj.netty.handler;

import com.benefitj.netty.ByteBufCopy;
import io.netty.channel.*;
import io.netty.util.internal.TypeParameterMatcher;

@ChannelHandler.Sharable
public abstract class CopyOutboundHandler<I> extends ChannelOutboundHandlerAdapter
    implements ByteBufCopy {

  private final ByteBufCopy bufCopy = ByteBufCopy.newByteBufCopy();
  private final TypeParameterMatcher matcher;

  public CopyOutboundHandler() {
    this.matcher = TypeParameterMatcher.find(this, CopyOutboundHandler.class, "I");
  }

  public CopyOutboundHandler(Class<? extends I> outboundMessageType) {
    this.matcher = TypeParameterMatcher.get(outboundMessageType);
  }

  /**
   * Returns {@code true} if the given message should be handled. If {@code false} it will be passed to the next
   * {@link ChannelOutboundHandler} in the {@link ChannelPipeline}.
   */
  public boolean acceptOutboundMessage(Object msg) throws Exception {
    return matcher.match(msg);
  }

  @Override
  public final void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (acceptOutboundMessage(msg)) {
      @SuppressWarnings("unchecked")
      I cast = (I) msg;
      channelWrite0(ctx, cast, promise);
    } else {
      ctx.write(msg, promise);
    }
  }

  protected abstract void channelWrite0(ChannelHandlerContext ctx, I msg, ChannelPromise promise);

  public ByteBufCopy getBufCopy() {
    return bufCopy;
  }

  @Override
  public byte[] getCache(int size, boolean local) {
    return bufCopy.getCache(size, local);
  }
}
