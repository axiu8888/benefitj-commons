package com.benefitj.vertx.http;

import com.benefitj.vertx.VertxHolder;
import com.benefitj.vertx.VertxVerticle;
import io.vertx.core.http.*;
import io.vertx.core.net.ProxyOptions;
import io.vertx.core.net.ProxyType;

public class HttpProxyVerticle extends VertxVerticle {

  private static final String TARGET_HOST = "192.168.1.204"; // 目标主机
  private static final int TARGET_PORT = 80;                // 目标端口

  protected HttpServerOptions serverOptions;
  protected HttpServer httpServer;
  protected HttpClient httpClient;

  public int port() {
    if (httpServer != null) httpServer.actualPort();
    if (serverOptions != null) serverOptions.getPort();
    return 0;
  }


  @Override
  public void start() throws Exception {
    this.httpClient = getVertx().createHttpClient(new HttpClientOptions()
        .setProxyOptions(new ProxyOptions()
            .setHost("192.168.1.204")
            .setPort(80)
            .setType(ProxyType.HTTP)
            .setUsername("cwbTest")
            .setPassword("hsrg8888")
        )
    );

    this.serverOptions = new HttpServerOptions()
        .setPort(8080)
        .setReusePort(true)
        .setMaxHeaderSize(10 * (1024 << 10))//最大10兆
        .setCompressionLevel(1)
        .setCompressionSupported(true)
        .setReceiveBufferSize(4 * (1024 << 10));
    this.httpServer = VertxHolder.createHttpServer(this.serverOptions)
        .requestHandler(serverRequest -> {
          // 构建目标请求
          this.httpClient.request(
                  serverRequest.method(),
                  TARGET_PORT,
                  TARGET_HOST,
                  serverRequest.uri())
              .andThen(res -> {
                HttpClientRequest clientRequest = res.result();
                // 复制请求头
                clientRequest.headers().setAll(serverRequest.headers());
                // 将请求体数据转发给目标服务器
                serverRequest.handler(data -> clientRequest.write(data));
                serverRequest.endHandler(v -> clientRequest.end());
                clientRequest.exceptionHandler(e -> {
                  e.printStackTrace();
                  serverRequest.response().setStatusCode(500).end("Proxy error: " + e.getMessage());
                });
              })
              .onSuccess(request -> {

                HttpClientResponse clientResponse = request.response().await();
                // 设置响应状态码和头部
                serverRequest.response().setStatusCode(clientResponse.statusCode());
                serverRequest.response().headers().setAll(clientResponse.headers());
                // 将响应数据转发给客户端
                clientResponse.handler(data -> serverRequest.response().write(data));
                clientResponse.endHandler(v -> serverRequest.response().end());
              })
              .onComplete(res -> {
                HttpClientRequest req = res.result();
                if (res.succeeded()) {
                } else {
                  log.warn((req != null ? req.getURI() : "NULL") + ", 请求失败: {}", res.cause());
                }
              });
        });

    this.httpServer
        .listen()
        .onComplete(res -> {
          if (res.succeeded()) {
            log.info("HTTP Proxy server started on port {}", port());
          } else {
            log.info("http proxy port["+ port() +"], Failed to start proxy server: " + res.cause());
          }
        });
  }

  @Override
  public void stop() throws Exception {
    if (httpServer != null) {
      httpServer
          .close()
          .onComplete(res -> {
            if (res.succeeded()) {
              log.info("HTTP Proxy server stop on port {}", port());
            } else {
              log.info("http proxy port["+ port() +"], Failed to stop proxy server: " + res.cause());
            }
          });
    }
    if (httpClient != null) {
      httpClient.close().onComplete(res -> {/*^_^*/});
    }
  }

}
