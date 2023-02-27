package com.benefitj.http;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.util.*;
import java.util.function.Consumer;

/**
 * API对象构建器实现
 *
 * @param <T> API类型
 */
public class ApiBuilderImpl<T> implements ApiBuilder<T> {

  /**
   * 接口类型
   */
  private Class<T> apiClass;
  /**
   * URL前缀
   */
  private String baseUrl;
  /**
   * OkHttp客户端
   */
  private OkHttpClient okHttpClient;
  /**
   * 网络请求拦截器
   */
  private final List<Interceptor> networkInterceptors = new LinkedList<>();
  /**
   * 请求拦截器
   */
  private final List<Interceptor> interceptors = new LinkedList<>();
  /**
   * 转换器工厂
   */
  private final LinkedHashMap<Class<? extends Converter.Factory>, Converter.Factory> converterFactories = new LinkedHashMap<>();
  /**
   * 调用适配器工厂
   */
  private final LinkedHashMap<Class<? extends CallAdapter.Factory>, CallAdapter.Factory> callAdapterFactories = new LinkedHashMap<>();
  /**
   * Gzip请求
   */
  private Interceptor gzipInterceptor = new GzipRequestInterceptor();
  /**
   * HTTP日志
   */
  private final HttpLogging httpLogging = new HttpLogging().setLevel(HttpLoggingInterceptor.Level.NONE);
  /**
   * 是否支持GZIP
   */
  private boolean gzipEnable = false;

  private boolean useDefault = true;

  public ApiBuilderImpl() {
  }

  public ApiBuilderImpl(Class<T> apiClass) {
    this.apiClass = apiClass;
  }

  public ApiBuilderImpl(Class<T> apiClass, String baseUrl, OkHttpClient okHttpClient) {
    this.apiClass = apiClass;
    this.baseUrl = baseUrl;
    this.okHttpClient = okHttpClient;
  }

  public HttpLogging getHttpLogging() {
    return httpLogging;
  }

  @Override
  public Class<T> getApiClass() {
    return apiClass;
  }

  @Override
  public ApiBuilderImpl<T> setApiClass(Class<T> apiClass) {
    this.apiClass = apiClass;
    return this;
  }

  @Override
  public String getBaseUrl() {
    return baseUrl;
  }

  @Override
  public ApiBuilderImpl<T> setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
    return this;
  }

  @Override
  public OkHttpClient getOkHttpClient() {
    return okHttpClient;
  }

  @Override
  public ApiBuilderImpl<T> setOkHttpClient(OkHttpClient client) {
    this.okHttpClient = client;
    return this;
  }

  @Override
  public List<Interceptor> getNetworkInterceptors() {
    return networkInterceptors;
  }

  @Override
  public ApiBuilderImpl<T> addNetworkInterceptors(Interceptor... interceptor) {
    this.networkInterceptors.addAll(Arrays.asList(interceptor));
    return this;
  }

  @Override
  public List<Interceptor> getInterceptors() {
    return interceptors;
  }

  @Override
  public ApiBuilderImpl<T> addInterceptors(Interceptor... interceptor) {
    this.interceptors.addAll(Arrays.asList(interceptor));
    return this;
  }

  @Override
  public Interceptor getGzipInterceptor() {
    return gzipInterceptor;
  }

  @Override
  public ApiBuilderImpl<T> setGzipInterceptor(Interceptor gzipInterceptor) {
    this.gzipInterceptor = gzipInterceptor;
    return this;
  }
  @Override
  public boolean isGzipEnable() {
    return this.gzipEnable;
  }

  @Override
  public ApiBuilderImpl<T> setGzipEnable(boolean gzipEnable) {
    this.gzipEnable = gzipEnable;
    return this;
  }

  @Override
  public ApiBuilderImpl<T> setLogLevel(HttpLoggingInterceptor.Level level) {
    this.httpLogging.setLevel(level);
    return this;
  }

  @Override
  public List<Converter.Factory> getConverterFactories() {
    return new ArrayList<>(converterFactories.values());
  }

  @Override
  public ApiBuilderImpl<T> addConverterFactories(Converter.Factory... factories) {
    for (Converter.Factory factory : factories) {
      this.converterFactories.put(factory.getClass(), factory);
    }
    return this;
  }

  @Override
  public ApiBuilder<T> addConverterFactoryIfAbsent(Converter.Factory factory) {
    this.converterFactories.putIfAbsent(factory.getClass(), factory);
    return this;
  }

  @Override
  public List<CallAdapter.Factory> getCallAdapterFactories() {
    return new ArrayList<>(callAdapterFactories.values());
  }

  @Override
  public ApiBuilderImpl<T> addCallAdapterFactories(CallAdapter.Factory... factories) {
    for (CallAdapter.Factory factory : factories) {
      this.callAdapterFactories.put(factory.getClass(), factory);
    }
    return this;
  }

  @Override
  public ApiBuilderImpl<T> addCallAdapterFactoryIfAbsent(CallAdapter.Factory factory) {
    this.callAdapterFactories.putIfAbsent(factory.getClass(), factory);
    return this;
  }

  @Override
  public boolean isUseDefault() {
    return useDefault;
  }

  @Override
  public ApiBuilderImpl<T> setUseDefault(boolean useDefault) {
    this.useDefault = useDefault;
    return this;
  }

  @Override
  public T build(Consumer<Retrofit.Builder> consumer) {
    String url = getBaseUrl();
    if (url == null || url.trim().isEmpty()) {
      throw new IllegalStateException("baseUrl不能为空!");
    }

    Class<T> apiClass = getApiClass();
    if (apiClass == null) {
      throw new IllegalStateException("apiClass不能为null!");
    }

    Retrofit.Builder builder = new Retrofit.Builder();
    builder.baseUrl(url);

    OkHttpClient client = getOkHttpClient();
    OkHttpClient.Builder clientBuilder;
    if (client == null) {
      clientBuilder = new OkHttpClient.Builder();
    } else {
      clientBuilder = client.newBuilder();
    }
    clientBuilder.addNetworkInterceptor(httpLogging);
    // 拦截器
    for (Interceptor interceptor : getInterceptors()) {
      clientBuilder.addInterceptor(interceptor);
    }
    // 添加网络拦截器
    for (Interceptor interceptor : getNetworkInterceptors()) {
      clientBuilder.addNetworkInterceptor(interceptor);
    }
    Interceptor gzipInterceptor = getGzipInterceptor();
    if (isGzipEnable() && gzipInterceptor != null) {
      clientBuilder.addInterceptor(gzipInterceptor);
    }
    builder.client(clientBuilder.build());

    if (isUseDefault()) {
      useDefaultConverterFactories();
      useDefaultCallAdapterFactories();
    }

    // 调用适配器工厂
    for (CallAdapter.Factory factory : getCallAdapterFactories()) {
      builder.addCallAdapterFactory(factory);
    }

    // 转换器工厂
    for (Converter.Factory factory : getConverterFactories()) {
      builder.addConverterFactory(factory);
    }

    if (consumer != null) {
      consumer.accept(builder);
    }

    Retrofit retrofit = builder.build();
    return retrofit.create(apiClass);
  }
}
