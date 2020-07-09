package com.benefitj.influxdb.template;

import okhttp3.OkHttpClient;
import org.influxdb.BasicInfluxDB;
import org.influxdb.BasicInfluxDBFactory;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import com.benefitj.influxdb.converter.PointConverterFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 默认的Template
 * <p>
 * 已过时，建议使用 {@link RxJavaInfluxDBTemplate}
 */
@Deprecated
public class SimpleInfluxDBTemplate extends AbstractInfluxDBTemplate<BasicInfluxDB, QueryResult> {

  public SimpleInfluxDBTemplate() {
  }

  public SimpleInfluxDBTemplate(InfluxDBProperty property) {
    super(property);
  }

  public SimpleInfluxDBTemplate(InfluxDBProperty property, PointConverterFactory converterFactory) {
    super(property, converterFactory);
  }

  /**
   * 创建新的InfluxDB实现对象
   *
   * @param url      URL 地址
   * @param username 用户名
   * @param password 密码
   * @param client   OkHttp的Builder
   * @return 返回新创建的InfluxDB对象
   */
  @Override
  public BasicInfluxDB newInfluxDB(String url, String username, String password, OkHttpClient.Builder client) {
    return BasicInfluxDBFactory.connect(url, username, password, client);
  }

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  @Override
  public QueryResult query(Query query) {
    return getInfluxDB().query(query);
  }

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  @Override
  public QueryResult query(Query query, TimeUnit timeUnit) {
    return getInfluxDB().query(query, timeUnit);
  }

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  @Deprecated
  @Override
  public QueryResult query(Query query, int chunkSize) throws IllegalStateException {
    throw new IllegalStateException("Not implements!");
    // 请使用其他的方法
  }

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   * @return a List of time series data matching the query
   */
  @Override
  public void query(Query query, int chunkSize, Consumer<QueryResult> consumer) {
    getInfluxDB().query(query, chunkSize, consumer);
  }
}
