package com.benefitj.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

/**
 * OkHttpClient
 */
public enum HttpClient {

  INSTANCE;

  private HttpLogging logging = new HttpLogging().setLevel(HttpLoggingInterceptor.Level.NONE);

  private final OkHttpClient client = new OkHttpClient.Builder()
      .addInterceptor(logging)
      .connectTimeout(30, TimeUnit.MINUTES)
      .readTimeout(60, TimeUnit.MINUTES)
      .writeTimeout(60, TimeUnit.MINUTES)
      .pingInterval(30, TimeUnit.MINUTES)
      .build();

  public static void setLevel(HttpLoggingInterceptor.Level level) {
    INSTANCE.logging.setLevel(level);
  }

  /**
   * 获取OkHttp客户端
   */
  public static OkHttpClient get() {
    return INSTANCE.client;
  }

  /**
   * 创建 WebSocket
   *
   * @param url      URL地址
   * @param listener 监听
   * @return 返回创建的WebSocket对象
   */
  public static WebSocket newWebSocket(String url, WebSocketListener listener) {
    Request request = new Request.Builder()
        .url(url)
        .get()
        .build();
    return newWebSocket(request, listener);
  }

  /**
   * 创建 WebSocket
   *
   * @param url URL地址
   * @return 返回创建的WebSocket对象
   */
  public static WebSocket newWebSocket(WebSocketImpl socket, String url) {
    Request request = new Request.Builder()
        .url(url)
        .get()
        .build();
    return newWebSocket(socket, request);
  }

  /**
   * 创建 WebSocket
   *
   * @param request  URL地址
   * @param listener 监听
   * @return 返回创建的WebSocket对象
   */
  public static WebSocket newWebSocket(Request request, WebSocketListener listener) {
    return newWebSocket(new WebSocketImpl(listener), request);
  }

  /**
   * 创建 WebSocket
   *
   * @param socket  WebSocket客户端
   * @param request URL地址
   * @return 返回创建的WebSocket对象
   */
  public static WebSocket newWebSocket(WebSocketImpl socket, Request request) {
    return newWebSocket(get(), socket, request);
  }

  /**
   * 创建 WebSocket
   *
   * @param client  客户端
   * @param socket  WebSocket客户端
   * @param request URL地址
   * @return 返回创建的WebSocket对象
   */
  public static WebSocket newWebSocket(OkHttpClient client, WebSocketImpl socket, Request request) {
    socket.setRaw(client.newWebSocket(request, socket));
    return socket;
  }

}
