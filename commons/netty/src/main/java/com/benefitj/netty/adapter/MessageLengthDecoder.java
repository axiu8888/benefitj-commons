package com.benefitj.netty.adapter;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 消息长度解码
 */
public class MessageLengthDecoder extends ByteToMessageDecoder {

  public static final byte[] HEAD = new byte[0];
  public static final LengthFunction LENGTH_FUNCTION = (ctx, in, readBuff) -> in.readableBytes();
  private static final Function<Integer, byte[]> BUFF_FUNC = byte[]::new;

  private final ThreadLocal<Map<Integer, byte[]>> buffCache = ThreadLocal.withInitial(WeakHashMap::new);
  /**
   * 获取最小读取长度
   */
  private volatile int minReadLength = 1;
  /**
   * 包头
   */
  private byte[] head = HEAD;
  /**
   * 获取长度的实现
   */
  private LengthFunction lengthFunction = LENGTH_FUNCTION;

  private final AttributeKey<Integer> lengthKey = AttributeKey.valueOf("length");

  public MessageLengthDecoder() {
  }

  public MessageLengthDecoder(int minReadLength, byte[] head) {
    this.setMinReadLength(minReadLength);
    this.setHead(head);
  }

  public MessageLengthDecoder(int minReadLength, byte[] head, LengthFunction lengthFunction) {
    this(minReadLength, head);
    this.lengthFunction = lengthFunction;
  }

  public LengthFunction getLengthFunction() {
    return lengthFunction;
  }

  public void setLengthFunction(LengthFunction lengthFunction) {
    this.lengthFunction = lengthFunction;
  }

  @Override
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    final int minReadLength = getMinReadLength();
    if (in.readableBytes() < minReadLength) {
      return;
    }

    final Attribute<Integer> attr = ctx.channel().attr(getLengthKey());
    if (attr.get() != null && in.readableBytes() < attr.get()) {
      // 未达到可读取长度
      return;
    }

    // 标记一下读取数据之前的位置
    in.markReaderIndex();

    byte[] headBuff = getLocalBuff(minReadLength);
    // 只读取包头的数据
    in.readBytes(headBuff);

    // 判断包头是否匹配
    byte[] head = getHead();
    for (int i = 0; i < head.length; i++) {
      // 检查包头
      if (headBuff[i] != head[i]) {
        // 不匹配直接丢弃
        attr.set(null);
        // 丢弃不匹配的包头数据
        in.resetReaderIndex();
        // 读取后丢弃的字节
        in.readBytes(getLocalBuff(i + 1));
        return;
      }
    }

    // 重置为读取之前的位置
    in.resetReaderIndex();

    // 检查可读取的字节是否达到可读取的长度
    final int len = getLength(ctx, in, headBuff);
    if (in.readableBytes() < len) {
      attr.set(len);
      return;
    }
    attr.set(null);
    // 读取字节数组: 包的长度
    out.add(in.readBytes(len));
  }

  /**
   * 长度
   *
   * @param ctx      xxx
   * @param in       数据
   * @param readBuff 读取的缓冲数据
   * @return 返回数据的长度
   */
  public int getLength(ChannelHandlerContext ctx, ByteBuf in, byte[] readBuff) {
    final LengthFunction func = this.lengthFunction;
    if (func == null) {
      throw new IllegalStateException("LengthFunction is null...");
    }
    return func.getLength(ctx, in, readBuff);
  }

  @Nonnull
  public int getMinReadLength() {
    return minReadLength;
  }

  public void setMinReadLength(int minReadLength) {
    this.minReadLength = minReadLength;
  }

  @Nonnull
  public byte[] getHead() {
    return head;
  }

  public void setHead(byte[] head) {
    this.head = head != null ? head : HEAD;
  }

  public AttributeKey<Integer> getLengthKey() {
    return lengthKey;
  }

  protected byte[] getLocalBuff(Integer size) {
    return buffCache.get().computeIfAbsent(size, BUFF_FUNC);
  }

  public interface LengthFunction {
    /**
     * 长度
     *
     * @param ctx      xxx
     * @param in       数据
     * @param readBuff 读取的缓冲数据
     * @return 返回数据的长度
     */
    int getLength(ChannelHandlerContext ctx, ByteBuf in, byte[] readBuff);
  }
}
