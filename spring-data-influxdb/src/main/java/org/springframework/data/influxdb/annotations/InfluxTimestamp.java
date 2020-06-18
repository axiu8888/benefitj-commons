package org.springframework.data.influxdb.annotations;

import java.lang.annotation.*;

/**
 * measurement中时间戳的字段注解
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD})
public @interface InfluxTimestamp {
}
