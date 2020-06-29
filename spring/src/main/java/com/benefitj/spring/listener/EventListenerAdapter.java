package com.benefitj.spring.listener;

import com.benefitj.spring.ApplicationListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.event.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.context.event.ContextStoppedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SpringBoot事件监听
 */
@ConditionalOnMissingBean(EventListenerAdapter.class)
@Component
public class EventListenerAdapter extends ApplicationListenerAdapter implements ApplicationListener {

  @Autowired
  private ApplicationContext context;

  @Override
  public final void onApplicationEvent(ApplicationEvent event) {
    super.onApplicationEvent(event);
  }

  @Override
  public void onApplicationEnvironmentPreparedEvent(ApplicationEnvironmentPreparedEvent event) {
    apply(event, IApplicationEnvironmentPreparedEventListener.class);
  }

  @Override
  public void onApplicationContextInitializedEvent(ApplicationContextInitializedEvent event) {
    apply(event, IApplicationContextInitializedEventListener.class);
  }

  @Override
  public void onApplicationStartingEvent(ApplicationStartingEvent event) {
    apply(event, IApplicationStartingEventListener.class);
  }

  @Override
  public void onApplicationStartedEvent(ApplicationStartedEvent event) {
    apply(event, IApplicationStartedEventListener.class);
  }

  @Override
  public void onApplicationPreparedEvent(ApplicationPreparedEvent event) {
    apply(event, IApplicationPreparedEventListener.class);
  }

  @Override
  public void onContextRefreshedEvent(ContextRefreshedEvent event) {
    apply(event, IContextRefreshedEventListener.class);
  }

  @Override
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    apply(event, IApplicationReadyEventListener.class);
  }

  @Override
  public void onContextStartedEvent(ContextStartedEvent event) {
    apply(event, IContextStartedEventListener.class);
  }

  @Override
  public void onContextStoppedEvent(ContextStoppedEvent event) {
    apply(event, IContextStoppedEventListener.class);
  }

  @Override
  public void onContextClosedEventEvent(ContextClosedEvent event) {
    apply(event, IContextClosedEventListener.class);
  }

  @Override
  public void onOtherApplicationEvent(ApplicationEvent event) {
    super.onOtherApplicationEvent(event);
  }

  protected <E extends ApplicationEvent, L extends EventListener<E>> void apply(E e, Class<L> type) {
    apply(e, context.getBeansOfType(type));
  }

  protected <E extends ApplicationEvent, L extends EventListener<E>> void apply(E e, Map<String, L> listenerMap) {
    for (L listener : listenerMap.values()) {
      try {
        listener.onEvent(e);
      } catch (Throwable ex) {
        ex.printStackTrace();
      }
    }
  }

}
