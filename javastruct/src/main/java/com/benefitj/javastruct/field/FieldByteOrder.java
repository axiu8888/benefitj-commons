package com.benefitj.javastruct.field;

import java.nio.ByteOrder;

/**
 * 字节顺序
 */
public enum FieldByteOrder {

  /**
   * 大端
   */
  BIG_ENDIAN(ByteOrder.BIG_ENDIAN),

  /**
   * 小端
   */
  LITTLE_ENDIAN(ByteOrder.LITTLE_ENDIAN);

  private final ByteOrder order;

  FieldByteOrder(ByteOrder order) {
    this.order = order;
  }

  public ByteOrder getOrder() {
    return order;
  }

}
