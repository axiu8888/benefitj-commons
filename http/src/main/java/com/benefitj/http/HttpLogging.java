package com.benefitj.http;


import java.io.IOException;

import lombok.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * HTTP日志打印
 */
public class HttpLogging implements Interceptor {

  private final HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();

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
