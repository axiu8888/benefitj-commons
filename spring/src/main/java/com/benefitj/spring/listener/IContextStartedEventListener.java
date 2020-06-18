package com.benefitj.spring.listener;


import org.springframework.context.event.ContextStartedEvent;

/**
 * 应用启动监听
 */
public interface IContextStartedEventListener extends EventListener<ContextStartedEvent>  {
  /**
   * 应用启动
   *
   * @param event 事件
   */
  @Override
  void onEvent(ContextStartedEvent event);

}
