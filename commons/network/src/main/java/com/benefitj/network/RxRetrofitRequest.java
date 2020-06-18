package com.benefitj.network;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Retrofit结合RxJava使用
 */
public class RxRetrofitRequest<Api> extends RetrofitRequest<Api> {

  /**
   * 创建新的Request
   *
   * @param apiClass 接口
   * @param baseUrl  基地址
   * @return 返回接口的代理类
   */
  public static <T> T create(String baseUrl, Class<T> apiClass) {
    return new RxRetrofitRequest<T>(baseUrl, apiClass).getApi();
  }

  /**
   * 创建新的Request
   *
   * @param apiClass 接口
   * @param baseUrl  基地址
   * @return 返回接口的代理类
   */
  public static <T> T create(String baseUrl, Class<T> apiClass, OkHttpClient client) {
    RxRetrofitRequest<T> request = new RxRetrofitRequest<>(baseUrl, apiClass);
    request.setOkHttpClient(client);
    return request.getApi();
  }

  /* ******************************************************************* */


  /**
   * RxJava的默认调度器
   */
  private Scheduler observerScheduler = Schedulers.trampoline();
  /**
   * OkHttpClient
   */
  private volatile OkHttpClient okHttpClient;

  private Class<Api> apiClass;

  public RxRetrofitRequest(String baseUrl) {
    super(baseUrl);
  }

  public RxRetrofitRequest(String baseUrl, Class<Api> apiClass) {
    super(baseUrl);
    this.apiClass = apiClass;
  }

  @Override
  public Class<Api> getApiClass() {
    Class<Api> klass = this.apiClass;
    if (klass == null) {
      throw new IllegalStateException("Api的class不能为null, 请覆写此方法!");
    }
    return klass;
  }

  public void setOkHttpClient(OkHttpClient client) {
    this.okHttpClient = client;
  }

  @Override
  public OkHttpClient getOkHttpClient() {
    OkHttpClient c = this.okHttpClient;
    if (c == null) {
      synchronized (this) {
        if ((c = this.okHttpClient) == null) {
          OkHttpClient.Builder builder = new OkHttpClient.Builder()
              .connectTimeout(5, TimeUnit.SECONDS)
              .readTimeout(30, TimeUnit.SECONDS)
              .writeTimeout(30, TimeUnit.SECONDS);
          getNetworkInterceptor().forEach(builder::addNetworkInterceptor);
          c = (this.okHttpClient = builder.build());
        }
      }
    }
    return c;
  }

  /**
   * 默认调度器
   */
  public void setObserverScheduler(Scheduler observerScheduler) {
    this.observerScheduler = observerScheduler;
  }

  /**
   * 获取调度器
   */
  public Scheduler getObserverScheduler() {
    return observerScheduler != null ? observerScheduler : Schedulers.trampoline();
  }

  @Override
  public List<Converter.Factory> getConverterFactory() {
    return Arrays.asList(JacksonConverterFactory.create(), ScalarsConverterFactory.create());
  }

  @Override
  public List<CallAdapter.Factory> getCallAdapterFactory() {
    return Collections.singletonList(RxJava2CallAdapterFactory.create());
  }

  /* ******************************************************************* */

  /**
   * 发送请求
   *
   * @param observable Observable对象
   * @param <T>        类型
   * @return 返回结果
   */
  public <T> Observable<T> schedule(Observable<T> observable, Scheduler scheduler) {
    return observable.subscribeOn(scheduler).observeOn(getObserverScheduler());
  }

  /**
   * 发送请求
   *
   * @param observable Observable对象
   * @param <T>        类型
   * @return 返回结果
   */
  public <T> Observable<T> scheduleIO(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io()).observeOn(getObserverScheduler());
  }

  /**
   * 发送请求
   *
   * @param observable Observable对象
   * @param scheduler  调度器
   * @param <T>        类型
   * @return 返回结果
   */
  public <T> Observable<T> scheduleIO(Observable<T> observable, Scheduler scheduler) {
    return observable.subscribeOn(Schedulers.io()).observeOn(scheduler);
  }

  /**
   * 发送请求
   *
   * @param observable Observable对象
   * @param scheduler  调度器
   * @param <T>        类型
   * @return 返回结果
   */
  public <T> Observable<T> scheduleComputation(Observable<T> observable, Scheduler scheduler) {
    return observable.subscribeOn(Schedulers.computation()).observeOn(scheduler);
  }

  /**
   * 发送请求
   *
   * @param observable Observable对象
   * @param <T>        类型
   * @return 返回结果
   */
  public <T> Observable<T> scheduleComputation(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.computation()).observeOn(getObserverScheduler());
  }
}
