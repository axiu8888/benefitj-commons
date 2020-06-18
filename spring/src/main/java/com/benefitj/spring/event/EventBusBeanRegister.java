package com.benefitj.spring.event;

import com.benefitj.frameworks.event.EventAdapter;
import com.benefitj.frameworks.event.EventBusPoster;
import com.benefitj.spring.ApplicationListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Map;

/**
 * 注册EventBus，如果有多个EventBusPoster，请自行处理
 */
public class EventBusBeanRegister extends ApplicationListenerAdapter implements ApplicationListener {

  private final Logger logger = LoggerFactory.getLogger(EventBusBeanRegister.class);

  private EventBusPoster poster;

  private Map<String, EventAdapter> adapters = null;

  private ApplicationContext context;

  @Autowired
  public EventBusBeanRegister(ApplicationContext context, EventBusPoster poster) {
    this.context = context;
    this.poster = poster;
  }

  @Override
  public void onApplicationReadyEvent(ApplicationReadyEvent event) {
    adapters = context.getBeansOfType(EventAdapter.class);
    poster.register(adapters.values());
  }

  @Override
  public void onContextClosedEventEvent(ContextClosedEvent event) {
    if (adapters != null) {
      poster.unregister(adapters.values());
    }
  }
}
