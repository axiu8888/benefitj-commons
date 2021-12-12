package com.benefitj.javastruct;

import java.lang.annotation.*;

/**
 * 类结构
 *
 * @author DINGXIUAN
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface JavaStructClass {

  /**
   * 结构体的长度，-1为根据数据类型自动计算
   */
  int value() default -1;

  /**
   * 实例化器
   */
  Class<Instantiator> instantiator() default Instantiator.class;

}
