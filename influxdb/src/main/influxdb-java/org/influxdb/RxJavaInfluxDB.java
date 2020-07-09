package org.influxdb;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;

import java.util.concurrent.TimeUnit;

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
public interface RxJavaInfluxDB extends BasicInfluxDB {


  /**
   * Execute a query against a database.
   *
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  Flowable<QueryResult> queryRxJava(final Query query, BackpressureStrategy strategy);

  /**
   * Execute a query against a database.
   *
   * @param query    the query to execute.
   * @param timeUnit the time unit of the results.
   * @return a List of Series which matched the query.
   */
  Flowable<QueryResult> queryRxJava(final Query query, TimeUnit timeUnit, BackpressureStrategy strategy);

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param strategy  反压策略
   * @return a List of Series which matched the query.
   */
  Flowable<QueryResult> queryRxJava(Query query, int chunkSize, BackpressureStrategy strategy);

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param strategy  反压策略
   * @return a List of Series which matched the query.
   */
  Flowable<String> queryString(Query query, int chunkSize, BackpressureStrategy strategy);
}
