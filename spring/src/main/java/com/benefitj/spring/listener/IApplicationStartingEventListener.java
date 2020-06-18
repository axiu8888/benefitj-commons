package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationStartingEvent;

/**
 * 应用启动中监听
 */
public interface IApplicationStartingEventListener extends EventListener<ApplicationStartingEvent> {

  /**
   * 应用启动中
   *
   * @param event 事件
   */
  @Override
  void onEvent(ApplicationStartingEvent event);
}
