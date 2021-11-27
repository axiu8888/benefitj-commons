package com.benefitj.network;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

/**
 * OkHttpClient
 */
public enum HttpClientHolder {

  INSTANCE;

  private final OkHttpClient client = new OkHttpClient();

  /**
   * 获取OkHttp客户端
   */
  public static OkHttpClient getOkHttpClient() {
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
   * @param request  URL地址
   * @param listener 监听
   * @return 返回创建的WebSocket对象
   */
  public static WebSocket newWebSocket(Request request, WebSocketListener listener) {
    return getOkHttpClient().newWebSocket(request, listener);
  }

}
