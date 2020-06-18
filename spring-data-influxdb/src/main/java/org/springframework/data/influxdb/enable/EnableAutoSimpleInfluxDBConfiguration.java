package org.springframework.data.influxdb.enable;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 自动注入InfluxDB的bean，已过时，建议使用 {@link EnableAutoRxJavaInfluxDBConfiguration}
 */
@Deprecated
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(SimpleInfluxDBConfiguration.class)
public @interface EnableAutoSimpleInfluxDBConfiguration {
}
