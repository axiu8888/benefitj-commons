package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class IdleStateEventHandler extends SimpleCopyHandler<Object> {

  /**
   * 事件监听
   */
  private IdleStateEventListener eventListener;

  public IdleStateEventHandler() {
  }

  public IdleStateEventHandler(IdleStateEventListener eventListener) {
    this.eventListener = eventListener;
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
   * 创建超时的Handler
   *
   * @return 返回创建的Handler
   */
  public static IdleStateEventHandler newHandler(IdleStateEventListener listener) {
    return new IdleStateEventHandler(listener);
  }

  /**
   * 创建超时的Handler
   *
   * @return 返回创建的Handler
   */
  public static IdleStateEventHandler newCloseHandler() {
    return new IdleStateEventHandler((ctx, evt) -> ctx.close());
  }

  public static IdleStateHandler newReaderIdle(int reader, TimeUnit unit) {
    return newIdle(reader, 0, 0, unit);
  }

  public static IdleStateHandler newWriterIdle(int writer, TimeUnit unit) {
    return newIdle(0, writer, 0, unit);
  }

  public static IdleStateHandler newAllIdle(int all, TimeUnit unit) {
    return newIdle(0, 0, all, unit);
  }

  public static IdleStateHandler newIdle(int reader, int writer, int all, TimeUnit unit) {
    return new IdleEventHandlerAdapter(reader, writer, all, unit);
  }


  @Sharable
  public static class IdleEventHandlerAdapter extends IdleStateHandler {

    public IdleEventHandlerAdapter(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
      super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);
    }

    public IdleEventHandlerAdapter(long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
      super(readerIdleTime, writerIdleTime, allIdleTime, unit);
    }

    public IdleEventHandlerAdapter(boolean observeOutput, long readerIdleTime, long writerIdleTime, long allIdleTime, TimeUnit unit) {
      super(observeOutput, readerIdleTime, writerIdleTime, allIdleTime, unit);
    }
  }
}
