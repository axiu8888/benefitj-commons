package com.benefitj.spring.aop.web;

import java.lang.annotation.*;

/**
 * AOP切入点
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AopWebPointCut {
}
