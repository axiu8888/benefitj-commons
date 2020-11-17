package com.benefitj.network;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.util.List;

/**
 * 请求接口
 */
public interface IRetrofitRequest<Api> {

  /**
   * 获取创建的ServiceApi
   */
  Api getApi();

  /**
   * 获取基地址
   */
  String getBaseUrl();

  /**
   * 获取ServiceApi的Class
   */
  Class<Api> getApiType();

  /**
   * 构建Retrofit
   *
   * @param builder 根据当前配置创建的Retrofit.Builder对象
   * @return 返回创建的Retrofit对象
   */
  Retrofit buildRetrofit(Retrofit.Builder builder);

  /**
   * 获取创建接口代理的Retrofit
   */
  Retrofit getRetrofit();

  /**
   * 创建 OkHttpClient 对象
   */
  OkHttpClient getOkHttpClient();

  /**
   * 创建网络请求的拦截器
   */
  List<Interceptor> getNetworkInterceptor();

  /**
   * 创建Converter.Factory的实现类对象，如 {@link retrofit2.converter.jackson.JacksonConverterFactory#create()}
   */
  List<Converter.Factory> getConverterFactory();

  /**
   * 创建 CallAdapter.Factory 的实现类对象，如 {@link retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory#create()}
   */
  List<CallAdapter.Factory> getCallAdapterFactory();
}
