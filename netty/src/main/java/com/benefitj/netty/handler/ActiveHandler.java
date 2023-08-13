package com.benefitj.netty.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 监听 Channel 的状态
 */
@ChannelHandler.Sharable
public class ActiveHandler extends SimpleByteBufHandler<Object> {

  private ActiveStateListener listener;

  public ActiveHandler(ActiveStateListener listener) {
    this.listener = listener;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    getListener().onChanged(this, ctx, State.ACTIVE);
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    getListener().onChanged(this, ctx, State.INACTIVE);
  }

  public ActiveStateListener getListener() {
    return listener;
  }

  public void setListener(ActiveStateListener listener) {
    this.listener = listener;
  }

  public interface ActiveStateListener {
    /**
     * 监听
     *
     * @param ctx     上下文
     * @param handler 当前的Handler
     * @param state   状态
     */
    void onChanged(ActiveHandler handler, ChannelHandlerContext ctx, State state);
  }


  /**
   * 创建 Handler
   *
   * @param listener 监听
   * @return 返回创建的Handler
   */
  public static ActiveHandler newHandler(ActiveStateListener listener) {
    return new ActiveHandler(listener);
  }

  public enum State {
    /**
     * {@link ChannelDuplexHandler#channelActive(ChannelHandlerContext)}
     */
    ACTIVE(true),
    /**
     * {@link ChannelDuplexHandler#channelInactive(ChannelHandlerContext)}
     */
    INACTIVE(false);

    private final boolean active;

    State(boolean active) {
      this.active = active;
    }

    public boolean isActive() {
      return active;
    }

  }

}
