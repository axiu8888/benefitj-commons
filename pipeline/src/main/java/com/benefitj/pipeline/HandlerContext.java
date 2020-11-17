package com.benefitj.pipeline;

/**
 * Pipeline上下文
 */
public interface HandlerContext {

  /**
   * @return 获取Pipeline对象
   */
  Pipeline pipeline();

  /**
   * 从第一个开始往后传递消息，如果其中的Handler已处理过消息，则会被忽略，消息会直接往后传递
   *
   * @param msg 消息
   */
  default void fireHeadNext(Object msg) {
    pipeline().fireNext(msg);
  }

  /**
   * 从最后一个开始往前传递消息，如果其中的Handler已处理过消息，则会被忽略，消息会直接往前传递
   *
   * @param msg 消息
   */
  default void fireTailPrev(Object msg) {
    pipeline().firePrev(msg);
  }

  /**
   * 获取当前的 PipelineHandler
   *
   * @return 返回当前的PipelineHandler
   */
  PipelineHandler getHandler();

  /**
   * 往上一个处理器传递消息
   *
   * @param msg 消息
   */
  void firePrev(Object msg);

  /**
   * 往下一个处理器传递消息
   *
   * @param msg 消息
   */
  void fireNext(Object msg);
}
