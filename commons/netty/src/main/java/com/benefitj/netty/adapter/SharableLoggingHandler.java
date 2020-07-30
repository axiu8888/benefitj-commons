package com.benefitj.netty.adapter;

import com.benefitj.core.HexTools;
import com.benefitj.netty.log.NettyLogger;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.io.HexDump;

/**
 * 打印日志
 */
@ChannelHandler.Sharable
public class SharableLoggingHandler extends LocalCacheChannelInboundHandler<ByteBuf> {

  public static final SharableLoggingHandler INSTANCE = new SharableLoggingHandler(100, true);

  /**
   * 读取的最大长度
   */
  private int readMaxSize = 100;
  /**
   * 是否打印日志
   */
  private boolean print = false;

  private final NettyLogger log = NettyLogger.INSTANCE;

  public SharableLoggingHandler() {
  }

  public SharableLoggingHandler(int readMaxSize) {
    this.readMaxSize = readMaxSize;
  }

  public SharableLoggingHandler(int readMaxSize, boolean print) {
    this.readMaxSize = readMaxSize;
    this.print = print;
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
    try {
      if (isPrint() && msg.readableBytes() > 0) {
        byte[] data = read(msg, Math.max(0, Math.min(getReadMaxSize(), 1024)), true, true);
        log.info(HexTools.byteToHex(data));
      }
    } finally {
      ctx.fireChannelRead(msg.retain());
    }
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
