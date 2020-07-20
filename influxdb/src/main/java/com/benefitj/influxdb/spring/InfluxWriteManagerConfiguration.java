package com.benefitj.influxdb.spring;

import com.benefitj.influxdb.file.LineFileFactory;
import com.benefitj.influxdb.file.LineFileListener;
import com.benefitj.influxdb.template.InfluxDBTemplate;
import com.benefitj.influxdb.write.*;
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
   * 行协议文件工厂
   */
  @ConditionalOnMissingBean(LineFileFactory.class)
  @Bean
  public LineFileFactory lineFileFactory() {
    return new DefaultLineFileFactory();
  }

  /**
   * 行协议文件监听
   */
  @ConditionalOnMissingBean(LineFileListener.class)
  @Bean
  public LineFileListener lineFileListener(InfluxDBTemplate template) {
    return new InfluxDBLineFileListener(template);
  }


  /**
   * 保存InfluxDB数据的管理实例
   *
   * @param lineFileFactory  行协议文件工厂
   * @param lineFileListener 行协议文件监听
   * @param property         配置属性
   * @return 写入管理类实例
   */
  @ConditionalOnMissingBean(InfluxWriteManager.class)
  @Bean
  public InfluxWriteManager influxWriteManager(LineFileFactory lineFileFactory,
                                               LineFileListener lineFileListener,
                                               InfluxDBWriteProperty property) {
    DefaultInfluxWriteManager manager = new DefaultInfluxWriteManager(property);
    manager.setLineFileFactory(lineFileFactory);
    manager.setLineFileListener(lineFileListener);
    return manager;
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
