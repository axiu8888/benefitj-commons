package com.benefitj.http;

import com.benefitj.core.ProxyUtils;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * API对象构建器
 *
 * @param <T> API类型
 */
public interface ApiBuilder<T> {

  /**
   * 获取API的接口类型
   */
  Class<T> getApiClass();

  /**
   * 设置基基本的URL前缀
   *
   * @param apiClass API的接口类型
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> setApiClass(Class<T> apiClass);

  /**
   * 获取基本的URL前缀
   */
  String getBaseUrl();

  /**
   * 设置基本的URL前缀
   *
   * @param baseUrl 路径
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> setBaseUrl(String baseUrl);

  /**
   * 获取OKHttp客户端
   */
  OkHttpClient getOkHttpClient();

  /**
   * 设置OkHttp客户端
   *
   * @param client 客户端
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> setOkHttpClient(OkHttpClient client);

  /**
   * 获取网络请求拦截器
   */
  List<Interceptor> getNetworkInterceptors();

  /**
   * 添加网络请求拦截器
   *
   * @param interceptor 拦截器
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> addNetworkInterceptors(Interceptor... interceptor);

  /**
   * 获取网络请求拦截器
   */
  List<Interceptor> getInterceptors();

  /**
   * 添加网络请求拦截器
   *
   * @param interceptor 拦截器
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> addInterceptors(Interceptor... interceptor);

  /**
   * 获取GZIP拦截器
   */
  Interceptor getGzipInterceptor();

  /**
   * 设置GZIP拦截器
   *
   * @param gzipInterceptor 拦截器
   */
  ApiBuilder<T> setGzipInterceptor(Interceptor gzipInterceptor);

  /**
   * 是否支持GZIP
   */
  boolean isGzipEnable();

  /**
   * 设置是否支持GZIP
   *
   * @param gzip 是否支持
   */
  ApiBuilder<T> setGzipEnable(boolean gzip);

  /**
   * 设置Http日志打印层级
   *
   * @param level 打印的层级
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> setLogLevel(HttpLoggingInterceptor.Level level);

  /**
   * 获取转换器工厂 <br/>
   * <p>
   * 如: <br/>
   * 1. {@link retrofit2.converter.scalars.ScalarsConverterFactory#create()}
   * 2. {@link retrofit2.converter.jackson.JacksonConverterFactory#create()}
   * 3. {@link retrofit2.converter.gson.GsonConverterFactory#create()}
   */
  List<Converter.Factory> getConverterFactories();

  /**
   * 添加转换器工厂 <br/>
   *
   * @param factories 转换器工厂
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> addConverterFactories(Converter.Factory... factories);

  /**
   * 添加转换器工厂 <br/>
   *
   * @param factory 转换器工厂
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> addConverterFactoryIfAbsent(Converter.Factory factory);

  /**
   * 添加默认转换器工厂
   */
  default ApiBuilder<T> useDefaultConverterFactories() {
    if (isClassExist("retrofit2.converter.scalars.ScalarsConverterFactory")) {
      addConverterFactoryIfAbsent(ScalarsConverterFactory.create());
    }
    if (isClassExist("retrofit2.converter.jackson.JacksonConverterFactory")) {
      addConverterFactoryIfAbsent(JacksonConverterFactory.create());
    }
    if (isClassExist("retrofit2.converter.gson.GsonConverterFactory")) {
      addConverterFactoryIfAbsent(GsonConverterFactory.create());
    }
    return this;
  }

  /**
   * 获取调用适配器工厂，如: <br/>
   * <p>
   * {@link retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory#create()} <br/>
   */
  List<CallAdapter.Factory> getCallAdapterFactories();

  /**
   * 添加调用适配器工厂，如: <br/>
   * <p>
   * {@link retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory#create()} <br/>
   */
  ApiBuilder<T> addCallAdapterFactories(CallAdapter.Factory... factories);

  /**
   * 添加调用适配器工厂，如: <br/>
   * <p>
   * {@link retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory#create()} <br/>
   */
  ApiBuilder<T> addCallAdapterFactoryIfAbsent(CallAdapter.Factory factory);

  /**
   * 添加默认调用适配器工厂
   */
  default ApiBuilder<T> useDefaultCallAdapterFactories() {
    if (isClassExist("retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory")) {
      addCallAdapterFactoryIfAbsent(RxJava3CallAdapterFactory.create());
    }
    return this;
  }

  /**
   * 获取是否添加默认 适配器和转换器
   */
  boolean isUseDefault();

  /**
   * 设置是否添加默认的适配器和转换器
   *
   * @param useDefault 是否添加默认对象
   * @return 返回 RetrofitApi
   */
  ApiBuilder<T> setUseDefault(boolean useDefault);

  /**
   * 创建API对象
   */
  T build(Consumer<Retrofit.Builder> consumer);

  /**
   * 创建API对象
   */
  default T build() {
    return build(builder -> {
      /* ignore */
    });
  }

  /**
   * 创建构建器对象
   *
   * @param <T> API接口的类型
   * @return 返回构建器对象
   */
  static <T> ApiBuilder<T> newBuilder(Class<T> apiClass) {
    return new ApiBuilderImpl<>(apiClass);
  }

  /**
   * 创建构建器对象
   *
   * @param <T> API接口的类型
   * @return 返回构建器对象
   */
  static <T> ApiBuilder<T> newBuilder(Class<T> apiClass, String baseUrl, @Nullable OkHttpClient client) {
    return new ApiBuilderImpl<>(apiClass, baseUrl, client);
  }

  /**
   * 创建API接口实现
   *
   * @param apiClass API接口类型
   * @param baseUrl  基本地址
   * @param <T>      接口类型
   * @return 返回接口对象
   */
  static <T> T create(Class<T> apiClass, String baseUrl) {
    return create(apiClass, baseUrl, null);
  }

  /**
   * 创建API接口实现
   *
   * @param apiClass API接口
   * @param baseUrl  基本地址
   * @param client   OkHttp客户端
   * @param <T>      接口类型
   * @return 返回接口对象
   */
  static <T> T create(Class<T> apiClass, String baseUrl, @Nullable OkHttpClient client) {
    return newBuilder(apiClass, baseUrl, client).build();
  }

  /**
   * 检查类是否存在
   */
  static boolean isClassExist(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException ignore) {
      return false;
    }
  }

  /**
   * 创建HTTP客户端
   *
   * @param apiType      API类型
   * @param baseUrl      基地址
   * @param interceptors 拦截器
   * @return 返回API对象
   */
  static <T> T createApiProxy(Class<T> apiType,
                              String baseUrl,
                              Interceptor... interceptors) {
    return createApiProxy(apiType, baseUrl, HttpLoggingInterceptor.Level.NONE, interceptors);
  }

  /**
   * 创建HTTP客户端
   *
   * @param apiType      API类型
   * @param baseUrl      基地址
   * @param level        日志等级
   * @param interceptors 拦截器
   * @return 返回API对象
   */
  static <T> T createApiProxy(Class<T> apiType,
                              String baseUrl,
                              HttpLoggingInterceptor.Level level,
                              Interceptor... interceptors) {
    return createApiProxy(apiType
        , baseUrl
        , builder -> builder.setLogLevel(level).addNetworkInterceptors(interceptors)

    );
  }

  /**
   * 创建HTTP客户端
   *
   * @param apiType  API类型
   * @param baseUrl  基地址
   * @param consumer 自定义ApiBuilder
   * @return 返回API对象
   */
  static <T> T createApiProxy(Class<T> apiType,
                              String baseUrl,
                              Consumer<ApiBuilder<T>> consumer) {
    return createApiProxy(apiType, baseUrl, consumer, ApiInvocationHandler.createScheduler());
  }

  /**
   * 创建HTTP客户端
   *
   * @param apiType  API类型
   * @param baseUrl  基地址
   * @param consumer 自定义ApiBuilder
   * @param handler  处理器
   * @return 返回API对象
   */
  static <T> T createApiProxy(Class<T> apiType,
                              String baseUrl,
                              Consumer<ApiBuilder<T>> consumer,
                              ApiInvocationHandler<T> handler) {
    ApiBuilder<T> builder = ApiBuilder.newBuilder(apiType)
        .setUseDefault(true)
        .setBaseUrl(baseUrl);
    consumer.accept(builder);
    final T api = builder.build();
    return ProxyUtils.newProxy(apiType, (proxy, method, args) -> handler.invoke(api, method, args));
  }

}
