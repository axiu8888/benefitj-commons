package com.benefitj.http.sse;

import com.benefitj.core.*;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.net.SocketException;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SSE 客户端
 */
public interface SSEClient {

  /**
   * 属性值
   */
  AttributeMap attrs();

  /**
   * SSE地址
   */
  default String getSseUrl() {
    return getRequest().url().toString();
  }

  /**
   * SSE 请求
   */
  Request getRequest();

  /**
   * 获取监听
   */
  SSEEventListener getEventListener();

  /**
   * 设置监听
   *
   * @param eventListener 监听
   */
  void setEventListener(SSEEventListener eventListener);

  /**
   * 是否已连接
   */
  boolean isConnected();

  /**
   * 连接
   */
  void connect();

  /**
   * 断开
   */
  void disconnect();

  /**
   * 是否自动重连
   */
  boolean isAutoReconnect();

  /**
   * 设置是否自动重连
   *
   * @param auto
   */
  void setAutoReconnect(boolean auto);

  /**
   * 自动重连间隔
   */
  Duration getAutoReconnectInterval();

  /**
   * 设置自动重连间隔
   *
   * @param interval 间隔
   */
  void setAutoReconnectInterval(Duration interval);

  /**
   * 保活超时时长
   */
  Duration getKeepAliveTimeout();

  /**
   * 设置保活超时时长
   *
   * @param timeout 超时时长
   */
  void setKeepAliveTimeout(Duration timeout);

  /**
   * 关闭SSE
   */
  static void close(SSEClient sse) {
    if (sse != null)
      sse.disconnect();
  }

  static Impl newClient(String sseUrl, SSEEventListener eventListener) {
    return newClient(new Request.Builder().url(sseUrl).build(), eventListener);
  }

  static Impl newClient(Request request, SSEEventListener eventListener) {
    return new Impl(request, eventListener, Impl.DEFAULT_HTTP_CLIENT);
  }

  static Impl start(String sseUrl, boolean autoConnect, Duration autoConnectInterval, SSEEventListener eventListener) {
    Request request = new Request.Builder().url(sseUrl).build();
    return start(request, autoConnect, autoConnectInterval, eventListener);
  }

  static Impl start(Request request, boolean autoConnect, Duration autoConnectInterval, SSEEventListener eventListener) {
    Impl impl = newClient(request, eventListener);
    impl.setAutoReconnect(autoConnect);
    impl.setAutoReconnectInterval(autoConnectInterval);
    impl.connect();
    return impl;
  }

  @Slf4j
  class Impl implements SSEClient, AutoConnectTimer.Connector {

    /**
     * 默认的客户端
     */
    public static final OkHttpClient DEFAULT_HTTP_CLIENT = new OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS) // 设置为0表示无限等待，适合SSE长连接
        .writeTimeout(120, TimeUnit.SECONDS)
        .build();

    private final AttributeMap attrs = AttributeMap.wrap(new ConcurrentHashMap<>());

    private Request request;
    private SSEEventListener eventListener;
    private OkHttpClient httpClient = DEFAULT_HTTP_CLIENT;
    /**
     * 保活超时时长
     */
    private Duration keepAliveTimeout = Duration.ofSeconds(120);

    public Impl(Request request) {
      this(request, DEFAULT_HTTP_CLIENT);
    }

    public Impl(Request request, OkHttpClient httpClient) {
      this(request, null, httpClient);
    }

    public Impl(Request request, SSEEventListener eventListener, OkHttpClient httpClient) {
      this.request = request;
      this.setEventListener(eventListener);
      this.setHttpClient(httpClient);
    }

    @Override
    public AttributeMap attrs() {
      return attrs;
    }

    public void setRequest(Request request) {
      this.request = request;
    }

    @Override
    public Request getRequest() {
      return request;
    }

    @Override
    public SSEEventListener getEventListener() {
      return eventListener;
    }

    @Override
    public void setEventListener(SSEEventListener eventListener) {
      this.eventListener = eventListener;
    }

    public OkHttpClient getHttpClient() {
      return httpClient;
    }

    public void setHttpClient(OkHttpClient httpClient) {
      this.httpClient = httpClient;
    }

    @Override
    public boolean isAutoReconnect() {
      return autoConnectTimer.isAutoConnect();
    }

    @Override
    public void setAutoReconnect(boolean auto) {
      this.autoConnectTimer.setAutoConnect(auto);
    }

    @Override
    public Duration getAutoReconnectInterval() {
      return this.autoConnectTimer.getInterval();
    }

    @Override
    public void setAutoReconnectInterval(Duration interval) {
      this.autoConnectTimer.setInterval(interval);
    }

    @Override
    public Duration getKeepAliveTimeout() {
      return keepAliveTimeout;
    }

    @Override
    public void setKeepAliveTimeout(Duration keepAliveTimeout) {
      this.keepAliveTimeout = keepAliveTimeout;
    }

    @Override
    public boolean isConnected() {
      Call call = callRef.get();
      return call != null && !call.isCanceled();
    }

    @Override
    public void doConnect() {
      synchronized (this) {
        if (callRef.get() == null) {
          _el_.onReconnectBefore(this);//重连之前
          callRef.set(newCall(getHttpClient(), getRequest(), _cb_));
        }
      }
    }

    /**
     * 连接
     */
    public void connect() {
      if (isConnected())
        return;
      doConnect();
    }

    /**
     * 断开
     */
    public void disconnect() {
      if (!closed.get()) {
        synchronized (this) {
          closed.set(true);
          Call call = this.callRef.getAndSet(null);
          if (call != null) {
            call.cancel();
          }
          closeResponse();
        }
      }
    }

    private void closeResponse() {
      Response prevResponse = responseRef.getAndSet(null);
      if (prevResponse != null) {
        prevResponse.close();
      }
    }

    protected Impl self() {
      return this;
    }

    /**
     * 自动重连
     */
    private final AutoConnectTimer autoConnectTimer = new AutoConnectTimer(false, Duration.ofSeconds(30));
    private final AtomicReference<Call> callRef = new AtomicReference<>();
    private final AtomicReference<Response> responseRef = new AtomicReference<>();
    private final AtomicLong activeAt = new AtomicLong();
    private final AtomicBoolean closed = new AtomicBoolean(false);
    private final SSEEventListener _el_ = new SSEEventListener() {

      boolean opened = false;

      @Override
      public void onOpen(SSEClient client) {
        opened = true;
        autoConnectTimer.stop();
        try {
          getEventListener().onOpen(client);
        } catch (Throwable e) {
          log.error("onOpen", e);
        }
      }

      @Override
      public void onEvent(SSEClient client, SSEEvent event) {
        try {
          getEventListener().onEvent(client, event);
        } catch (Throwable e) {
          log.error("onEvent", e);
        }
      }

      @Override
      public void onFailure(SSEClient client, Throwable error) {
        try {
          getEventListener().onFailure(client, error);
        } catch (Throwable e) {
          log.error("onFailure", e);
        }

        if (!opened) {
          onClosed(self());
        }
      }

      @Override
      public void onClosed(SSEClient client) {
        opened = false;
        callRef.set(null);
        closeResponse();
        try {
          getEventListener().onClosed(client);
        } catch (Throwable e) {
          log.error("onClosed error -->>: " + e.getMessage(), e);
        }
        if (!closed.get()) {
          autoConnectTimer.start(self());// 自动重连
        }
      }

      @Override
      public void onReconnectBefore(SSEClient client) {
        try {
          getEventListener().onReconnectBefore(client);
        } catch (Throwable e) {
          log.error("onReconnectBefore error -->>: " + e.getMessage(), e);
        }
      }
    };

    private final Callback _cb_ = new Callback() {

      @Override
      public void onFailure(Call call, IOException e) {
        _el_.onFailure(self(), e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        SSEEventListener el = _el_;
        if (!response.isSuccessful()) {
          el.onFailure(self(), new IOException("Unexpected code " + response));
          return;
        }
        closed.set(false);//未关闭
        responseRef.set(response);

        // 保活检查
        final AtomicReference<ScheduledFuture<?>> keepAliveTask = new AtomicReference<>(EventLoop.asyncIOFixedRate(() -> {
          if (TimeUtils.diffNow(activeAt.get()) < getKeepAliveTimeout().toMillis()) return;
          Call remove = callRef.getAndSet(null);
          if (remove != null) {
            remove.cancel();
            el.onClosed(self());
          }
        }, 5, 5, TimeUnit.SECONDS));

        el.onOpen(self());
        try (final BufferedSource source = Okio.buffer(response.body().source())) {
          StringBuilder buf = new StringBuilder();
          String line;
          while (!source.exhausted()) {
            if ((line = source.readUtf8Line()) == null) continue;
            if (line.isEmpty()) {
              // 空行表示一个事件结束
              if (buf.length() > 0) {
                try {
                  SSEEvent event = parseEvent(buf.toString());
                  if (event.isKeepAlive()) {
                    el.onKeepAlive(self(), event);
                  } else {
                    el.onEvent(self(), event);
                  }
                } catch (Exception e) {
                  //el.onFailure(self(), e);
                  log.error("throws: " + e.getMessage(), e);
                }
                buf.setLength(0); // 清空当前事件
              }
            } else {
              buf.append(line).append("\n");
            }
            activeAt.set(System.currentTimeMillis());
          }
        } catch (Exception e) {
          if (!closed.get() && !(e instanceof SocketException)) {//不是主动关闭
            el.onFailure(self(), e);
          }
        } finally {
          IOUtils.closeQuietly(response, response.body());
          EventLoop.cancel(keepAliveTask.getAndSet(null));
          el.onClosed(self());
        }
      }
    };

  }

  /**
   * 调用
   *
   * @param client   客户端
   * @param url      URL地址
   * @param callback 回调
   * @return 返回 call
   */
  static Call newCall(OkHttpClient client, String url, Callback callback) {
    return newCall(client, new Request.Builder().url(url).get().build(), callback);
  }

  /**
   * 调用
   *
   * @param client   客户端
   * @param request  请求
   * @param callback 回调
   * @return 返回 call
   */
  static Call newCall(OkHttpClient client, Request request, Callback callback) {
    Call call = client.newCall(request.newBuilder()
        .header("Accept", "text/event-stream") // 重要：告诉服务器我们需要事件流
        .build());
    call.enqueue(callback);
    return call;
  }

  /**
   * 解析事件
   *
   * @param rawEvent 时间对象
   * @return 返回解析结果
   */
  static SSEEvent parseEvent(String rawEvent) {
    SSEEvent event = new SSEEvent();
    String[] lines = rawEvent.split("\n");
    for (String line : lines) {
      if (line.startsWith("event:")) {
        event.setEvent(line.substring(6).trim());
      } else if (line.startsWith("data:")) {
        event.setData(line.substring(5).trim());
      } else if (line.startsWith("id:")) {
        event.setId(line.substring(3).trim());
      } else if (line.startsWith("retry:")) {
        try {
          event.setRetry(Long.parseLong(line.substring(6).trim()));
        } catch (NumberFormatException ignored) {/*^_^*/}
      } else if (line.startsWith(":keep-alive")) {
        event.setKeepAlive(true);
      } else {
        event.setOther(line.trim());
      }
      // 忽略其他字段和注释
    }
    return event;
  }

}