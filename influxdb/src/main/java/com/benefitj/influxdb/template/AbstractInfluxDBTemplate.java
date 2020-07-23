package com.benefitj.influxdb.template;

import com.benefitj.influxdb.InfluxPointUtils;
import com.benefitj.influxdb.converter.Converter;
import com.benefitj.influxdb.converter.PointConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import org.influxdb.BasicInfluxDB;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Pong;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.impl.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 抽象的InfluxDBTemplate实现
 */
public abstract class AbstractInfluxDBTemplate<Influx extends BasicInfluxDB, Q> implements InfluxDBTemplate<Influx, Q> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  /**
   * InfluxDB的连接
   */
  private volatile Influx influxDB;
  /**
   * InfluxDB的属性配置
   */
  private InfluxDBProperty property;
  /**
   * 将Bean转换为Point的转换器工厂
   */
  private PointConverterFactory pointConverterFactory = PointConverterFactory.INSTANCE;

  public AbstractInfluxDBTemplate() {
    // ~
  }

  public AbstractInfluxDBTemplate(InfluxDBProperty property) {
    this.property = property;
  }

  public AbstractInfluxDBTemplate(InfluxDBProperty property, PointConverterFactory pointConverterFactory) {
    this.property = property;
    this.pointConverterFactory = pointConverterFactory;
  }

  @Override
  public void afterPropertiesSet() {
    Assert.notNull(getPointConverterFactory(), "PointConverterFactory is required");
  }

  @Override
  public <T> Point convert(T t) {
    return getPointConverterFactory().convert(t);
  }

  @Override
  public <T> List<Point> convert(T[] items) {
    return getPointConverterFactory().convert(items);
  }

  @Override
  public <T> List<Point> convert(Collection<T> items) {
    return getPointConverterFactory().convert(items);
  }

  public void setProperty(InfluxDBProperty property) {
    this.property = property;
  }

  @Override
  public InfluxDBProperty getInfluxDBProperty() {
    return this.property;
  }

  public void setPointConverterFactory(PointConverterFactory pointConverterFactory) {
    this.pointConverterFactory = pointConverterFactory;
  }

  /**
   * 获取Point转换器工厂
   *
   * @return PointConverterFactory
   */
  @Override
  public PointConverterFactory getPointConverterFactory() {
    return this.pointConverterFactory;
  }

  @Override
  public Influx getInfluxDB() {
    Influx db = this.influxDB;
    if (db == null) {
      final InfluxDBProperty prop = getInfluxDBProperty();
      Assert.notNull(prop, "InfluxDBProperties are required");
      synchronized (this) {
        if ((db = this.influxDB) != null) {
          return db;
        }
        final OkHttpClient.Builder client = new OkHttpClient.Builder()
            .connectTimeout(prop.getConnectTimeout(), TimeUnit.SECONDS)
            .writeTimeout(prop.getWriteTimeout(), TimeUnit.SECONDS)
            .readTimeout(prop.getReadTimeout(), TimeUnit.SECONDS);

        String url = prop.getUrl();
        String username = prop.getUsername();
        String password = prop.getPassword();
        db = newInfluxDB(url, username, password, client);
        db.setDatabase(prop.getDatabase());
        db.setRetentionPolicy(prop.getRetentionPolicy());
        db.setConsistency(InfluxDB.ConsistencyLevel.ALL);

        logger.debug("Using InfluxDB '{}' on '{}'", prop.getDatabase(), url);

        if (prop.isGzip()) {
          logger.debug("Enabled gzip compression for HTTP requests");
          db.enableGzip();
        }

        if (prop.isEnableBatch()) {
          db.enableBatch(BatchOptions.DEFAULTS.actions(prop.getBatchActions()));
        }
        this.influxDB = db;
      }
    }
    return db;
  }

  @Override
  public String getDatabase() {
    return getInfluxDBProperty().getDatabase();
  }

  @Override
  public String getRetentionPolicy() {
    return getInfluxDBProperty().getRetentionPolicy();
  }

  /**
   * 创建新的 PointConverter 对象
   *
   * @param type bean类型
   * @return 返回 PointConverter 对象
   */
  @Override
  public <T> Converter<T, Point> getConverter(Class<T> type) {
    return getPointConverterFactory().getConverter(type);
  }

  /**
   * 转换成bean对象
   *
   * @param result 查询的结果集
   * @param type   bean类型
   * @return 返回解析的对象
   */
  @Override
  public <T> List<T> mapperTo(QueryResult result, Class<T> type) {
    return getPointConverterFactory().mapperTo(result, type);
  }

  @Override
  public QueryResult createDatabase(String database) {
    Preconditions.checkNonEmptyString(database, "database");
    String createDatabaseQuery = Query.encode(String.format("CREATE DATABASE \"%s\"", database));
    return getInfluxDB().postQuery(createDatabaseQuery);
  }

  /**
   * 转换成行协议数据
   *
   * @param payload 记录
   * @return 返回行协议数据
   */
  public <T> String lineProtocol(Collection<T> payload) {
    List<String> lines = InfluxPointUtils.toLineProtocol(payload);
    return String.join("\n", lines);
  }

  /**
   * Write a single measurement to the database.
   *
   * @param payload the measurement to write to
   */
  @Override
  public <T> void write(T payload) {
    if (payload instanceof CharSequence) {
      getInfluxDB().write(payload.toString());
    } else if (payload instanceof Point) {
      getInfluxDB().write(((Point) payload));
    } else if (payload instanceof Collection) {
      getInfluxDB().write(lineProtocol((Collection<?>) payload));
    } else if (payload instanceof File) {
      write((File) payload);
    } else {
      getInfluxDB().write(getPointConverterFactory().convert(payload));
    }
  }

  @Override
  public final <T> void write(final T[] payload) {
    write(Arrays.asList(payload));
  }

  @Override
  public <T> void write(final List<T> payload) {
    getInfluxDB().write(lineProtocol(payload));
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(File batchPoints) {
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
  @Override
  public void write(String database, String retentionPolicy, File batchPoints) {
    write(database, retentionPolicy, InfluxDB.ConsistencyLevel.ALL, batchPoints);
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
  @Override
  public void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, File batchPoints) {
    getInfluxDB().write(database, retentionPolicy, consistency, batchPoints);
  }

  /**
   * write
   * <p>
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  public void write(RequestBody batchPoints) {
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
  @Override
  public void write(String database, String retentionPolicy, InfluxDB.ConsistencyLevel consistency, RequestBody batchPoints) {
    getInfluxDB().write(database, retentionPolicy, consistency, batchPoints);
  }

  @Override
  public Pong ping() {
    return getInfluxDB().ping();
  }

  @Override
  public String version() {
    return getInfluxDB().version();
  }

  /**
   * @param query     the query to execute
   * @param chunkSize the number of QueryResults to process in one chunk
   * @param consumer  consumer
   */
  @Override
  public void query(Query query, int chunkSize, BasicInfluxDB.InfluxConsumer<String> consumer) {
    getInfluxDB().queryString(query, chunkSize, consumer);
  }

  @Override
  public QueryResult postQuery(String query) {
    return getInfluxDB().postQuery(query);
  }

  @Override
  public QueryResult postQuery(String db, String query) {
    return getInfluxDB().postQuery(db, query);
  }
}
