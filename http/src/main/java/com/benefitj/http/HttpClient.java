package com.benefitj.http;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * OkHttpClient
 */
public enum HttpClient {

  INSTANCE;

  private final OkHttpClient client = new OkHttpClient();

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
    socket.setRaw(get().newWebSocket(request, socket));
    return socket;
  }

}
