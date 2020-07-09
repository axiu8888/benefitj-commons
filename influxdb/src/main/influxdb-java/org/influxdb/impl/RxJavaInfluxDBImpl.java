package org.influxdb.impl;


import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.influxdb.InfluxDBException;
import org.influxdb.RxJavaInfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of a InluxDB API.
 *
 * @author stefan.majer [at] gmail.com
 */
public class RxJavaInfluxDBImpl extends BasicInfluxDBImpl implements RxJavaInfluxDB {

  public RxJavaInfluxDBImpl(String url,
                            String username,
                            String password,
                            OkHttpClient.Builder client) {
    this(url, username, password, client, ResponseFormat.JSON);
  }

  public RxJavaInfluxDBImpl(String url,
                            String username,
                            String password,
                            OkHttpClient.Builder okHttpBuilder,
                            ResponseFormat responseFormat) {
    this(url, username, password, okHttpBuilder, new retrofit2.Retrofit.Builder(), responseFormat);
  }

  public RxJavaInfluxDBImpl(String url,
                            String username,
                            String password,
                            OkHttpClient.Builder okHttpBuilder,
                            Retrofit.Builder retrofitBuilder,
                            ResponseFormat responseFormat) {
    super(url, username, password, okHttpBuilder, retrofitBuilder, responseFormat);
  }

  /**
   * Execute a query against a database.
   *
   * @param query the query to execute.
   * @return a List of Series which matched the query.
   */
  @Override
  public Flowable<QueryResult> queryRxJava(Query query, BackpressureStrategy strategy) {
    return queryRxJava(query, 10000, strategy);
  }

  /**
   * Execute a query against a database.
   *
   * @param query    the query to execute.
   * @param timeUnit the time unit of the results.
   * @return a List of Series which matched the query.
   */
  @Override
  public Flowable<QueryResult> queryRxJava(Query query, TimeUnit timeUnit, BackpressureStrategy strategy) {
    return Flowable.create(emitter -> {
      try {
        QueryResult result = query(query, timeUnit);
        emitter.onNext(result);
        emitter.onComplete();
      } catch (Exception e) {
        emitter.onError(e);
      }
    }, strategy);
  }

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param strategy  反压策略
   * @return a List of Series which matched the query.
   */
  @Override
  public Flowable<QueryResult> queryRxJava(Query query, int chunkSize, BackpressureStrategy strategy) {
    return Flowable.create(emitter -> query(query, chunkSize,
            (cancellable, queryResult) -> emitter.onNext(queryResult), emitter::onComplete, emitter::onError), strategy);
  }

  /**
   * Execute a query against a database.
   *
   * @param query     the query to execute.
   * @param chunkSize the number of QueryResults to process in one chunk.
   * @param strategy  反压策略
   * @return a List of Series which matched the query.
   */
  @Override
  public Flowable<String> queryString(Query query, int chunkSize, BackpressureStrategy strategy) {
    return Flowable.create(emitter -> {
      try {
        Call<ResponseBody> call = callResponseBody(query, chunkSize);
        Response<ResponseBody> response = call.execute();
        if (response.isSuccessful()) {
          Cancellable cancellable = new Cancellable() {
            @Override
            public void cancel() {
              call.cancel();
            }

            @Override
            public boolean isCanceled() {
              return call.isCanceled();
            }
          };
          ResponseBody chunkedBody = response.body();
          getStringChunkProcessor().process(chunkedBody, cancellable, (cancellable1, result) -> emitter.onNext(result), emitter::onComplete);
        } else {
          // REVIEW: must be handled consistently with IOException.
          ResponseBody errorBody = response.errorBody();
          if (errorBody != null) {
            emitter.onError(new InfluxDBException(errorBody.string()));
          }
        }
      } catch (IOException e) {
        QueryResult queryResult = new QueryResult();
        queryResult.setError(e.toString());
        emitter.onNext(getStringChunkProcessor().toJson(queryResult));
        //passing null onFailure consumer is here for backward compatibility
        //where the empty queryResult containing error is propagating into onNext consumer
        emitter.onError(e);
      }
    }, strategy);
  }

}
