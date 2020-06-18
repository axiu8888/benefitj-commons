package com.benefitj.spring.aop.simple;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

/**
 * 普通的AOP配置
 */
@ConditionalOnMissingBean(SimpleAopConfiguration.class)
@EnableAspectJAutoProxy
@Configuration
public class SimpleAopConfiguration {

  @ConditionalOnMissingBean(SimpleAop.class)
  @Bean
  public SimpleAop defaultAspect(@Autowired(required = false) List<SimpleAop.SimpleAopHandler> handlers) {
    return new SimpleAop(handlers);
  }

  @ConditionalOnMissingBean(SimpleAop.SimpleAopHandler.class)
  @Bean
  public SimpleAop.SimpleAopHandler defaultAopHandler() {
    return new SimpleAop.SimpleAopHandler() {};
  }

}
