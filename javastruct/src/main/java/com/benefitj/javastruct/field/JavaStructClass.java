package com.benefitj.javastruct.field;

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
   * 结构体的长度，0为根据数据类型定义
   */
  int value() default 0;

}
