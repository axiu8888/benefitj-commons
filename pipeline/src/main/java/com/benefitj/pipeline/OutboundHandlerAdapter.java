package com.benefitj.pipeline;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * PipelineHandler instance
 *
 * @param <I>
 */
public abstract class OutboundHandlerAdapter<I> extends UnboundHandlerAdapter<I> implements PipelineHandler {

  private static final Function<HandlerContext, OutboundHandlerContext> CTX_FUNC = OutboundHandlerContext::new;

  private final Map<HandlerContext, OutboundHandlerContext> ohCtxCache = new WeakHashMap<>();

  public OutboundHandlerAdapter() {
  }

  public OutboundHandlerAdapter(Class<? extends I> inboundMessageType) {
    super(inboundMessageType);
  }

  @Override
  protected final void processPrev0(HandlerContext ctx, I msg) {
    if (ctx instanceof OutboundHandlerContext) {
      process0(ctx, msg);
    } else {
      process0(ohCtxCache.computeIfAbsent(ctx, CTX_FUNC), msg);
    }
  }

  @Override
  protected final void processNext0(HandlerContext ctx, I msg) {
    ctx.fireNext(msg);
  }

  /**
   * 处理消息
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  protected abstract void process0(HandlerContext ctx, I msg);

  /**
   * 只允许往前传递消息
   */
  public static class OutboundHandlerContext implements HandlerContext {

    private HandlerContext ctx;

    public OutboundHandlerContext(HandlerContext ctx) {
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
      getCtx().firePrev(msg);
    }

    @Override
    public void fireNext(Object msg) {
      throw new UnsupportedOperationException("消息仅允许往前传递!");
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
