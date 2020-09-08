package com.benefitj.netty.adapter;

import com.benefitj.core.HexUtils;
import com.benefitj.netty.MessageValidator;
import com.benefitj.netty.log.NettyLogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * UDP解析器处理器
 */
@ChannelHandler.Sharable
public class DatagramPacketInboundHandler extends ByteBufCopyChannelInboundHandler<DatagramPacket> {

  public static final MessageValidator<byte[]> DEFAULT_VALIDATOR = (original, msg) -> true;
  /**
   * log
   */
  protected final NettyLogger logger = NettyLogger.INSTANCE;
  /**
   * 数据包最小长度
   */
  private volatile int minLen = 1;
  /**
   * 最大读取长度
   */
  private volatile int maxReadLen = Integer.MAX_VALUE;
  /**
   * 读取未知数据字节长度
   */
  private volatile int readUnknownMaxLen = 50;
  /**
   * 解析器
   */
  private final List<DatagramPacketAdapter> adapters = new CopyOnWriteArrayList<>();
  /**
   * 验证器
   */
  private MessageValidator<byte[]> messageValidator = DEFAULT_VALIDATOR;

  public DatagramPacketInboundHandler() {
  }

  public DatagramPacketInboundHandler(int minLen) {
    this.minLen = minLen;
  }

  public DatagramPacketInboundHandler(List<DatagramPacketAdapter> resolvers) {
    register(resolvers);
  }

  public DatagramPacketInboundHandler(int minLen, List<DatagramPacketAdapter> resolvers) {
    this.minLen = minLen;
    register(resolvers);
  }

  /**
   * 设置数据最小长度
   *
   * @param minLen 最小长度
   */
  public void setMinLen(int minLen) {
    this.minLen = minLen;
  }

  /**
   * @return 获取最小长度
   */
  public int getMinLen() {
    return minLen;
  }

  /**
   * @return 获取最大读取长度
   */
  public int getMaxReadLen() {
    return maxReadLen;
  }

  /**
   * 设置数据最大读取长度
   *
   * @param maxReadLen 最大读取长度
   */
  public void setMaxReadLen(int maxReadLen) {
    this.maxReadLen = maxReadLen;
  }

  /**
   * 设置未知数据字节长度
   *
   * @param readUnknownMaxLen 长度
   */
  public void setReadUnknownMaxLen(int readUnknownMaxLen) {
    this.readUnknownMaxLen = readUnknownMaxLen;
  }

  /**
   * @return 获取读取未知数据字节长度
   */
  public int getReadUnknownMaxLen() {
    return readUnknownMaxLen;
  }

  /**
   * @return 获取解析器
   */
  protected List<DatagramPacketAdapter> getAdapters() {
    return adapters;
  }

  /**
   * @return 获取消息验证器
   */
  public MessageValidator<byte[]> getValidator() {
    return messageValidator;
  }

  /**
   * 设置消息验证器
   *
   * @param validator 消息验证器
   */
  public void setMessageValidator(MessageValidator<byte[]> validator) {
    this.messageValidator = validator;
  }

  /**
   * 注冊解析器
   *
   * @param adapter 处理器
   */
  public void register(DatagramPacketAdapter adapter) {
    checkNotNull(adapter, "adapter");
    final List<DatagramPacketAdapter> handlers = this.adapters;
    if (!handlers.contains(adapter)) {
      handlers.add(adapter);
    }
  }

  /**
   * 注册全部解析器
   *
   * @param adapters 处理器
   */
  public void register(Collection<DatagramPacketAdapter> adapters) {
    for (DatagramPacketAdapter resolver : adapters) {
      register(resolver);
    }
  }

  /**
   * 取消注册解析器
   *
   * @param adapter 处理器
   */
  public void unregister(DatagramPacketAdapter adapter) {
    checkNotNull(adapter, "adapter");
    adapters.remove(adapter);
  }

  /**
   * 取消注册全部解析器
   *
   * @param adapters 处理器
   */
  public void unregister(Collection<DatagramPacketAdapter> adapters) {
    for (DatagramPacketAdapter resolver : adapters) {
      unregister(resolver);
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
    try {
      final ByteBuf content = msg.content();
      final int len = content.readableBytes();
      final byte[] data = copy(content, Math.min(len, getMaxReadLen()), true, true);
      if (len < getMinLen()) {
        onPacketUnknown(ctx, msg, data);
        return;
      }

      // 校验数据
      if (getValidator().verify(content, data)) {
        // 处理
        process0(ctx, msg, data);
      } else {
        // 未知数据
        onPacketUnknown(ctx, msg, data);
      }
    } catch (Exception e) {
      logger.error("throw: " + e.getMessage(), e);
      throw e;
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    super.exceptionCaught(ctx, cause);
  }

  /**
   * 解析数据
   *
   * @param ctx    上下文
   * @param packet UDP数据包
   * @param data   数据
   * @return 返回解析的对象
   */
  public void process0(ChannelHandlerContext ctx, DatagramPacket packet, byte[] data) {
    final List<DatagramPacketAdapter> adapters = this.adapters;
    if (adapters.isEmpty()) {
      onEmptyProcess(ctx, packet, data);
    } else {
      for (DatagramPacketAdapter handler : adapters) {
        if (handler.support(data)) {
          handler.process(ctx, packet, data);
          break;
        }
      }
    }
  }

  /**
   * 空处理
   *
   * @param ctx    上下文
   * @param packet UDP数据包
   * @param data   数据
   */
  public void onEmptyProcess(ChannelHandlerContext ctx, DatagramPacket packet, byte[] data) {
    ctx.fireChannelRead(packet.retain());
  }

  /**
   * 未知的数据包
   *
   * @param ctx 上下文
   * @param msg 消息
   */
  public void onPacketUnknown(ChannelHandlerContext ctx, DatagramPacket msg, byte[] data) {
    if (data != null) {
      int len = data.length;
      // 未知数据
      int size = Math.min(data.length, getReadUnknownMaxLen());
      data = copy(msg.content(), size, true, true);
      logger.info("unknown packet, sender: {}, size: {}, content: {}...",
          msg.sender(), len, HexUtils.bytesToHex(data));
    } else {
      logger.info("unknown packet, sender: {}, size == 0", msg.sender());
    }
  }

  /**
   * 写入数据
   *
   * @param ctx     上下文
   * @param address 地址
   * @param data    数据
   * @return 返回ChannelFuture
   */
  public ChannelFuture send(ChannelHandlerContext ctx, InetSocketAddress address, byte[] data) {
    return ctx.writeAndFlush(new DatagramPacket(Unpooled.wrappedBuffer(data), address));
  }


}
