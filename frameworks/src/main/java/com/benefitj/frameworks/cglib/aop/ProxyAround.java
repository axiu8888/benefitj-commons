package com.benefitj.frameworks.cglib.aop;

import java.lang.annotation.*;
import java.lang.reflect.Modifier;

/**
 * AOP切面
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface ProxyAround {

  /**
   * 访问修饰符类型，
   * {@link Modifier#PUBLIC}
   * {@link Modifier#PROTECTED}
   * {@link Modifier#PRIVATE}
   * {@link Modifier#FINAL}
   */
  int value() default Modifier.PUBLIC;

}
