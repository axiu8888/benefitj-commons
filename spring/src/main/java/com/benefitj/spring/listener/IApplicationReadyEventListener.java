package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * 应用已启动完成
 */
public interface IApplicationReadyEventListener extends EventListener<ApplicationReadyEvent> {
  /**
   * 应用已启动完成
   *
   * @param event 事件
   */
  @Override
  void onEvent(ApplicationReadyEvent event);
}
