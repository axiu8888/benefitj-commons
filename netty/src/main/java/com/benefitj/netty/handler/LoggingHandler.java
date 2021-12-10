package com.benefitj.netty.handler;

import com.benefitj.core.HexUtils;
import com.benefitj.netty.log.NettyLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * 打印日志
 */
@ChannelHandler.Sharable
public class LoggingHandler extends CopyInboundHandler<ByteBuf> {

  public static final LoggingHandler INSTANCE = new LoggingHandler(50, true);

  /**
   * 读取的最大长度
   */
  private int readMaxSize = 100;
  /**
   * 是否打印日志
   */
  private boolean print = false;

  private final NettyLogger log = NettyLogger.INSTANCE;

  public LoggingHandler() {
  }

  public LoggingHandler(int readMaxSize) {
    this.readMaxSize = readMaxSize;
  }

  public LoggingHandler(int readMaxSize, boolean print) {
    this.readMaxSize = readMaxSize;
    this.print = print;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    try {
      if (isPrint() && msg.readableBytes() > 0) {
        int size = Math.max(0, Math.min(getReadMaxSize(), msg.readableBytes()));
        byte[] data = copyAndReset(msg, size, true);
        printLog(ctx, msg, data);
      }
    } finally {
      ctx.fireChannelRead(msg.retain());
    }
  }

  /**
   * 打印日志
   *
   * @param ctx 上下文
   * @param msg 消息
   * @param buf 读取的缓冲数据
   */
  public void printLog(ChannelHandlerContext ctx, ByteBuf msg, byte[] buf) {
    log.info("remote: {}, size: {}, data: {}"
        , ctx.channel().remoteAddress()
        , msg.readableBytes()
        , HexUtils.bytesToHex(buf));
  }

  public int getReadMaxSize() {
    return readMaxSize;
  }

  public void setReadMaxSize(int readMaxSize) {
    this.readMaxSize = readMaxSize;
  }

  public boolean isPrint() {
    return print;
  }

  public void setPrint(boolean print) {
    this.print = print;
  }
}
