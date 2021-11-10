package com.benefitj.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;

import java.util.Collections;
import java.util.List;

/**
 * 请求基类
 */
public abstract class RetrofitRequest<Api> implements IRetrofitRequest<Api> {

  private Retrofit retrofit;
  /**
   * 请求接口
   */
  private volatile Api serviceApi;
  /**
   * 请求基地址
   */
  private String baseUrl;
  /**
   * 日志级别
   */
  private HttpLoggingInterceptor.Level loggingLevel = HttpLoggingInterceptor.Level.NONE;

  public RetrofitRequest() {
  }

  public RetrofitRequest(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  private static boolean isBlank(String s) {
    return s == null || s.trim().isEmpty();
  }

  /**
   * 创建Api服务
   *
   * @return 返回新创建的ServiceApi
   */
  public Api createApi(Class<Api> apiClass) {
    String url = getBaseUrl();
    if (isBlank(url)) {
      throw new IllegalStateException("请保证请求的URL不为空!");
    }

    Builder builder = new Builder();
    builder.baseUrl(url);

    OkHttpClient.Builder client = getOkHttpClient().newBuilder();
    for (Interceptor interceptor : getNetworkInterceptor()) {
      client.addNetworkInterceptor(interceptor);
    }
    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(getLoggingLevel());
    client.addNetworkInterceptor(logging);
    builder.client(client.build());

    List<CallAdapter.Factory> adapterFactories = getCallAdapterFactory();
    if (adapterFactories != null && !adapterFactories.isEmpty()) {
      for (CallAdapter.Factory factory : adapterFactories) {
        builder.addCallAdapterFactory(factory);
      }
    }

    List<Converter.Factory> converterFactories = getConverterFactory();
    if (converterFactories != null && !converterFactories.isEmpty()) {
      for (Converter.Factory factory : converterFactories) {
        builder.addConverterFactory(factory);
      }
    }

    return (this.retrofit = buildRetrofit(builder)).create(apiClass);
  }

  /**
   * 获取基地址
   */
  @Override
  public String getBaseUrl() {
    return baseUrl;
  }

  public RetrofitRequest<Api> setBaseUrl(String baseUrl) {
    if (isBlank(baseUrl)) {
      throw new IllegalArgumentException("baseUrl");
    }
    this.baseUrl = baseUrl;
    return this;
  }

  @Override
  public Api getApi() {
    Api api = this.serviceApi;
    if (api == null) {
      synchronized (this) {
        if ((api = this.serviceApi) != null) {
          return api;
        }
        api = (this.serviceApi = createApi(getApiType()));
      }
    }
    return api;
  }

  @Override
  public Retrofit buildRetrofit(Builder builder) {
    return builder.build();
  }

  @Override
  public Retrofit getRetrofit() {
    return retrofit;
  }

  @Override
  public abstract OkHttpClient getOkHttpClient();

  @Override
  public List<Interceptor> getNetworkInterceptor() {
    return Collections.emptyList();
  }

  /**
   *
   */
  @Override
  public abstract List<Converter.Factory> getConverterFactory();

  @Override
  public abstract List<CallAdapter.Factory> getCallAdapterFactory();

  public HttpLoggingInterceptor.Level getLoggingLevel() {
    return loggingLevel;
  }

  public RetrofitRequest<Api> setLoggingLevel(HttpLoggingInterceptor.Level loggingLevel) {
    this.loggingLevel = loggingLevel;
    return this;
  }

}

