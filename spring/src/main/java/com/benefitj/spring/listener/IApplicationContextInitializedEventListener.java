package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationContextInitializedEvent;

/**
 * 上下文初始化的监听
 */
public interface IApplicationContextInitializedEventListener extends EventListener<ApplicationContextInitializedEvent> {

  /**
   * 上下文初始化时
   *
   * @param event 事件
   */
  @Override
  void onEvent(ApplicationContextInitializedEvent event);
}
