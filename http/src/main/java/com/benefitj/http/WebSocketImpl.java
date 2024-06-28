package com.benefitj.http;

import com.benefitj.core.AutoConnectTimer;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.IdUtils;
import okhttp3.Request;
import okhttp3.Response;
import okio.ByteString;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.net.SocketException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * 连接的客户端
 */
public class WebSocketImpl extends okhttp3.WebSocketListener implements WebSocket, AutoConnectTimer.Connector {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final String id = IdUtils.uuid();

  private final Map<String, Object> attrs = new ConcurrentHashMap<>();

  private okhttp3.WebSocket raw;
  private WebSocketListener listener;

  private volatile boolean opened = false;
  private volatile boolean closed = false;
  /**
   * 自动重连
   */
  private final AutoConnectTimer autoConnectTimer = new AutoConnectTimer(false, Duration.ofSeconds(10));

  public WebSocketImpl() {
  }

  public WebSocketImpl(WebSocketListener listener) {
    this.listener = listener;
  }

  public okhttp3.WebSocket getRaw() {
    return raw;
  }

  public void setRaw(okhttp3.WebSocket raw) {
    this.raw = raw;
  }

  public WebSocketListener getListener() {
    return listener;
  }

  public void setListener(WebSocketListener listener) {
    this.listener = listener;
  }

  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public WebSocketImpl getAutoConnectTimer(Consumer<AutoConnectTimer> consumer) {
    consumer.accept(getAutoConnectTimer());
    return this;
  }

  @Override
  public boolean isConnected() {
    return isOpen();
  }

  @Override
  public void doConnect() {
    connect(getUrl());
  }

  /**
   * 重连
   */
  void connect0(Request request) {
    HttpClient.get().newWebSocket(request, this);
  }

  /**
   * 重连
   */
  public void connect(String url) {
    if (isOpen()) throw new IllegalStateException("客户端已连接!");
    connect0(new Request.Builder()
        .url(url)
        .get()
        .build());
  }

  /**
   * 重连
   */
  public void reconnect() {
    okhttp3.WebSocket raw = getRaw();
    if (raw == null) throw new IllegalStateException("还未连接过！");
    if (isOpen()) return;
    this.closed = false;
    connect0(raw.request()
        .newBuilder()
        .build());
  }

  @Override
  public void onOpen(@NotNull okhttp3.WebSocket ws, @NotNull Response response) {
    log.trace("[{}] onOpen, url: {}, response.code: {}, response.message: {}", getId(), obtainUrl(ws), response.code(), response.message());
    this.opened = true;
    this.closed = false;
    this.raw = ws;
    getListener().onOpen(this, response);
  }

  @Override
  public void onMessage(@NotNull okhttp3.WebSocket webSocket, @NotNull String text) {
    log.trace("[{}] onMessage, url: {}, text[{}]: {}", getId(), getUrl(), text.length(), text);
    getListener().onMessage(this, text);
  }

  @Override
  public void onMessage(@NotNull okhttp3.WebSocket ws, @NotNull ByteString bytes) {
    log.trace("[{}] onMessage, url: {}, bytes[{}]: {}", getId(), obtainUrl(ws), bytes.size(), bytes.base64());
    getListener().onMessage(this, bytes);
  }

  @Override
  public void onFailure(@NotNull okhttp3.WebSocket ws, @NotNull Throwable error, @Nullable Response response) {
    log.trace("[{}] onFailure, url: {}, response.code: {}, response.message: {}"
        , getId()
        , obtainUrl(ws)
        , response != null ? response.code() : -1
        , response != null ? response.message() : ""
    );
    try {
      getListener().onFailure(this, error, response);
    } finally {
      if (error instanceof EOFException || error instanceof SocketException) {
        this.opened = false;
        try {
          getListener().onClosed(this, 1, error.getMessage());
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        }
      }
    }
    if (!isClosed()) {
      autoConnectTimer.start(this); // 开始自动重连(如果需要自动重连)
    }
  }

  @Override
  public void onClosing(@NotNull okhttp3.WebSocket ws, int code, @NotNull String reason) {
//    log.trace("[{}] onClosing, url: {}, code: {}, reason: {}", getId(), getUrl(), code, reason);
//    this.open = false;
    getListener().onClosed(this, code, reason);
  }

  @Override
  public void onClosed(@NotNull okhttp3.WebSocket ws, int code, @NotNull String reason) {
    log.trace("[{}] onClosed, url: {}, code: {}, reason: {}", getId(), obtainUrl(ws), code, reason);
    try {
      boolean _open = this.opened;
      this.opened = false;
      if (_open) {
        getListener().onClosed(this, code, reason);
      }
    } finally {
      if (!isClosed()) {
        this.autoConnectTimer.start(this); // 开始自动重连(如果需要自动重连)
      }
    }
  }

  /**
   * 获取ID
   */
  @Override
  public String getId() {
    return id;
  }

  /**
   * 是否已打开
   */
  @Override
  public boolean isOpen() {
    return opened;
  }

  @Override
  public boolean isClosed() {
    return closed;
  }

  @Override
  public String getUrl() {
    okhttp3.WebSocket raw = getRaw();
    return raw != null ? raw.request().url().toString() : null;
  }

  @Override
  public Map<String, Object> attrs() {
    return attrs;
  }

  @Override
  public void cancel() {
    //getRaw().cancel();
    //throw new UnsupportedOperationException("不支持此功能!");
    close();
  }

  @Override
  public boolean close(int code, @Nullable String reason) {
    try {
      if (!isOpen()) CatchUtils.ignore(() -> getRaw().cancel());// 取消
      if (!isOpen() && isClosed()) return true;
      this.closed = true;
      return getRaw().close(code, reason);
    } finally {
      this.autoConnectTimer.stop();
    }
  }

  @Override
  public long queueSize() {
    return getRaw().queueSize();
  }

  @Override
  public Request request() {
    return getRaw().request();
  }

  @Override
  public boolean send(@NotNull String text) {
    return getRaw().send(text);
  }

  @Override
  public boolean send(@NotNull ByteString bytes) {
    return getRaw().send(bytes);
  }

  static String obtainUrl(okhttp3.WebSocket ws) {
    return ws != null ? ws.request().url().toString() : null;
  }

}
