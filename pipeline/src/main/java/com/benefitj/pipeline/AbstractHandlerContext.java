package com.benefitj.pipeline;

import javax.annotation.Nonnull;

/**
 * 抽象的 HandlerContext
 */
public abstract class AbstractHandlerContext implements HandlerContext, PipelineHandler {

  private final String name;
  /**
   * 前一个
   */
  private AbstractHandlerContext prev;
  /**
   * 当前的PipelineHandler
   */
  private PipelineHandler handler;
  /**
   * 下一个
   */
  private AbstractHandlerContext next;

  public AbstractHandlerContext(String name) {
    this.name = name;
  }

  public AbstractHandlerContext(String name,
                                AbstractHandlerContext prev,
                                PipelineHandler handler,
                                AbstractHandlerContext next) {
    this.name = name;
    this.prev = prev;
    this.next = next;
    this.handler = handler;
  }

  /**
   * 处理上一条数据
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  @Override
  public void processPrev(@Nonnull HandlerContext ctx, Object msg) {
    final PipelineHandler handler = getHandler();
    if (handler != null) {
      handler.processPrev(this, msg);
    }
  }

  /**
   * 处理下一条数据
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  @Override
  public void processNext(@Nonnull HandlerContext ctx, Object msg) {
    final PipelineHandler handler = getHandler();
    if (handler != null) {
      handler.processNext(this, msg);
    }
  }

  public String getName() {
    return name;
  }

  public AbstractHandlerContext getPrev() {
    return prev;
  }

  public void setPrev(AbstractHandlerContext prev) {
    this.prev = prev;
  }

  /**
   * 获取当前的 PipelineHandler
   *
   * @return 返回当前的PipelineHandler
   */
  @Override
  public PipelineHandler getHandler() {
    return handler;
  }

  public void setHandler(PipelineHandler handler) {
    this.handler = handler;
  }

  public AbstractHandlerContext getNext() {
    return next;
  }

  public void setNext(AbstractHandlerContext next) {
    this.next = next;
  }

  @Override
  public String toString() {
    return String.valueOf(name);
  }
}
