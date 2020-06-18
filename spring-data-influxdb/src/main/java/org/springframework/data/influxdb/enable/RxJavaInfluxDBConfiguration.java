package org.springframework.data.influxdb.enable;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.influxdb.converter.PointConverterFactory;
import org.springframework.data.influxdb.template.InfluxDBProperty;
import org.springframework.data.influxdb.template.RxJavaInfluxDBTemplate;

@Lazy
@ConditionalOnMissingBean(RxJavaInfluxDBConfiguration.class)
@Configuration
public class RxJavaInfluxDBConfiguration {

  @ConditionalOnMissingBean(InfluxDBProperty.class)
  @Bean
  public InfluxDBProperty influxDBProperty() {
    return new InfluxDBProperty();
  }

  @ConditionalOnMissingBean(PointConverterFactory.class)
  @Bean
  public PointConverterFactory pointConverterFactory() {
    return PointConverterFactory.INSTANCE;
  }

  @ConditionalOnMissingBean(RxJavaInfluxDBTemplate.class)
  @Bean
  public RxJavaInfluxDBTemplate rxJavaInfluxDBTemplate(InfluxDBProperty property,
                                                      PointConverterFactory pointConverterFactory) {
    RxJavaInfluxDBTemplate template = new RxJavaInfluxDBTemplate();
    if (pointConverterFactory != null) {
      template.setPointConverterFactory(pointConverterFactory);
    }
    if (property != null) {
      template.setProperty(property);
    }
    return template;
  }

}
