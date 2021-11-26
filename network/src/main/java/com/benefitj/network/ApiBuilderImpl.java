package com.benefitj.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
   * 转换器工厂
   */
  private final List<Converter.Factory> converterFactories = new LinkedList<>();
  /**
   * 调用适配器工厂
   */
  private final List<CallAdapter.Factory> callAdapterFactories = new LinkedList<>();

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
  public List<Converter.Factory> getConverterFactories() {
    return converterFactories;
  }

  @Override
  public ApiBuilderImpl<T> addConverterFactories(Converter.Factory... factories) {
    this.converterFactories.addAll(Arrays.asList(factories));
    return this;
  }

  @Override
  public List<CallAdapter.Factory> getCallAdapterFactories() {
    return callAdapterFactories;
  }

  @Override
  public ApiBuilderImpl<T> addCallAdapterFactories(CallAdapter.Factory... factories) {
    this.callAdapterFactories.addAll(Arrays.asList(factories));
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
    // 添加网络拦截器
    for (Interceptor interceptor : getNetworkInterceptors()) {
      clientBuilder.addNetworkInterceptor(interceptor);
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
