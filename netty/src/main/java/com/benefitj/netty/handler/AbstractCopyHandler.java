package com.benefitj.netty.handler;

import com.benefitj.netty.ByteBufCopy;
import io.netty.channel.*;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.TypeParameterMatcher;



/**
 * 具有本地缓存的 Handler
 *
 * @param <I>
 */
public abstract class AbstractCopyHandler<I> extends ChannelDuplexHandler implements ByteBufCopy {

  private final ByteBufCopy cache = ByteBufCopy.newByteBufCopy();

  private final TypeParameterMatcher matcher;
  private final boolean autoRelease;

  public AbstractCopyHandler() {
    this(true);
  }

  public AbstractCopyHandler(boolean autoRelease) {
    matcher = TypeParameterMatcher.find(this, AbstractCopyHandler.class, "I");
    this.autoRelease = autoRelease;
  }

  public AbstractCopyHandler(Class<? extends I> inboundMessageType) {
    this(inboundMessageType, true);
  }

  public AbstractCopyHandler(Class<? extends I> inboundMessageType, boolean autoRelease) {
    matcher = TypeParameterMatcher.get(inboundMessageType);
    this.autoRelease = autoRelease;
  }

  @Override
  public byte[] getCache(int size, boolean local) {
    return cache.getCache(size, local);
  }

  /**
   * Returns {@code true} if the given message should be handled. If {@code false} it will be passed to the next
   * {@link ChannelInboundHandler} in the {@link ChannelPipeline}.
   */
  public boolean acceptMessage(Object msg) throws Exception {
    return matcher.match(msg);
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    boolean release = true;
    try {
      if (acceptMessage(msg)) {
        @SuppressWarnings("unchecked")
        I imsg = (I) msg;
        channelRead0(ctx, imsg);
      } else {
        release = false;
        ctx.fireChannelRead(msg);
      }
    } finally {
      if (autoRelease && release) {
        ReferenceCountUtil.release(msg);
      }
    }
  }

  @Override
  public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
    if (acceptMessage(msg)) {
      @SuppressWarnings("unchecked")
      I cast = (I) msg;
      channelWrite0(ctx, cast, promise);
    } else {
      ctx.write(msg, promise);
    }
  }

  /**
   * Is called for each message of type {@link I}.
   *
   * @param ctx the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler} belongs to
   * @param msg the message to handle
   * @throws Exception is thrown if an error occurred
   */
  protected abstract void channelRead0(ChannelHandlerContext ctx, I msg) throws Exception;

  /**
   * 写入资源
   *
   * @param ctx     the {@link ChannelHandlerContext} which this {@link SimpleChannelInboundHandler} belongs to
   * @param msg     the message to handle
   * @param promise 结果处理
   */
  protected abstract void channelWrite0(ChannelHandlerContext ctx, I msg, ChannelPromise promise);

}
