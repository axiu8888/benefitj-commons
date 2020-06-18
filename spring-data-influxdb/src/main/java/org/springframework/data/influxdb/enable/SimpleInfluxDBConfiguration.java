package org.springframework.data.influxdb.enable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.influxdb.converter.PointConverterFactory;
import org.springframework.data.influxdb.template.InfluxDBProperty;
import org.springframework.data.influxdb.template.SimpleInfluxDBTemplate;

/**
 * 已过时，建议使用 {@link RxJavaInfluxDBConfiguration}
 */
@ConditionalOnMissingBean(SimpleInfluxDBConfiguration.class)
@Lazy
@Configuration
@Deprecated
public class SimpleInfluxDBConfiguration {

  @ConditionalOnMissingBean(PointConverterFactory.class)
  @Bean
  public PointConverterFactory pointConverterFactory() {
    return PointConverterFactory.INSTANCE;
  }

  @ConditionalOnMissingBean(SimpleInfluxDBTemplate.class)
  @Bean
  public SimpleInfluxDBTemplate simpleInfluxDBTemplate(@Autowired(required = false) InfluxDBProperty property,
                                                       @Autowired(required = false) PointConverterFactory pointConverterFactory) {

    SimpleInfluxDBTemplate template = new SimpleInfluxDBTemplate();
    if (pointConverterFactory != null) {
      template.setPointConverterFactory(pointConverterFactory);
    }
    if (property != null) {
      template.setProperty(property);
    }
    return template;
  }

}
