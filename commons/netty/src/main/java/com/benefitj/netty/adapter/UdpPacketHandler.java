package com.benefitj.netty.adapter;

import com.benefitj.netty.MessageValidator;
import com.benefitj.netty.log.NettyLogger;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

import static io.netty.util.internal.ObjectUtil.checkNotNull;

/**
 * UDP解析器处理器
 */
@ChannelHandler.Sharable
public class UdpPacketHandler extends SimpleChannelInboundHandler<DatagramPacket> {

  public static final MessageValidator<byte[]> DEFAULT_VALIDATOR = (original, msg) -> true;
  public static final Function<Integer, byte[]> BYTES_FUNC = byte[]::new;

  /**
   * log
   */
  protected final NettyLogger logger = NettyLogger.INSTANCE;

  /**
   * 缓存字节数组
   */
  private final ThreadLocal<Map<Integer, byte[]>> byteCache = ThreadLocal.withInitial(WeakHashMap::new);

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
  private final List<PacketResolveAdapter> adapters = new CopyOnWriteArrayList<>();
  /**
   * 验证器
   */
  private MessageValidator<byte[]> messageValidator = DEFAULT_VALIDATOR;

  public UdpPacketHandler() {
  }

  public UdpPacketHandler(int minLen) {
    this.minLen = minLen;
  }

  public UdpPacketHandler(List<PacketResolveAdapter> resolvers) {
    register(resolvers);
  }

  public UdpPacketHandler(int minLen, List<PacketResolveAdapter> resolvers) {
    this.minLen = minLen;
    register(resolvers);
  }

  public byte[] getByteCache(int size) {
    return byteCache.get().computeIfAbsent(size, BYTES_FUNC);
  }

  public byte[] copy(byte[] src) {
    return copy(src, new byte[src.length]);
  }

  public byte[] copy(byte[] src, byte[] dest) {
    System.arraycopy(src, 0, dest, 0, dest.length);
    return dest;
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
  protected List<PacketResolveAdapter> getAdapters() {
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
  public void register(PacketResolveAdapter adapter) {
    checkNotNull(adapter, "adapter");
    final List<PacketResolveAdapter> handlers = this.adapters;
    if (!handlers.contains(adapter)) {
      handlers.add(adapter);
    }
  }

  /**
   * 注册全部解析器
   *
   * @param adapters 处理器
   */
  public void register(Collection<PacketResolveAdapter> adapters) {
    for (PacketResolveAdapter resolver : adapters) {
      register(resolver);
    }
  }

  /**
   * 取消注册解析器
   *
   * @param adapter 处理器
   */
  public void unregister(PacketResolveAdapter adapter) {
    checkNotNull(adapter, "adapter");
    adapters.remove(adapter);
  }

  /**
   * 取消注册全部解析器
   *
   * @param adapters 处理器
   */
  public void unregister(Collection<PacketResolveAdapter> adapters) {
    for (PacketResolveAdapter resolver : adapters) {
      unregister(resolver);
    }
  }

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) throws Exception {
    try {
      final ByteBuf content = msg.content();
      final int len = content.readableBytes();
      final byte[] data = readByteBufLocal(content, Math.min(len, getMaxReadLen()));
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
    final List<PacketResolveAdapter> adapters = this.adapters;
    if (adapters.isEmpty()) {
      onEmptyProcess(ctx, packet, data);
    } else {
      for (PacketResolveAdapter handler : adapters) {
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
      int readUnknownMaxLen = getReadUnknownMaxLen();
      if (data.length > readUnknownMaxLen) {
        data = copy(data, getByteCache(readUnknownMaxLen));
      }
      logger.info("unknown packet, sender: {}, size: {}, content: {}...",
          msg.sender(), len, Arrays.toString(data));
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

  /**
   * 读取缓冲
   *
   * @param buf msg
   * @return 返回 dest
   */
  public byte[] readByteBufLocal(ByteBuf buf, int size) {
    return readByteBuf(buf, getByteCache(size), true);
  }

  /**
   * 读取缓冲
   *
   * @param buf       msg
   * @param dest      byte[]
   * @param resetMark 是否标记读取的位置
   * @return 返回 dest
   */
  public byte[] readByteBuf(ByteBuf buf, byte[] dest, boolean resetMark) {
    if (resetMark) {
      buf.markReaderIndex();
      buf.readBytes(dest);
      buf.resetReaderIndex();
    } else {
      buf.readBytes(dest);
    }
    return dest;
  }

}
