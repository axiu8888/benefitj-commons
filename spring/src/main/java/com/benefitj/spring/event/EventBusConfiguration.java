package com.benefitj.spring.event;

import com.benefitj.frameworks.event.EventBusPoster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EventBusConfiguration.class)
@Configuration
public class EventBusConfiguration {

  @Bean
  @ConditionalOnMissingBean(EventBusPoster.class)
  public EventBusPoster poster() {
    return EventBusPoster.getInstance();
  }

  @Bean
  @ConditionalOnMissingBean(EventBusBeanRegister.class)
  public EventBusBeanRegister eventBusBeanRegister(ApplicationContext context,
                                                   EventBusPoster poster) {
    return new EventBusBeanRegister(context, poster);
  }

}
