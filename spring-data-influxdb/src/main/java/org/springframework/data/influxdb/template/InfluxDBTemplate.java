/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.influxdb.template;

import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.BasicInfluxDB;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.influxdb.converter.ConverterFactory;
import org.springframework.data.influxdb.converter.PointConverter;
import org.springframework.data.influxdb.converter.PointConverterFactory;
import org.springframework.data.influxdb.dto.ContinuousQuery;
import org.springframework.data.influxdb.dto.FieldKey;
import org.springframework.data.influxdb.dto.RetentionPolicy;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface InfluxDBTemplate<Influx extends BasicInfluxDB, Q> extends InitializingBean, ConverterFactory<Point> {

  /**
   * 创建新的InfluxDB实现对象
   *
   * @param url      URL 地址
   * @param username 用户名
   * @param password 密码
   * @param client   OkHttp的Builder
   * @return 返回新创建的InfluxDB对象
   */
  Influx newInfluxDB(String url, String username, String password, OkHttpClient.Builder client);

  /**
   * 获取InfluxDB
   */
  Influx getInfluxDB();

  /**
   * InfluxDB的配置
   */
  InfluxDBProperty getInfluxDBProperty();

  /**
   * 转换Bean为Point的转换器工厂
   */
  default <T> PointConverter<T> getPointConverter(Class<T> clazz) {
    return getPointConverterFactory().getConverter(clazz);
  }

  /**
   * 获取Point转换器工厂
   *
   * @return PointConverterFactory
   */
  PointConverterFactory getPointConverterFactory();

  @Override
  default <T> Point convert(T t) {
    return getPointConverterFactory().convert(t);
  }

  @Override
  default <T> List<Point> convert(T[] items) {
    return getPointConverterFactory().convert(items);
  }

  @Override
  default <T> List<Point> convert(Collection<T> items) {
    return getPointConverterFactory().convert(items);
  }

  /**
   * Ensures that the configured database exists.
   */
  default QueryResult createDatabase() {
    return createDatabase(getDatabase());
  }

  /**
   * Ensures that the configured database exists.
   */
  QueryResult createDatabase(String database);

  /**
   * 数据库名
   */
  String getDatabase();

  /**
   * 缓存策略
   */
  String getRetentionPolicy();

  /**
   * Write a single measurement to the database.
   *
   * @param payload the measurement to write to
   */
  <T> void write(final T payload);

  /**
   * Write a single measurement to the database.
   *
   * @param payload the measurement to write to
   */
  <T> void write(final T[] payload);

  /**
   * Write a set of measurements to the database.
   *
   * @param payload the values to write to
   */
  <T> void write(final List<T> payload);

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  default void write(File batchPoints) {
    write(getDatabase(), getRetentionPolicy(), InfluxDB.ConsistencyLevel.ALL, batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  default void write(String database, String retentionPolicy, File batchPoints) {
    write(database, retentionPolicy, InfluxDB.ConsistencyLevel.ALL, batchPoints);
  }

  default void write(RequestBody batchPoints) {
    this.write(getDatabase(), getRetentionPolicy(), InfluxDB.ConsistencyLevel.ALL, batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, File batchPoints);

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, RequestBody batchPoints);

  /**
   * Executes a query against the database.
   *
   * @param query the query to execute
   * @return a List of time series data matching the query
   */
  Q query(final Query query);

  /**
   * Executes a query against the database.
   *
   * @param query    the query to execute
   * @param timeUnit the time unit to be used for the query
   * @return a List of time series data matching the query
   */
  Q query(final Query query, final TimeUnit timeUnit);

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @return a List of time series data matching the query
   */
  Q query(Query query, int chunkSize) throws IllegalStateException;

  /**
   * Executes a query against the database.
   *
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  void query(Query query, int chunkSize, Consumer<QueryResult> consumer) throws IllegalStateException;

  /**
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  void query(Query query, int chunkSize, BasicInfluxDB.InfluxConsumer<String> consumer);

  /**
   * Ping the database.
   *
   * @return the response of the ping execution
   */
  Pong ping();

  /**
   * Return the version of the connected database.
   *
   * @return the version String, otherwise unknown
   */
  String version();

  /**
   * 获取全部的持续查询
   */
  default List<ContinuousQuery> getContinuousQueries() {
    return getContinuousQueries(null);
  }

  /**
   * 获取全部的持续查询
   */
  default List<ContinuousQuery> getContinuousQueries(String database) {
    String query = "SHOW CONTINUOUS QUERIES";
    return getResults(query)
        .stream()
        .filter(r -> r != null && r.getSeries() != null)
        .flatMap(r -> r.getSeries().stream())
        .filter(s -> !isBlank(database) || s.getName().equals(database))
        .filter(s -> s.getValues() != null)
        .flatMap(s -> s.getValues()
            .stream()
            .map(values -> {
              ContinuousQuery continuousQuery = new ContinuousQuery();
              continuousQuery.setDatabase(s.getName());
              continuousQuery.setName(String.valueOf(values.get(0)));
              continuousQuery.setQuery(String.valueOf(values.get(1)));
              return continuousQuery;
            }))
        .collect(Collectors.toList());
  }

  /**
   * obtain all retention policies
   *
   * @return return retention policies
   */
  default List<RetentionPolicy> getRetentionPolicies() {
    return getRetentionPolicies(getDatabase());
  }

  /**
   * obtain all retention policies
   *
   * @param db database name
   * @return return retention policies
   */
  default List<RetentionPolicy> getRetentionPolicies(String db) {
    QueryResult queryResult = postQuery(db, "SHOW RETENTION POLICIES ON " + db);
    return mapperTo(queryResult, RetentionPolicy.class);
  }

  /**
   * 获取 MEASUREMENT 的 TAG keys
   *
   * @param measurement MEASUREMENT
   * @return return TAG list
   */
  default List<String> getTagKeys(String measurement) {
    return getTagKeys(getDatabase(), measurement);
  }

  /**
   * 获取 MEASUREMENT 的 TAG keys
   *
   * @param db          database name
   * @param measurement MEASUREMENT
   * @return return TAG list
   */
  default List<String> getTagKeys(String db, String measurement) {
    QueryResult showTagKeyResult = postQuery(db, "SHOW TAG KEYS FROM " + measurement);
    return getObjectsStream(showTagKeyResult)
        .flatMap(Collection::stream)
        .flatMap(o -> Stream.of(String.valueOf(o)))
        .collect(Collectors.toList());
  }

  /**
   * 获取 TAG values
   *
   * @param measurement measurements
   * @param tagKey      tag key
   * @return return TAG key value list
   */
  default List<String> getTagValues(String measurement, String tagKey) {
    return getTagValues(getDatabase(), measurement, tagKey);
  }

  /**
   * 获取 TAG values
   *
   * @param db          database name
   * @param measurement measurements
   * @param tagKey      tag key
   * @return return TAG key value list
   */
  default List<String> getTagValues(String db, String measurement, String tagKey) {
    final String sql = "SHOW TAG VALUES FROM \"" + measurement + "\" WITH KEY = \"" + tagKey + "\"";
    QueryResult queryResult = postQuery(db, sql);
    return getObjectsStream(queryResult)
        .flatMap(values -> Stream.of((String) values.get(1)))
        .collect(Collectors.toList());
  }

  /**
   * 获取 TAG values MAP
   *
   * @param measurement measurement
   * @return return TAG values Map
   */
  default Map<String, List<String>> getTagValuesMap(String measurement) {
    return getTagValuesMap(getDatabase(), measurement);
  }

  /**
   * 获取 TAG values MAP
   *
   * @param db          database name
   * @param measurement measurement
   * @return return TAG values Map
   */
  default Map<String, List<String>> getTagValuesMap(String db, String measurement) {
    final List<String> tagKeys = getTagKeys(db, measurement);
    final Map<String, List<String>> tagValuesMap = new LinkedHashMap<>();
    for (String tagKey : tagKeys) {
      List<String> values = getTagValues(db, measurement, tagKey);
      tagValuesMap.put(tagKey, values);
    }
    return tagValuesMap;
  }

  /**
   * obtain measurements
   *
   * @return return measurement list
   */
  default List<String> getMeasurements() {
    return getMeasurements(getDatabase());
  }

  /**
   * obtain measurements
   *
   * @param db database name
   * @return return measurement list
   */
  default List<String> getMeasurements(String db) {
    QueryResult showMeasurementResult = postQuery(db, "SHOW MEASUREMENTS ON " + db);
    return getObjectsStream(showMeasurementResult)
        .flatMap(Collection::stream)
        .flatMap(o -> Stream.of((String) o))
        .collect(Collectors.toList());
  }

  /**
   * Obtain field key map
   *
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @return return field key map
   */
  default Map<String, FieldKey> getFieldKeyMap(String retentionPolicy, String measurement) {
    return getFieldKeyMap(getDatabase(), retentionPolicy, measurement);
  }

  /**
   * Obtain field key map
   *
   * @param db              database name
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @return return field key map
   */
  default Map<String, FieldKey> getFieldKeyMap(String db, String retentionPolicy, String measurement) {
    return getFieldKeyMap(db, retentionPolicy, measurement, false);
  }

  /**
   * Obtain field key map
   *
   * @param db              database name
   * @param retentionPolicy retention policy
   * @param measurement     measurement
   * @param containTags     contains tag
   * @return return field key map
   */
  default Map<String, FieldKey> getFieldKeyMap(String db, String retentionPolicy, String measurement, boolean containTags) {
    final String sql = "SHOW FIELD KEYS FROM \"" + retentionPolicy + "\".\"" + measurement + "\"";
    QueryResult queryResult = postQuery(db, sql);
    List<FieldKey> fieldKeys = getObjectsStream(queryResult)
        .flatMap(values -> {
          FieldKey fieldKey = new FieldKey.Builder()
              .setColumn((String) values.get(0))
              .setFieldType(FieldKey.getFieldType((String) values.get(1)))
              .build();
          return Stream.of(fieldKey);
        })
        .collect(Collectors.toList());

    if (containTags) {
      List<String> tagKeys = getTagKeys(db, measurement);
      fieldKeys.addAll(tagKeys.stream()
          .flatMap(tag -> {
            FieldKey fieldKey = new FieldKey.Builder()
                .setColumn(tag)
                .setFieldType(String.class)
                .setTag(true)
                .build();
            return Stream.of(fieldKey);
          })
          .collect(Collectors.toList()));
    }

    if (!fieldKeys.isEmpty()) {
      fieldKeys.add(new FieldKey.Builder()
          .setColumn("time")
          .setFieldType(String.class)
          .setTag(true)
          .setTimestamp(true)
          .build());
    }

    if (fieldKeys.isEmpty()) {
      return Collections.emptyMap();
    }
    final Map<String, FieldKey> fieldKeyMap = new ConcurrentHashMap<>();
    for (FieldKey fieldKey : fieldKeys) {
      fieldKeyMap.put(fieldKey.getColumn(), fieldKey);
    }
    return fieldKeyMap;
  }

  /**
   * post query for result
   *
   * @param query
   * @return
   */
  QueryResult postQuery(String query);

  /**
   * post query for result
   *
   * @param query
   * @return
   */
  QueryResult postQuery(String db, String query);


  default Stream<List<Object>> getObjectsStream(QueryResult queryResult) {
    return getResults(queryResult)
        .stream()
        .filter(r -> r.getSeries() != null)
        .flatMap(r -> r.getSeries().stream())
        .flatMap(s -> s.getValues().stream());
  }

  /**
   * 检查 QueryResult
   */
  default boolean checkResult(QueryResult result) {
    List<QueryResult.Result> results = result.getResults();
    return result.getError() == null && (results != null && !results.isEmpty());
  }

  /**
   * get QueryResult.Result list
   */
  default List<QueryResult.Result> getResults(QueryResult result) {
    return checkResult(result) ? result.getResults() : Collections.emptyList();
  }

  /**
   * get QueryResult.Result list
   */
  default List<QueryResult.Result> getResults(String query) {
    QueryResult queryResult = postQuery(query);
    return getResults(queryResult);
  }

  /**
   * get QueryResult.Result list
   */
  default List<QueryResult.Result> getResults(String db, String query) {
    QueryResult queryResult = postQuery(db, query);
    return getResults(queryResult);
  }

  static boolean isBlank(final CharSequence cs) {
    int strLen;
    if (cs == null || (strLen = cs.length()) == 0) {
      return true;
    }
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(cs.charAt(i))) {
        return false;
      }
    }
    return true;
  }

}
