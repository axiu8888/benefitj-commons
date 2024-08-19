package com.benefitj.core.device;

import java.util.function.BiConsumer;

/**
 * 消息发送
 */
public interface MessageSender {

  /**
   * 发送消息
   *
   * @param device        设备
   * @param msg           消息
   * @param resultHandler 结果处理
   */
  void send(Device<?> device, Object msg, BiConsumer<Object, Throwable> resultHandler);

}
