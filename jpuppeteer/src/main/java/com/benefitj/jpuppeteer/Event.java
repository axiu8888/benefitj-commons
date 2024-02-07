package com.benefitj.jpuppeteer;

import java.lang.annotation.*;

/**
 * 事件
 *
 * @author Administrator
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({
    ElementType.TYPE,
    ElementType.METHOD
})
@Inherited
public @interface Event {
  /**
   * 事件名
   */
  String value();
}
