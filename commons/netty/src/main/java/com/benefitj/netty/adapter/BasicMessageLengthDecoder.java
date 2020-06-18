package com.benefitj.netty.adapter;

/**
 * 基本的长度校验，已过时，建议使用 {@link MessageLengthDecoder}
 */
@Deprecated
public class BasicMessageLengthDecoder extends MessageLengthDecoder {

  public BasicMessageLengthDecoder() {
  }

  public BasicMessageLengthDecoder(int minReadLength, byte[] head) {
    super(minReadLength, head);
  }

  public BasicMessageLengthDecoder(int minReadLength, byte[] head, LengthFunction lengthFunction) {
    super(minReadLength, head, lengthFunction);
  }
}
