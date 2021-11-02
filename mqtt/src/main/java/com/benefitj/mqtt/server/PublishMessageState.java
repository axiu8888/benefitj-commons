package com.benefitj.mqtt.server;


/**
 * 消息发送状态
 */
public enum PublishMessageState {

  /**
   * 确认
   */
  ACKNOWLEDGE,
  /**
   * 已接收
   */
  RECEIVED,
  /**
   * 释放
   */
  RELEASE,
  /**
   * 完成
   */
  COMPLETION;
}
