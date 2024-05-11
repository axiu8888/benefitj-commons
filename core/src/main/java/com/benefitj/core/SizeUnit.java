package com.benefitj.core;

/**
 * 长度单位
 */
public enum SizeUnit {

  BIT(1),
  KB(1024L << 1),
  MB(1024L << 10),
  GB((1024L << 10) << 10),
  TB(((1024L << 10) << 10) << 10),
  PB((((1024L << 10) << 10) << 10) << 10),

  ;

  private final long length;

  SizeUnit(long length) {
    this.length = length;
  }

  public long length() {
    return length;
  }

  public long ofSize(int size) {
    return length * size;
  }

  @Override
  public String toString() {
    return name();
  }

}
