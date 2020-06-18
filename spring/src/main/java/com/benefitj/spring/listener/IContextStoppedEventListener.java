package com.benefitj.spring.listener;

import org.springframework.context.event.ContextStoppedEvent;

/**
 * 应用停止监听
 */
public interface IContextStoppedEventListener extends EventListener<ContextStoppedEvent> {
  /**
   * 应用停止
   *
   * @param event 事件
   */
  @Override
  void onEvent(ContextStoppedEvent event);
}
