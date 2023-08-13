package com.benefitj.netty.handler;

import com.benefitj.core.HexUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 打印日志
 */
@ChannelHandler.Sharable
public class LoggingHandler extends SimpleByteBufHandler<ByteBuf> {

  public static final LoggingHandler INSTANCE = new LoggingHandler(50, true);

  /**
   * 读取的最大长度
   */
  private int readMaxSize = 100;
  /**
   * 是否打印日志
   */
  private boolean print = false;

  private final Logger log = LoggerFactory.getLogger(getClass());

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
        int size = Math.min(getReadMaxSize(), msg.readableBytes());
        byte[] data = copyAndReset(msg, size, true);
        log.info("remote: {}, data[{}]: {}"
            , ctx.channel().remoteAddress()
            , msg.readableBytes()
            , HexUtils.bytesToHex(data));
      }
    } finally {
      ctx.fireChannelRead(msg.retain());
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    //super.exceptionCaught(ctx, cause);
    log.error("throws: " + cause.getMessage(), cause);
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
