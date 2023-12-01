package com.benefitj.frameworks.cache;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface TryHandler {

  /**
   * 异常类型
   */
  Class<? extends Throwable> exception() default Throwable.class;

  /**
   * 处理的类
   */
  Class<? extends Handler> handler() default NothingHandler.class;

}
