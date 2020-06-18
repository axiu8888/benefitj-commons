package com.benefitj.spring.listener;

import org.springframework.context.event.ContextRefreshedEvent;

/**
 * 应用刷新监听
 */
public interface IContextRefreshedEventListener extends EventListener<ContextRefreshedEvent> {
  /**
   * 应用刷新
   *
   * @param event 事件
   */
  @Override
  void onEvent(ContextRefreshedEvent event);
}
