package com.benefitj.frameworks.cglib.aop;

import java.lang.annotation.*;
import java.lang.reflect.Modifier;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface AopAround {

  /**
   * 访问修饰符类型，
   * {@link Modifier#PUBLIC}
   * {@link Modifier#PROTECTED}
   * {@link Modifier#PRIVATE}
   * {@link Modifier#FINAL}
   */
  int modifier() default Modifier.PUBLIC | Modifier.FINAL;

}
