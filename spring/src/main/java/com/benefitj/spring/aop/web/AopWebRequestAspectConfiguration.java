package com.benefitj.spring.aop.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.List;

/**
 * AOP的web配置
 */
@ConditionalOnMissingBean(AopWebRequestAspectConfiguration.class)
@EnableAspectJAutoProxy
@Configuration
public class AopWebRequestAspectConfiguration {

  @ConditionalOnMissingBean(WebAop.class)
  @Bean
  public WebAop webRequestAspect(@Autowired(required = false) List<WebAop.WebAopHandler> handlers) {
    return new WebAop(handlers);
  }

  @ConditionalOnMissingBean(WebAop.WebAopHandler.class)
  @Bean
  public WebAop.WebAopHandler webAopHandler() {
    return new WebAop.WebAopHandler() {
    };
  }
}
