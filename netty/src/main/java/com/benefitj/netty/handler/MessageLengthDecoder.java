package com.benefitj.netty.handler;

import com.benefitj.core.HexUtils;
import com.benefitj.netty.ByteBufCopy;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 消息长度解码
 */
@ChannelHandler.Sharable
public class MessageLengthDecoder extends ByteToMessageDecoder implements ByteBufCopy {

  public static final byte[] HEAD = new byte[0];
  public static final LengthFunction LENGTH_FUNCTION = (ctx, in, segment) -> in.readableBytes();

  private final ByteBufCopy copy = ByteBufCopy.newBufCopy();
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
  /**
   * 消息头校验
   */
  private HeadValidator headValidator;
  /**
   * 长度
   */
  private final AttributeKey<Integer> lengthKey = AttributeKey.valueOf("length");

  public MessageLengthDecoder() {
  }

  public MessageLengthDecoder(int minReadLength) {
    this(minReadLength, HEAD);
  }

  public MessageLengthDecoder(int minReadLength, byte[] head) {
    this.setMinReadLength(minReadLength);
    this.setHead(head);
  }

  public MessageLengthDecoder(int minReadLength, LengthFunction lengthFunction) {
    this(minReadLength, HEAD, lengthFunction);
  }

  public MessageLengthDecoder(int minReadLength, byte[] head, LengthFunction lengthFunction) {
    this(minReadLength, head);
    this.lengthFunction = lengthFunction;
  }

  @Override
  public byte[] getCache(int size, boolean local) {
    return copy.getCache(size, local);
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

    // 只读取包头的数据
    byte[] segment = copy.copyAndReset(in, minReadLength, true);
    // 判断包头是否匹配
    byte[] head = getHead();
    if (!isHead(head, segment, 0)) {
      int discardSize = 0;
      try {
        int size = segment.length - head.length;
        for (int i = 0; i < size; i++) {
          if (isHead(head, segment, i)) {
            break;
          }
          if (isDiscard(head, segment, i)) {
            // 不匹配的字节
            discardSize++;
          } else {
            return;
          }
        }
        if (discardSize > 0) {
          return;
        }
      } finally {
        if (discardSize > 0) {
          // 丢弃不匹配的数据
          attr.set(null);
          byte[] discard;
          if (discardSize != segment.length) {
            // 拷贝无用数据
            discard = this.copy.copy(in, discardSize, false, false);
          } else {
            discard = segment;
            in.skipBytes(discardSize);
          }
          discardBytes(ctx, in, discard);
        }
      }
    }

    // 检查可读取的字节是否达到可读取的长度
    final int len = getLength(ctx, in, segment);
    if (in.readableBytes() < len) {
      attr.set(len);
      return;
    }
    attr.set(null);
    // 读取字节数组: 包的长度
    out.add(in.readBytes(len));
  }

  /**
   * 丢弃的数据
   *
   * @param discard 数据
   */
  protected void discardBytes(ChannelHandlerContext ctx, ByteBuf in, byte[] discard) {
    // nothing done.
  }

  /**
   * 校验消息头
   *
   * @param head    消息头
   * @param segment 读取的数据缓冲
   * @param start   缓冲区开始的位置
   * @return 返回校验结果
   */
  protected boolean isHead(byte[] head, byte[] segment, int start) {
    HeadValidator validator = getHeadValidator();
    if (validator != null) {
      return validator.isHead(head, segment, start);
    }
    return head.length == 0 || HexUtils.isEquals(head, segment, start);
  }

  /**
   * 是否丢弃数据
   *
   * @param head    消息头
   * @param segment 读取的数据缓冲
   * @param start   缓冲开始的位置
   * @return 返回是否丢弃数据
   */
  protected boolean isDiscard(byte[] head, byte[] segment, int start) {
    return head.length == 0 || !HexUtils.isEquals(head, segment, start);
  }

  /**
   * 长度
   *
   * @param ctx xxx
   * @param in  数据
   * @param buf 读取的缓冲数据
   * @return 返回数据的长度
   */
  public int getLength(ChannelHandlerContext ctx, ByteBuf in, byte[] buf) {
    final LengthFunction func = this.lengthFunction;
    if (func == null) {
      throw new IllegalStateException("LengthFunction is null...");
    }
    return func.getLength(ctx, in, buf);
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

  public LengthFunction getLengthFunction() {
    return lengthFunction;
  }

  public void setLengthFunction(LengthFunction lengthFunction) {
    this.lengthFunction = lengthFunction;
  }

  public HeadValidator getHeadValidator() {
    return headValidator;
  }

  public void setHeadValidator(HeadValidator headValidator) {
    this.headValidator = headValidator;
  }

  public AttributeKey<Integer> getLengthKey() {
    return lengthKey;
  }


  public interface HeadValidator {
    /**
     * 校验 head
     *
     * @param head  消息头
     * @param buf   读取的数据缓冲
     * @param start 缓冲区开始的位置
     * @return 返回校验结果
     */
    boolean isHead(byte[] head, byte[] buf, int start);
  }

  public interface LengthFunction {
    /**
     * 长度
     *
     * @param ctx     xxx
     * @param in      数据
     * @param segment 读取的数据片段
     * @return 返回数据的长度
     */
    int getLength(ChannelHandlerContext ctx, ByteBuf in, byte[] segment);
  }

}

