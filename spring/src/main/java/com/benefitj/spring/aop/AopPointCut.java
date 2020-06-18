package com.benefitj.spring.aop;

import java.lang.annotation.*;

/**
 * AOP切入点
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface AopPointCut {
}
