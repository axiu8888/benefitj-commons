package com.benefitj.http;

import com.benefitj.core.SingletonSupplier;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HttpHelper {

  static SingletonSupplier<HttpHelper> singleton = SingletonSupplier.of(HttpHelper::new);

  public static HttpHelper get() {
    return singleton.get();
  }

  /**
   * 日志
   */
  private final HttpLoggingInterceptor httpLogging = new HttpLoggingInterceptor();
  /**
   * 拦截器
   */
  private Interceptor networkInterceptor;

  /**
   * HTTP客户端
   */
  private OkHttpClient client = new OkHttpClient.Builder()
      .connectTimeout(5, TimeUnit.SECONDS)
      .readTimeout(120, TimeUnit.SECONDS)
      .writeTimeout(120, TimeUnit.SECONDS)
      .addNetworkInterceptor(httpLogging.setLevel(HttpLoggingInterceptor.Level.NONE))
      .addNetworkInterceptor(chain -> {
        Interceptor interceptor = getNetworkInterceptor();
        return interceptor != null ? interceptor.intercept(chain) : chain.proceed(chain.request());
      })
      .build();

  public HttpHelper() {
  }

  public OkHttpClient getClient() {
    return client;
  }

  public HttpHelper setClient(OkHttpClient client) {
    this.client = client;
    return this;
  }

  public HttpHelper setLogLevel(HttpLoggingInterceptor.Level level) {
    httpLogging.setLevel(level);
    return this;
  }

  public Interceptor getNetworkInterceptor() {
    return networkInterceptor;
  }

  public HttpHelper setNetworkInterceptor(Interceptor networkInterceptor) {
    this.networkInterceptor = networkInterceptor;
    return this;
  }

  /**
   * 发送GET请求
   *
   * @param url 请求地址
   * @return 返回响应
   */
  public Response get(String url) {
    return get(url, Collections.emptyMap());
  }

  /**
   * 发送GET请求
   *
   * @param url     请求地址
   * @param headers 请求头
   * @return 返回响应
   */
  public Response get(String url, Map<String, String> headers) {
    return request(new Request.Builder()
        .url(url)
        .headers(Headers.of(headers))
        .get()
        .build());
  }

  /**
   * 发送POST请求
   *
   * @param url 请求地址
   * @return 返回响应
   */
  public Response post(String url) {
    return post(url, Collections.emptyMap());
  }

  /**
   * 发送POST请求
   *
   * @param url     请求地址
   * @param headers 请求头
   * @return 返回响应
   */
  public Response post(String url, Map<String, String> headers) {
    return request(new Request.Builder()
        .url(url)
        .headers(Headers.of(headers))
        .build());
  }

  /**
   * 发送POST请求
   *
   * @param url  请求地址
   * @param file 请求体
   * @return 返回响应
   */
  public Response post(String url, File file) {
    return post(url, RequestBody.create(file, MediaType.parse("application/json")));
  }

  /**
   * 发送POST请求
   *
   * @param url  请求地址
   * @param body 请求体
   * @return 返回响应
   */
  public Response post(String url, RequestBody body) {
    return post(url, body, Collections.emptyMap());
  }

  /**
   * 发送POST请求
   *
   * @param url     请求地址
   * @param body    请求体
   * @param headers 请求头
   * @return 返回响应
   */
  public Response post(String url, RequestBody body, Map<String, String> headers) {
    return request(new Request.Builder()
        .url(url)
        .headers(Headers.of(headers))
        .post(body)
        .build());
  }

  /**
   * 发送请求
   *
   * @param request 请求
   * @return 返回响应
   */
  public Response request(Request request) {
    try {
      Call call = getClient().newCall(request);
      return call.execute();
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage(), e);
    }
  }


}
