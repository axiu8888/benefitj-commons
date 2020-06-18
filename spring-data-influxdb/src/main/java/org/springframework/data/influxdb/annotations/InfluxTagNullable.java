package org.springframework.data.influxdb.annotations;

import java.lang.annotation.*;

/**
 * 是否允许 tag 为 null 值
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface InfluxTagNullable {

  /**
   * 是否允许 tag 为 null 值
   */
  boolean value() default false;

}
