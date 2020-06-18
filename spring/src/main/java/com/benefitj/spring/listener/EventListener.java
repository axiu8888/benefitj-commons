package com.benefitj.spring.listener;

public interface EventListener<E> {

  /**
   * @param event 事件
   */
  void onEvent(E event);

}
