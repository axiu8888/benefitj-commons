package com.benefitj.influxdb.write;

import com.benefitj.influxdb.file.LineFileFactory;
import com.benefitj.influxdb.file.LineFileListener;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
   * 返回当前线程的写入次数
   */
  int currentWriteCount();

  /**
   * 调度器
   */
  ExecutorService getExecutor();

  /**
   * 配置属性
   */
  InfluxDBWriteProperty getProperty();

  /**
   * 创建文件的工厂
   *
   * @param factory 工厂类
   */
  void setLineFileFactory(LineFileFactory factory);

  /**
   * 获取创建文件的工厂
   */
  LineFileFactory getLineFileFactory();

  /**
   * 处理文件的监听
   *
   * @param listener 监听
   */
  void setLineFileListener(LineFileListener listener);

  /**
   * 获取处理文件的监听
   */
  LineFileListener getLineFileListener();

  /**
   * 处理文件的分派器
   *
   * @param dispatcher 分派器
   */
  void setWriterDispatcher(WriterDispatcher dispatcher);

  /**
   * 获取分派器
   */
  WriterDispatcher getWriterDispatcher();

}
