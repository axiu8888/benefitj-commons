package com.benefitj.pipeline;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * PipelineHandler instance
 *
 * @param <I>
 */
public abstract class InboundHandlerAdapter<I> extends UnboundHandlerAdapter<I> implements PipelineHandler {

  private static final Function<HandlerContext, InboundHandlerContext> CTX_FUNC = InboundHandlerContext::new;

  private final Map<HandlerContext, InboundHandlerContext> ihCtxCache = new WeakHashMap<>();

  public InboundHandlerAdapter() {
    super();
  }

  public InboundHandlerAdapter(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  @Override
  protected final void processPrev0(HandlerContext ctx, I msg) {
    ctx.firePrev(msg);
  }

  @Override
  protected final void processNext0(HandlerContext ctx, I msg) {
    if (ctx instanceof InboundHandlerContext) {
      process0(ctx, msg);
    } else {
      process0(ihCtxCache.computeIfAbsent(ctx, CTX_FUNC), msg);
    }
  }

  /**
   * 处理消息
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  protected abstract void process0(HandlerContext ctx, I msg);

  /**
   * 只允许往后传递消息
   */
  public static class InboundHandlerContext implements HandlerContext {

    private HandlerContext ctx;

    public InboundHandlerContext(HandlerContext ctx) {
      this.ctx = ctx;
    }

    public HandlerContext getCtx() {
      return ctx;
    }

    public void setCtx(HandlerContext ctx) {
      this.ctx = ctx;
    }

    @Override
    public Pipeline pipeline() {
      return ctx.pipeline();
    }

    /**
     * 获取当前的 PipelineHandler
     *
     * @return 返回当前的PipelineHandler
     */
    @Override
    public PipelineHandler getHandler() {
      return ctx.getHandler();
    }

    @Override
    public void firePrev(Object msg) {
      throw new UnsupportedOperationException("消息仅允许往后传递!");
    }

    @Override
    public void fireNext(Object msg) {
      getCtx().fireNext(msg);
    }

    @Override
    public boolean equals(Object obj) {
      return (obj == this || getCtx().equals(obj));
    }

    @Override
    public int hashCode() {
      return getCtx().hashCode();
    }

  }
}
