package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class IdleStateEventHandler extends IdleStateHandler {

  /**
   * 事件监听
   */
  private IdleStateEventListener eventListener;

  public IdleStateEventHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
    super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
  }

  public IdleStateEventHandler(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
    super(readerIdleTime, writerIdleTime, allIdleTime, unit);
  }

  public IdleStateEventHandler(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
    super(observeOutput, readerIdleTime, writerIdleTime, allIdleTime, unit);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {
      processChannelEvent(ctx, (IdleStateEvent) evt);
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }

  /**
   * 处理事件
   */
  public void processChannelEvent(ChannelHandlerContext ctx, IdleStateEvent evt) {
    IdleStateEventListener listener = getEventListener();
    if (listener != null) {
      listener.onProcess(ctx, evt);
    }
  }

  public IdleStateEventListener getEventListener() {
    return eventListener;
  }

  public void setEventListener(IdleStateEventListener eventListener) {
    this.eventListener = eventListener;
  }

  public interface IdleStateEventListener {
    /**
     * 处理事件
     *
     * @param ctx 上下文
     * @param evt 事件
     */
    void onProcess(ChannelHandlerContext ctx, IdleStateEvent evt);
  }

  /**
   * 创建读取超时的Handler
   *
   * @param reader 读取超时的毫秒数
   * @return 返回创建的Handler
   */
  public static IdleStateEventHandler newReaderHandler(int reader, IdleStateEventListener listener) {
    IdleStateEventHandler handler = new IdleStateEventHandler(reader, 0, 0, TimeUnit.MILLISECONDS);
    handler.setEventListener(listener);
    return handler;
  }

  /**
   * 创建写入超时的Handler
   *
   * @param writer 写入超时的毫秒数
   * @return 返回创建的Handler
   */
  public static IdleStateEventHandler newWriterHandler(int writer, IdleStateEventListener listener) {
    IdleStateEventHandler handler = new IdleStateEventHandler(0, writer, 0, TimeUnit.MILLISECONDS);
    handler.setEventListener(listener);
    return handler;
  }

  /**
   * 创建超时的Handler
   *
   * @param reader 读取超时的毫秒数
   * @param writer 写入超时的毫秒数
   * @param all    全部超时的毫秒数
   * @return 返回创建的Handler
   */
  public static IdleStateEventHandler newHandler(int reader, int writer, int all, IdleStateEventListener listener) {
    IdleStateEventHandler handler = new IdleStateEventHandler(reader, writer, all, TimeUnit.MILLISECONDS);
    handler.setEventListener(listener);
    return handler;
  }

}
