package com.benefitj.pipeline;

import javax.annotation.Nonnull;

/**
 * Pipeline处理器
 */
public interface PipelineHandler {

  /**
   * 处理上一条数据
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  void processPrev(@Nonnull HandlerContext ctx, Object msg);

  /**
   * 处理下一条数据
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  void processNext(@Nonnull HandlerContext ctx, Object msg);
}
