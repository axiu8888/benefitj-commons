package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationPreparedEvent;

/**
 * 初始化完成监听
 */
public interface IApplicationPreparedEventListener extends EventListener<ApplicationPreparedEvent> {

  /**
   * 初始化完成
   *
   * @param event 事件
   */
  @Override
  void onEvent(ApplicationPreparedEvent event);

}
