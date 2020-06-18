package org.springframework.data.influxdb.annotations;

import java.lang.annotation.*;

/**
 * 忽略InfluxDB字段的注解
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface InfluxIgnore {
  // ~
}
