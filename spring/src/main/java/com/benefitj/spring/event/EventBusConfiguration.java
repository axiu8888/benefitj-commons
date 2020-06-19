package com.benefitj.spring.event;

import com.benefitj.event.EventBusPoster;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnMissingBean(EventBusConfiguration.class)
@ConditionalOnClass(EventBusPoster.class)
@Configuration
public class EventBusConfiguration {

  @ConditionalOnMissingBean(EventBusPoster.class)
  @Bean
  public EventBusPoster poster() {
    return EventBusPoster.getInstance();
  }

  @ConditionalOnMissingBean(EventBusBeanRegister.class)
  @Bean
  public EventBusBeanRegister eventBusBeanRegister(ApplicationContext context,
                                                   EventBusPoster poster) {
    return new EventBusBeanRegister(context, poster);
  }

}
