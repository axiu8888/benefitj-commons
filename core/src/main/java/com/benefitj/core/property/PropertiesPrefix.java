package com.benefitj.core.property;


import java.lang.annotation.*;

/**
 * 前缀
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface PropertiesPrefix {

  /**
   * 前缀值
   */
  String value();

}
