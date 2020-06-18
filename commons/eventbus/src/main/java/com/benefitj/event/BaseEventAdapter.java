package com.benefitj.event;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 处理事件的Adapter
 */
public abstract class BaseEventAdapter<E> implements EventAdapter<E> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  public BaseEventAdapter() {
  }

  @Subscribe
  @Override
  public final void onEvent(E event) {
    try {
      process(event);
    } catch (Throwable e) {
      logger.error("throw: " + e.getMessage(), e);
    }
  }

  /**
   * 处理
   *
   * @param e 事件
   */
  public abstract void process(E e);

}
