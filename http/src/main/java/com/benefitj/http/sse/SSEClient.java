package com.benefitj.http.sse;

import com.benefitj.core.AutoConnectTimer;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.BufferedSource;
import okio.Okio;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * SSE 客户端
 */
public interface SSEClient {

  /**
   * SSE地址
   */
  String getSseUrl();

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

  static Impl newClient(String sseUrl) {
    return newClient(sseUrl, null);
  }

  static Impl newClient(String sseUrl, SSEEventListener eventListener) {
    return new Impl(sseUrl, eventListener, Impl.DEFAULT_HTTP_CLIENT);
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

    private final String sseUrl;
    private SSEEventListener eventListener;
    private OkHttpClient httpClient;

    public Impl(String sseUrl) {
      this(sseUrl, DEFAULT_HTTP_CLIENT);
    }

    public Impl(String sseUrl, OkHttpClient httpClient) {
      this(sseUrl, null, httpClient);
    }

    public Impl(String sseUrl, SSEEventListener eventListener, OkHttpClient httpClient) {
      this.sseUrl = sseUrl;
      this.setEventListener(eventListener);
      this.setHttpClient(httpClient);
    }

    @Override
    public String getSseUrl() {
      return sseUrl;
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
    public boolean isConnected() {
      Call call = callRef.get();
      return call != null && !call.isCanceled();
    }

    @Override
    public void doConnect() {
      synchronized (this) {
        if (callRef.get() == null) {
          callRef.set(newCall(getHttpClient(), getSseUrl(), _cb_));
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
      if (isConnected()) {
        synchronized (this) {
          Call call = this.callRef.getAndSet(null);
          if (call != null) {
            call.cancel();
          }
        }
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
        try {
          getEventListener().onClosed(client);
        } catch (Throwable e) {
          log.error("onClosed", e);
        }
        autoConnectTimer.start(self());// 自动重连
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
        el.onOpen(self());
        try (final BufferedSource source = Okio.buffer(response.body().source())) {
          StringBuilder buf = new StringBuilder();
          String line;
          while ((line = source.readUtf8Line()) != null) {
            if (line.isEmpty()) {
              // 空行表示一个事件结束
              if (buf.length() > 0) {
                el.onEvent(self(), parseEvent(buf.toString()));
                buf.setLength(0); // 清空当前事件
              }
            } else {
              buf.append(line).append("\n");
            }
          }
        } catch (Exception e) {
          el.onFailure(self(), e);
        } finally {
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
    Call call = client.newCall(new Request.Builder()
        .url(url)
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
        } catch (NumberFormatException ignored) {
        }
      }
      // 忽略其他字段和注释
    }
    return event;
  }

}