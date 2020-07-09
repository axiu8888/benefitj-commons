package com.benefitj.influxdb.spring;

import com.benefitj.influxdb.template.InfluxDBTemplate;
import com.benefitj.influxdb.write.InfluxWriteManager;
import com.benefitj.influxdb.write.InfluxDBWriteProperty;
import com.benefitj.influxdb.write.AutoInfluxWriteListener;
import com.benefitj.influxdb.write.SimpleInfluxWriteManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Lazy;

@Lazy
@ConditionalOnMissingBean(InfluxWriteManagerConfiguration.class)
@Import(AsyncInfluxDBManagement.class)
@Configuration
public class InfluxWriteManagerConfiguration {

  /**
   * 配置属性
   */
  @ConditionalOnMissingBean(InfluxDBWriteProperty.class)
  @Bean
  public InfluxDBWriteProperty influxWriteProperty() {
    return new InfluxDBWriteProperty();
  }

  /**
   * 保存InfluxDB数据的管理实例
   *
   * @param template template
   * @param property 配置属性
   * @return 写入管理类实例
   */
  @ConditionalOnMissingBean(InfluxWriteManager.class)
  @Bean
  public InfluxWriteManager influxWriteManager(InfluxDBTemplate template,
                                               InfluxDBWriteProperty property) {
    return new SimpleInfluxWriteManager(template, property);
  }

  /**
   * 程序启动时自动写入的监听
   *
   * @param template rxjava template
   * @param property 配置属性
   * @return 监听实例
   */
  @ConditionalOnMissingBean(AutoInfluxWriteListener.class)
  @Bean
  public AutoInfluxWriteListener influxAutoWriteListener(InfluxDBTemplate template,
                                                         InfluxDBWriteProperty property) {
    return new AutoInfluxWriteListener(template, property);
  }

}
