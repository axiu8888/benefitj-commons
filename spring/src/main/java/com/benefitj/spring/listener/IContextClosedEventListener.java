package com.benefitj.spring.listener;

import org.springframework.context.event.ContextClosedEvent;

/**
 * 应用关闭监听
 */
public interface IContextClosedEventListener extends EventListener<ContextClosedEvent> {

  /**
   * 应用关闭
   *
   * @param event 事件
   */
  @Override
  void onEvent(ContextClosedEvent event);

}
