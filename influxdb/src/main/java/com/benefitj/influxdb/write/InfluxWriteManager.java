package com.benefitj.influxdb.write;

import com.benefitj.influxdb.template.InfluxDBTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * InfluxDB写入数据的管理类
 */
public interface InfluxWriteManager {

  /**
   * 同步保存
   *
   * @param line 行协议数据
   */
  default void putSync(String line) {
    putSync(Collections.singletonList(line));
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  default void putSync(String... lines) {
    putSync(Arrays.asList(lines));
  }

  /**
   * 同步保存
   *
   * @param lines 行协议数据
   */
  void putSync(List<String> lines);

  /**
   * 异步保存
   *
   * @param line 行协议数据
   */
  default void putAsync(String line) {
    putAsync(Collections.singletonList(line));
  }

  /**
   * 异步保存
   *
   * @param lines 行协议数据
   */
  default void putAsync(String... lines) {
    putAsync(Arrays.asList(lines));
  }

  /**
   * 异步保存
   *
   * @param lines 行协议数据
   */
  void putAsync(List<String> lines);

  /**
   * 立刻保存
   */
  void flushNow();

  /**
   * 检查并上传数据
   */
  void checkFlush();

  /**
   * 调度器
   */
  Executor getExecutor();

  /**
   * InfluxDB template
   */
  InfluxDBTemplate getTemplate();

  /**
   * 配置属性
   */
  InfluxDBWriteProperty getProperty();

}
