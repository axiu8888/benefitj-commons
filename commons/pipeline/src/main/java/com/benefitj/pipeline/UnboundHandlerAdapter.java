package com.benefitj.pipeline;

import javax.annotation.Nonnull;

/**
 * PipelineHandler instance
 *
 * @param <I>
 */
public abstract class UnboundHandlerAdapter<I> implements PipelineHandler {

  private final TypeParameterMatcher matcher;

  public UnboundHandlerAdapter() {
    matcher = TypeParameterMatcher.find(this, UnboundHandlerAdapter.class, "I");
  }

  protected UnboundHandlerAdapter(Class<? extends I> inboundMessageType) {
    this.matcher = TypeParameterMatcher.get(inboundMessageType);
  }

  public boolean support(Object msg) {
    return matcher.match(msg);
  }

  @Override
  public final void processPrev(@Nonnull HandlerContext ctx, Object msg) {
    if (support(msg)) {
      @SuppressWarnings("unchecked")
      I imsg = (I) msg;
      processPrev0(ctx, imsg);
    } else {
      ctx.firePrev(msg);
    }
  }

  @Override
  public final void processNext(@Nonnull HandlerContext ctx, Object msg) {
    if (support(msg)) {
      @SuppressWarnings("unchecked")
      I imsg = (I) msg;
      processNext0(ctx, imsg);
    } else {
      ctx.fireNext(msg);
    }
  }

  /**
   * 处理上一条消息
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  protected abstract void processPrev0(HandlerContext ctx, I msg);

  /**
   * 处理下一条消息
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  protected abstract void processNext0(HandlerContext ctx, I msg);
}
