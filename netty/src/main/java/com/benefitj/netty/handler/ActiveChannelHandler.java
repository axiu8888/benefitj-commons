package com.benefitj.netty.handler;

import com.benefitj.netty.ByteBufCopy;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 监听 Channel 的状态
 */
@ChannelHandler.Sharable
public class ActiveChannelHandler extends ChannelDuplexHandler implements ByteBufCopy {

  private final ByteBufCopy bufCopy = ByteBufCopy.newByteBufCopy();

  private ActiveStateListener listener;

  public ActiveChannelHandler(ActiveStateListener listener) {
    this.listener = listener;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    getListener().onChanged(this, ctx, ActiveState.ACTIVE);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    getListener().onChanged(this, ctx, ActiveState.INACTIVE);
  }

  public ActiveStateListener getListener() {
    return listener;
  }

  public void setListener(ActiveStateListener listener) {
    this.listener = listener;
  }

  public ByteBufCopy getBufCopy() {
    return bufCopy;
  }

  @Override
  public byte[] getCache(int size, boolean local) {
    return bufCopy.getCache(size, local);
  }


  public interface ActiveStateListener {
    /**
     * 监听
     *
     * @param ctx     上下文
     * @param handler 当前的Handler
     * @param state   状态
     */
    void onChanged(ActiveChannelHandler handler, ChannelHandlerContext ctx, ActiveState state);
  }


  /**
   * 创建 Handler
   *
   * @param listener 监听
   * @return 返回创建的Handler
   */
  public static ActiveChannelHandler newHandler(ActiveStateListener listener) {
    return new ActiveChannelHandler(listener);
  }
}
