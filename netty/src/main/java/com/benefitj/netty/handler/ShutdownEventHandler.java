package com.benefitj.netty.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelOutputShutdownEvent;

/**
 * 处理输入或输出关闭的事件
 */
@ChannelHandler.Sharable
public class ShutdownEventHandler extends SimpleCopyHandler<Object> {

  public static final ShutdownEventHandler INSTANCE = new ShutdownEventHandler();

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    super.userEventTriggered(ctx, evt);
    // 接收到断开事件
    if (isClose(ctx, evt)) {
      ctx.channel().close();
    }
  }

  /**
   * 是否关闭
   *
   * @param ctx 上下文
   * @param evt 事件
   * @return 返回是否关闭
   */
  public static boolean isClose(ChannelHandlerContext ctx, Object evt) {
    return ctx.channel().isActive()
        && ((evt instanceof ChannelInputShutdownEvent)
        || (evt instanceof ChannelOutputShutdownEvent));
  }

}
