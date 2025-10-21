package com.benefitj.http;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.IdUtils;
import com.benefitj.core.SingletonSupplier;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * HTTP请求工具
 */
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
   * 请求头
   */
  private Interceptor headersInterceptor;
  /**
   * GZIP
   */
  private final GzipRequestInterceptor gzip = new GzipRequestInterceptor(false);

  /**
   * HTTP客户端
   */
  private volatile OkHttpClient client = new OkHttpClient.Builder()
      .connectTimeout(5, TimeUnit.SECONDS)
      .readTimeout(5, TimeUnit.MINUTES)
      .writeTimeout(5, TimeUnit.MINUTES)
      .addInterceptor(gzip)
      .addNetworkInterceptor(chain -> callInterceptor(getHeadersInterceptor(), chain))
      .addNetworkInterceptor(chain -> callInterceptor(getNetworkInterceptor(), chain))
      .addNetworkInterceptor(httpLogging.setLevel(HttpLoggingInterceptor.Level.NONE))
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

  public HttpHelper setGzipEnable(boolean enable) {
    this.gzip.setEnable(enable);
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

  public Interceptor getHeadersInterceptor() {
    return headersInterceptor;
  }

  public HttpHelper setHeadersInterceptor(Interceptor headersInterceptor) {
    this.headersInterceptor = headersInterceptor;
    return this;
  }

  /**
   * 创建GET请求
   *
   * @param url     URL地址
   * @param headers 请求头
   * @return 返回创建的请求对象
   */
  public Request newGetRequest(String url, Map<String, String> headers) {
    return new Request.Builder().url(url).headers(Headers.of(headers)).get().build();
  }

  /**
   * 创建POST请求
   *
   * @param url        URL地址
   * @param headers    请求头
   * @param parameters 请求参数
   * @param name       名称
   * @param files      文件
   * @return 返回创建的请求对象
   */
  public Request newPostRequest(String url, Map<String, String> headers, Map<String, String> parameters, String name, File... files) {
    return newPostRequest(url, BodyUtils.formBody(parameters, files, name), headers);
  }

  /**
   * 创建POST请求
   *
   * @param url     URL地址
   * @param body    请求体
   * @param headers 请求头
   * @return 返回创建的请求对象
   */
  public Request newPostRequest(String url, RequestBody body, Map<String, String> headers) {
    return new Request.Builder().url(url).headers(Headers.of(headers)).post(body).build();
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
    return execute(newGetRequest(url, headers));
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
    return post(url, RequestBody.create("", MediaType.get("application/json")), headers);
  }

  /**
   * 发送POST请求
   *
   * @param url 请求地址
   * @return 返回响应
   */
  public Response post(String url, File file) {
    return post(url, RequestBody.create(file, MediaType.get("application/json")));
  }

  /**
   * 发送POST请求
   *
   * @param url     请求地址
   * @param headers 请求头
   * @param file    上传的文件
   * @param name    文件的参数名
   * @return 返回响应
   */
  public Response post(String url, Map<String, String> headers, File file, String name) {
    return post(url, BodyUtils.formBody(Collections.emptyMap(), file, name), headers);
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
    return execute(newPostRequest(url, body, headers));
  }

  /**
   * 发送请求
   *
   * @param request 请求
   * @return 返回响应
   */
  public Call newCall(Request request) {
    return getClient().newCall(request);
  }

  /**
   * 发送请求，同步
   *
   * @param request 请求
   * @return 返回响应
   */
  public Response execute(Request request) {
    try {
      return newCall(request).execute();
    } catch (IOException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 发送请求，异步
   *
   * @param request 请求
   */
  public Call enqueue(Request request, Callback callback) {
    Call call = newCall(request);
    call.enqueue(callback);
    return call;
  }

  /**
   * 上传
   *
   * @param url              请求URL
   * @param headers          请求头
   * @param parameters       参数
   * @param name             文件名
   * @param src              上传的文件
   * @param async            是否异步
   * @param progressListener 进度监听
   * @return 返回调用对象
   */
  public Call upload(String url, Map<String, String> headers, Map<String, String> parameters, String name, File src, boolean async, FileProgressListener progressListener) {
    RequestBody requestBody = BodyUtils.progressRequestBody(parameters, src, name, progressListener);
    Request request = newPostRequest(url, requestBody, headers);
    Call call = newCall(request);
    progressListener.onStart(call);
    if (async) {
      enqueue(request, new Callback() {
        @Override
        public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
          try {
            progressListener.onFailure(call, e, src);
          } finally {
            progressListener.onFinish(call);
          }
        }

        @Override
        public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
          try {
            if (response.isSuccessful()) {
              progressListener.onSuccess(call, response, src);
            } else {
              progressListener.onFailure(call, new IOException(response.message()), src);
            }
          } finally {
            progressListener.onFinish(call);
          }
        }
      });
    } else {
      try {
        Response response = call.execute();
        if (response.isSuccessful()) {
          progressListener.onSuccess(call, response, null);
        } else {
          progressListener.onFailure(call, new IOException(response.message()), src);
        }
      } catch (Exception e) {
        progressListener.onFailure(call, e, src);
      } finally {
        progressListener.onFinish(call);
      }
    }
    return call;
  }

  /**
   * 下载
   *
   * @param url              请求URL
   * @param cacheDir         目标文件的缓存目录
   * @param filename         目标文件名
   * @param async            是否异步下载
   * @param progressListener 监听
   * @return 返回调用对象
   */
  public Call download(String url, File cacheDir, String filename, boolean async, FileProgressListener progressListener) {
    return download(url, Collections.emptyMap(), cacheDir, filename, async, progressListener);
  }

  /**
   * 下载
   *
   * @param url              请求URL
   * @param headers          进请求头
   * @param cacheDir         目标文件的缓存目录
   * @param filename         目标文件名
   * @param async            是否异步下载
   * @param progressListener 监听
   * @return 返回调用对象
   */
  public Call download(String url, Map<String, String> headers, File cacheDir, String filename, boolean async, FileProgressListener progressListener) {
    return download(newGetRequest(url, headers), cacheDir, filename, async, progressListener);
  }

  /**
   * 下载
   *
   * @param request          请求
   * @param cacheDir         目标文件的缓存目录
   * @param filename         目标文件名
   * @param async            是否异步下载
   * @param progressListener 监听
   * @return 返回调用对象
   */
  public Call download(Request request, File cacheDir, String filename, boolean async, FileProgressListener progressListener) {
    Call call = newCall(request);
    if (async) {
      progressListener.onStart(call);
      call.enqueue(new Callback() {
        @Override
        public void onFailure(@Nonnull Call call, @Nonnull IOException e) {
          try {
            progressListener.onFailure(call, e, null);
          } finally {
            progressListener.onFinish(call);
          }
        }

        @Override
        public void onResponse(@Nonnull Call call, @Nonnull Response response) throws IOException {
          try {
            progressListener.onStart(call);
            if (BodyUtils.getContentType(response.headers()).startsWith("application/json;")) {
              progressListener.onFailure(call, new IOException(response.body().string()), null);
              return;
            }
            File dest = new File(cacheDir, getFilename(response, filename));
            BodyUtils.progressResponseBody(response.body(), dest, progressListener);
            progressListener.onSuccess(call, response, dest);
          } catch (Exception e) {
            progressListener.onFailure(call, e, null);
          } finally {
            progressListener.onFinish(call);
          }
        }
      });
    } else {
      try {
        progressListener.onStart(call);
        Response response = call.execute();
        if (response.isSuccessful()) {
          if (BodyUtils.getContentType(response.headers()).startsWith("application/json;")) {
            progressListener.onFailure(call, new IOException(response.body().string()), null);
          } else {
            File dest = new File(cacheDir, getFilename(response, filename));
            BodyUtils.progressResponseBody(response.body(), dest, progressListener);
            progressListener.onSuccess(call, response, dest);
          }
        } else {
          progressListener.onFailure(call, new IOException(response.message()), null);
        }
      } catch (IOException e) {
        progressListener.onFailure(call, e, null);
      } finally {
        progressListener.onFinish(call);
      }
    }
    return call;
  }

  private String getFilename(Response response, String filename) {
    if (StringUtils.isNotBlank(filename)) {
      return filename;
    }
    List<String> paths = response.request().url().pathSegments();
    String tmpFilename = paths.get(paths.size() - 1);
    tmpFilename = StringUtils.isNotBlank(tmpFilename) ? tmpFilename : IdUtils.uuid();
    return BodyUtils.getFilename(response.headers(), tmpFilename);
  }


  public static Response callInterceptor(Interceptor source, @NotNull Interceptor.Chain chain) throws IOException {
    return source != null ? source.intercept(chain)  : chain.proceed(chain.request());
  }


}
