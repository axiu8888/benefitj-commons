package com.benefitj.mqtt;

/**
 * 服务质量
 */
public enum QoS {

  /**
   * 至多一次
   */
  AT_MOST_ONCE(0),
  /**
   * 至少一次
   */
  AT_LEAST_ONCE(1),
  /**
   * 每次
   */
  EXACTLY_ONCE(2),
  /**
   * 失败
   */
  FAILURE(0x80);

  private final int value;

  QoS(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }

}
