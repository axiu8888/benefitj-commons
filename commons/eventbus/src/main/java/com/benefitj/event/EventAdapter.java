package com.benefitj.event;

import com.google.common.eventbus.Subscribe;

/**
 * 事件处理器
 */
public interface EventAdapter<E> {

  /**
   * 接收事件
   *
   * @param event 事件
   */
  @Subscribe
  void onEvent(E event);

}
