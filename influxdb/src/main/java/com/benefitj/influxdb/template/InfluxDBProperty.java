package com.benefitj.influxdb.template;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "com.benefitj.influxdb")
public class InfluxDBProperty {

  /**
   * InfluxDB连接路径
   */
  private String url;
  /**
   * 用户名
   */
  private String username;
  /**
   * 密码
   */
  private String password;
  /**
   * 数据库
   */
  private String database;
  /**
   * 存储策略
   */
  private String retentionPolicy = "autogen";
  /**
   * 连接超时时间
   */
  private int connectTimeout = 10;
  /**
   * 读取超时时间
   */
  private int readTimeout = 30;
  /**
   * 写入超时时间
   */
  private int writeTimeout = 10;
  /**
   * gzip压缩
   */
  private boolean gzip = true;
  /**
   * 是否为批处理
   */
  private boolean enableBatch = true;
  /**
   * 批处理的响应数
   */
  private int batchActions = 100000;
  /**
   * 时间戳的字段名
   */
  private String timeFieldName = "time";

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getDatabase() {
    return database;
  }

  public void setDatabase(String database) {
    this.database = database;
  }

  public String getRetentionPolicy() {
    return retentionPolicy;
  }

  public void setRetentionPolicy(String retentionPolicy) {
    this.retentionPolicy = retentionPolicy;
  }

  public int getConnectTimeout() {
    return connectTimeout;
  }

  public void setConnectTimeout(int connectTimeout) {
    this.connectTimeout = connectTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public int getWriteTimeout() {
    return writeTimeout;
  }

  public void setWriteTimeout(int writeTimeout) {
    this.writeTimeout = writeTimeout;
  }

  public boolean isGzip() {
    return gzip;
  }

  public void setGzip(boolean gzip) {
    this.gzip = gzip;
  }

  public boolean isEnableBatch() {
    return enableBatch;
  }

  public void setEnableBatch(boolean enableBatch) {
    this.enableBatch = enableBatch;
  }

  public int getBatchActions() {
    return batchActions;
  }

  public void setBatchActions(int batchActions) {
    this.batchActions = batchActions;
  }

  public String getTimeFieldName() {
    return timeFieldName;
  }

  public void setTimeFieldName(String timeFieldName) {
    this.timeFieldName = timeFieldName;
  }

}
