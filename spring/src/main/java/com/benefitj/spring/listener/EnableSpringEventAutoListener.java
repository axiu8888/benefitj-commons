package com.benefitj.spring.listener;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * SpringBoot事件监听
 */
@Import({EventListenerAdapter.class})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableSpringEventAutoListener {
  // ~
}
