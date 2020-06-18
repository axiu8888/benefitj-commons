package com.benefitj.spring.aop.web;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * Aop WEB 自动配置注解
 */
@Import({AopWebRequestAspectConfiguration.class})
@Lazy
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAopWebAutoHandler {
  // ~
}
