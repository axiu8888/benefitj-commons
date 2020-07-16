package com.benefitj.influxdb.write;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.File;

@ConfigurationProperties(prefix = "com.benefitj.influxdb.write")
public class InfluxDBWriteProperty {

  /**
   * 缓存大小(MB)
   */
  private Integer cacheSize = 50;
  /**
   * 延迟时长(秒)
   */
  private Integer delay = 60;
  /**
   * 缓存目录
   */
  private String cacheDir;
  /**
   * 同时写入的文件数
   */
  private Integer lineFileCount = 1;
  /**
   * 线程数量
   */
  private int threadCount = 4;
  /**
   * 是否自动上传，默认false
   */
  private boolean autoUpload = true;
  /**
   * 文件后缀，默认是 “.line”
   */
  private String suffix = ".line";

  public Integer getCacheSize() {
    return cacheSize;
  }

  public void setCacheSize(Integer cacheSize) {
    this.cacheSize = cacheSize;
  }

  public Integer getDelay() {
    return delay;
  }

  public void setDelay(Integer delay) {
    this.delay = delay;
  }

  public String getCacheDir() {
    return cacheDir;
  }

  public void setCacheDir(String cacheDir) {
    this.cacheDir = cacheDir;
  }

  public Integer getLineFileCount() {
    return lineFileCount;
  }

  public void setLineFileCount(Integer lineFileCount) {
    this.lineFileCount = lineFileCount;
  }

  public int getThreadCount() {
    return threadCount;
  }

  public void setThreadCount(int threadCount) {
    this.threadCount = threadCount;
  }

  public boolean isAutoUpload() {
    return autoUpload;
  }

  public void setAutoUpload(boolean autoUpload) {
    this.autoUpload = autoUpload;
  }

  public String getSuffix() {
    return suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }


  /**
   * 默认配置
   */
  public static InfluxDBWriteProperty defaultProperty() {
    String tmpDir = System.getProperties().getProperty("java.io.tmpdir");
    File lineFile = new File(tmpDir, "/influxdb/lines");
    InfluxDBWriteProperty p = new InfluxDBWriteProperty();
    p.setCacheDir(lineFile.getAbsolutePath());
    p.setCacheSize(50);
    p.setDelay(10);
    p.setLineFileCount(1);
    p.setThreadCount(4);
    return p;
  }

}
