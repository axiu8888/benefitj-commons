package org.influxdb;

import okhttp3.RequestBody;
import org.influxdb.dto.*;

import java.io.File;
import java.net.DatagramSocket;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Interface with all available methods to access a InfluxDB database.
 * <p>
 * A full list of currently available interfaces is implemented in:
 * <p>
 * <a
 * href="https://github.com/influxdb/influxdb/blob/master/src/api/http/api.go">https://github.com/
 * influxdb/influxdb/blob/master/src/api/http/api.go</a>
 *
 * @author stefan.majer [at] gmail.com
 */
public interface BasicInfluxDB extends InfluxDB {


  /**
   * UDP sender socket
   */
  DatagramSocket getDatagramSocket();

  /**
   * Set the loglevel which is used for REST related actions.
   *
   * @param logLevel the loglevel to set.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB setLogLevel(LogLevel logLevel);

  /**
   * Enable Gzip compress for http request body.
   *
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB enableGzip();

  /**
   * Disable Gzip compress for http request body.
   *
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB disableGzip();

  /**
   * Returns whether Gzip compress for http request body is enabled.
   *
   * @return true if gzip is enabled.
   */
  @Override
  boolean isGzipEnabled();

  /**
   * Enable batching of single Point writes to speed up writes significantly. This is the same as calling
   * InfluxDB.enableBatch(BatchOptions.DEFAULTS)
   *
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB enableBatch();

  /**
   * Enable batching of single Point writes to speed up writes significantly. If either number of points written or
   * flushDuration time limit is reached, a batch write is issued.
   * Note that batch processing needs to be explicitly stopped before the application is shutdown.
   * To do so call disableBatch().
   *
   * @param batchOptions the options to set for batching the writes.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB enableBatch(BatchOptions batchOptions);

  /**
   * Enable batching of single Point writes as {@link #enableBatch(int, int, TimeUnit, ThreadFactory)}}
   * using {@linkplain Executors#defaultThreadFactory() default thread factory}.
   *
   * @param actions               the number of actions to collect
   * @param flushDuration         the time to wait at most.
   * @param flushDurationTimeUnit the TimeUnit for the given flushDuration.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   * @see #enableBatch(int, int, TimeUnit, ThreadFactory)
   */
  @Override
  InfluxDB enableBatch(int actions, int flushDuration, TimeUnit flushDurationTimeUnit);

  /**
   * Enable batching of single Point writes as
   * {@link #enableBatch(int, int, TimeUnit, ThreadFactory, BiConsumer)}
   * using with a exceptionHandler that does nothing.
   *
   * @param actions               the number of actions to collect
   * @param flushDuration         the time to wait at most.
   * @param flushDurationTimeUnit the TimeUnit for the given flushDuration.
   * @param threadFactory         a ThreadFactory instance to be used.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   * @see #enableBatch(int, int, TimeUnit, ThreadFactory, BiConsumer)
   */
  @Override
  InfluxDB enableBatch(int actions, int flushDuration, TimeUnit flushDurationTimeUnit, ThreadFactory threadFactory);

  /**
   * Enable batching of single Point writes with consistency set for an entire batch
   * flushDurations is reached first, a batch write is issued.
   * Note that batch processing needs to be explicitly stopped before the application is shutdown.
   * To do so call disableBatch(). Default consistency is ONE.
   *
   * @param actions               the number of actions to collect
   * @param flushDuration         the time to wait at most.
   * @param flushDurationTimeUnit the TimeUnit for the given flushDuration.
   * @param threadFactory         a ThreadFactory instance to be used.
   * @param exceptionHandler      a consumer function to handle asynchronous errors
   * @param consistency           a consistency setting for batch writes.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB enableBatch(int actions, int flushDuration, TimeUnit flushDurationTimeUnit, ThreadFactory threadFactory, BiConsumer<Iterable<Point>, Throwable> exceptionHandler, ConsistencyLevel consistency);

  /**
   * Enable batching of single Point writes to speed up writes significant. If either actions or
   * flushDurations is reached first, a batch write is issued.
   * Note that batch processing needs to be explicitly stopped before the application is shutdown.
   * To do so call disableBatch().
   *
   * @param actions               the number of actions to collect
   * @param flushDuration         the time to wait at most.
   * @param flushDurationTimeUnit the TimeUnit for the given flushDuration.
   * @param threadFactory         a ThreadFactory instance to be used.
   * @param exceptionHandler      a consumer function to handle asynchronous errors
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB enableBatch(int actions, int flushDuration, TimeUnit flushDurationTimeUnit, ThreadFactory threadFactory, BiConsumer<Iterable<Point>, Throwable> exceptionHandler);

  /**
   * Disable Batching.
   */
  @Override
  void disableBatch();

  /**
   * Returns whether Batching is enabled.
   *
   * @return true if batch is enabled.
   */
  @Override
  boolean isBatchEnabled();

  /**
   * Ping this influxDB.
   *
   * @return the response of the ping execution.
   */
  @Override
  Pong ping();

  /**
   * Return the version of the connected influxDB Server.
   *
   * @return the version String, otherwise unknown.
   */
  @Override
  String version();

  /**
   * Write a single Point to the default database.
   *
   * @param point The point to write
   */
  @Override
  void write(Point point);

  /**
   * Write a set of Points to the default database with the string records.
   *
   * @param records the points in the correct lineprotocol.
   */
  @Override
  void write(String records);

  /**
   * Write a set of Points to the default database with the list of string records.
   *
   * @param records the List of points in the correct lineprotocol.
   */
  @Override
  void write(List<String> records);

  /**
   * Write a single Point to the database.
   *
   * @param database        the database to write to.
   * @param retentionPolicy the retentionPolicy to use.
   * @param point
   */
  @Override
  void write(String database, String retentionPolicy, Point point);

  /**
   * Write a single Point to the database through UDP.
   *
   * @param udpPort the udpPort to write to.
   * @param point
   */
  @Override
  void write(int udpPort, Point point);

  /**
   * Write a set of Points to the influxdb database with the new (&gt;= 0.9.0rc32) lineprotocol.
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  void write(BatchPoints batchPoints);

  /**
   * Write a set of Points to the influxdb database with the new (&gt;= 0.9.0rc32) lineprotocol.
   * <p>
   * If batching is enabled with appropriate {@code BatchOptions} settings
   * ({@code BatchOptions.bufferLimit} greater than {@code BatchOptions.actions})
   * This method will try to retry in case of some recoverable errors.
   * Otherwise it just works as {@link #write(BatchPoints)}
   *
   * @param batchPoints
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   * @see <a href="https://github.com/influxdata/influxdb-java/wiki/Handling-errors-of-InfluxDB-under-high-load">
   * Retry worth errors</a>
   */
  @Override
  void writeWithRetry(BatchPoints batchPoints);

  /**
   * Write a set of Points to the influxdb database with the string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param records
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, String records);

  /**
   * Write a set of Points to the influxdb database with the string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param precision       the time precision to use
   * @param records
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, TimeUnit precision, String records);

  /**
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param records
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, List<String> records);

  /**
   * Write a set of Points to the influxdb database with the list of string records.
   *
   * @param database        the name of the database to write
   * @param retentionPolicy the retentionPolicy to use
   * @param consistency     the ConsistencyLevel to use
   * @param precision       the time precision to use
   * @param records
   * @see <a href="https://github.com/influxdb/influxdb/pull/2696">2696</a>
   */
  @Override
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, TimeUnit precision, List<String> records);

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
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, File batchPoints);

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
  void write(String database, String retentionPolicy, ConsistencyLevel consistency, RequestBody batchPoints);

  /**
   * Write a set of Points to the influxdb database with the string records through UDP.
   *
   * @param udpPort the udpPort where influxdb is listening
   * @param records
   */
  @Override
  void write(int udpPort, String records);

  /**
   * Write a set of Points to the influxdb database with the list of string records through UDP.
   *
   * @param udpPort the udpPort where influxdb is listening
   * @param records
   */
  @Override
  void write(int udpPort, List<String> records);

  /**
   * Execute a query against a database.
   *
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  @Override
  QueryResult query(Query query);

  /**
   * Execute a query against a database.
   * <p>
   * One of the consumers will be executed.
   *
   * @param query     the query to execute.
   * @param onSuccess the consumer to invoke when result is received
   * @param onFailure
   */
  @Override
  void query(Query query, Consumer<QueryResult> onSuccess, Consumer<Throwable> onFailure);

  /**
   * Execute a streaming query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param onNext
   */
  @Override
  void query(Query query, int chunkSize, Consumer<QueryResult> onNext);

  /**
   * Execute a streaming query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param onNext
   */
  @Override
  void query(Query query, int chunkSize, BiConsumer<Cancellable, QueryResult> onNext);

  /**
   * Execute a streaming query against a database.
   *
   * @param query      the query to execute.
   * @param chunkSize  the number of QueryResults to process in one chunk.
   * @param onNext     the consumer to invoke for each received QueryResult
   * @param onComplete
   */
  @Override
  void query(Query query, int chunkSize, Consumer<QueryResult> onNext, Runnable onComplete);

  /**
   * Execute a streaming query against a database.
   *
   * @param query      the query to execute.
   * @param chunkSize  the number of QueryResults to process in one chunk.
   * @param onNext     the consumer to invoke for each received QueryResult; with capability to discontinue a streaming query
   * @param onComplete
   */
  @Override
  void query(Query query, int chunkSize, BiConsumer<Cancellable, QueryResult> onNext, Runnable onComplete);

  /**
   * Execute a streaming query against a database.
   *
   * @param query      the query to execute.
   * @param chunkSize  the number of QueryResults to process in one chunk.
   * @param onNext     the consumer to invoke for each received QueryResult; with capability to discontinue a streaming query
   * @param onComplete the onComplete to invoke for successfully end of stream
   * @param onFailure
   */
  @Override
  void query(Query query, int chunkSize, BiConsumer<Cancellable, QueryResult> onNext, Runnable onComplete, Consumer<Throwable> onFailure);

  /**
   * Execute a query against a database.
   *
   * @param query    the query to execute.
   * @param timeUnit the time unit of the results.
   * @return a List of Series which matched the query.
   */
  @Override
  QueryResult query(Query query, TimeUnit timeUnit);

  /**
   * Create a new Database.
   *
   * @param name the name of the new database.
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a parameterized <strong>CREATE DATABASE</strong> query.
   */
  @Override
  void createDatabase(String name);

  /**
   * Delete a database.
   *
   * @param name the name of the database to delete.
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a <strong>DROP DATABASE</strong> query.
   */
  @Override
  void deleteDatabase(String name);

  /**
   * Describe all available databases.
   *
   * @return a List of all Database names.
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a <strong>SHOW DATABASES</strong> query.
   */
  @Override
  List<String> describeDatabases();

  /**
   * Check if a database exists.
   *
   * @param name the name of the database to search.
   * @return true if the database exists or false if it doesn't exist
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a <strong>SHOW DATABASES</strong> query and inspect the result.
   */
  @Override
  boolean databaseExists(String name);

  /**
   * Send any buffered points to InfluxDB. This method is synchronous and will block while all pending points are
   * written.
   *
   * @throws IllegalStateException if batching is not enabled.
   */
  @Override
  void flush();

  /**
   * close thread for asynchronous batch write and UDP socket to release resources if need.
   */
  @Override
  void close();

  /**
   * Set the consistency level which is used for writing points.
   *
   * @param consistency the consistency level to set.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB setConsistency(ConsistencyLevel consistency);

  /**
   * Set the database which is used for writing points.
   *
   * @param database the database to set.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB setDatabase(String database);

  /**
   * Set the retention policy which is used for writing points.
   *
   * @param retentionPolicy the retention policy to set.
   * @return the InfluxDB instance to be able to use it in a fluent manner.
   */
  @Override
  InfluxDB setRetentionPolicy(String retentionPolicy);

  /**
   * Creates a retentionPolicy.
   *
   * @param rpName            the name of the retentionPolicy(rp)
   * @param database          the name of the database
   * @param duration          the duration of the rp
   * @param shardDuration     the shardDuration
   * @param replicationFactor the replicationFactor of the rp
   * @param isDefault         if the rp is the default rp for the database or not
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a parameterized <strong>CREATE RETENTION POLICY</strong> query.
   */
  @Override
  void createRetentionPolicy(String rpName, String database, String duration, String shardDuration, int replicationFactor, boolean isDefault);

  /**
   * Creates a retentionPolicy. (optional shardDuration)
   *
   * @param rpName            the name of the retentionPolicy(rp)
   * @param database          the name of the database
   * @param duration          the duration of the rp
   * @param replicationFactor the replicationFactor of the rp
   * @param isDefault         if the rp is the default rp for the database or not
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a parameterized <strong>CREATE RETENTION POLICY</strong> query.
   */
  @Override
  void createRetentionPolicy(String rpName, String database, String duration, int replicationFactor, boolean isDefault);

  /**
   * Creates a retentionPolicy. (optional shardDuration and isDefault)
   *
   * @param rpName            the name of the retentionPolicy(rp)
   * @param database          the name of the database
   * @param duration          the duration of the rp
   * @param shardDuration     the shardDuration
   * @param replicationFactor the replicationFactor of the rp
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a parameterized <strong>CREATE RETENTION POLICY</strong> query.
   */
  @Override
  void createRetentionPolicy(String rpName, String database, String duration, String shardDuration, int replicationFactor);

  /**
   * Drops a retentionPolicy in a database.
   *
   * @param rpName   the name of the retentionPolicy
   * @param database the name of the database
   * @deprecated (since 2.9, removed in 3.0) Use <code>org.influxdb.InfluxDB.query(Query)</code>
   * to execute a <strong>DROP RETENTION POLICY</strong> query.
   */
  @Override
  void dropRetentionPolicy(String rpName, String database);

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param consumer  发射器
   * @return a List of Series which matched the query.
   */
  void queryString(Query query, int chunkSize, InfluxConsumer<String> consumer);

  /**
   * 执行 query / delete etc...
   *
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  QueryResult postQuery(String query);

  /**
   * 执行 query / delete etc...
   *
   * @param db    database name
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  QueryResult postQuery(String db, String query);


  interface InfluxConsumer<T> {
    /**
     * 消费一条数据
     */
    void onNext(T t);

    /**
     * 抛出异常
     */
    void onError(Throwable e);

    /**
     * 完成
     */
    void onComplete();
  }
}
