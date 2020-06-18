package com.benefitj.spring.listener;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;

/**
 * 初始化环境变量监听
 */
public interface IApplicationEnvironmentPreparedEventListener extends EventListener<ApplicationEnvironmentPreparedEvent> {

  /**
   * 初始化环境变量
   *
   * @param event 事件
   */
  @Override
  void onEvent(ApplicationEnvironmentPreparedEvent event);

}
