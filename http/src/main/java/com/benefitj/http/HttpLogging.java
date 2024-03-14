package com.benefitj.http;


import lombok.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * HTTP日志打印
 */
public class HttpLogging implements Interceptor {

  private static final Logger log = LoggerFactory.getLogger(HttpLogging.class);

  private final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(log::info);

  public HttpLogging() {
    this(HttpLoggingInterceptor.Level.NONE);
  }

  public HttpLogging(HttpLoggingInterceptor.Level level) {
    this.setLevel(level);
  }

  @NonNull
  @Override
  public Response intercept(@NonNull Chain chain) throws IOException {
    return interceptor.intercept(chain);
  }

  public HttpLogging setLevel(HttpLoggingInterceptor.Level level) {
    interceptor.setLevel(level);
    return this;
  }

  public HttpLoggingInterceptor.Level getLevel() {
    return interceptor.getLevel();
  }

}
