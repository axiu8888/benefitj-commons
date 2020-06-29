package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationStartedEvent;

/**
 * 应用启动中监听
 */
public interface IApplicationStartedEventListener extends EventListener<ApplicationStartedEvent> {

  /**
   * 应用启动中
   *
   * @param event 事件
   */
  @Override
  void onEvent(ApplicationStartedEvent event);
}
