package com.benefit.vertx.tcp;

import com.benefit.vertx.VertxHolder;
import com.benefit.vertx.log.VertxLogger;
import com.benefitj.core.*;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class VertxTcpClient {

  protected final VertxLogger log = VertxLogger.get();

  private final ClientNetListener<VertxTcpClient> proxyListener = ProxyUtils.newCopyListProxy(ClientNetListener.class);

  private NetClient raw;
  private NetClientOptions options;
  private NetSocket socket;
  /**
   * 是否支持自动重连
   */
  private boolean autoConnect = false;
  /**
   * 调度器
   */
  private Executor executor = EventLoop.io();
  /**
   * 远程地址
   */
  private SocketAddress remote;

  public VertxTcpClient(NetClientOptions options) {
    this.raw = VertxHolder.createNetClient(options);
    this.options = options;
  }

  public boolean isAutoConnect() {
    return autoConnect;
  }

  public void setAutoConnect(boolean autoConnect) {
    this.autoConnect = autoConnect;
  }

  public NetClient getRaw() {
    return raw;
  }

  public void setRaw(NetClient raw) {
    this.raw = raw;
  }

  public NetClientOptions getOptions() {
    return options;
  }

  public void setOptions(NetClientOptions options) {
    this.options = options;
  }

  public NetSocket getSocket() {
    return socket;
  }

  public void setSocket(NetSocket socket) {
    this.socket = socket;
  }

  public void addListener(ClientNetListener<VertxTcpClient> l) {
    if (!((List) proxyListener).contains(l)) {
      ((List) proxyListener).add(l);
    }
  }

  public void removeListener(ClientNetListener<VertxTcpClient> l) {
    if (l != null) {
      ((List) proxyListener).remove(l);
    }
  }

  public void connect(String host, int port) {
    connect(SocketAddress.inetSocketAddress(port, host));
  }

  public void connect(SocketAddress remote) {
    if (isActive()) {
      return;
    }
    this.remote = remote;
    this.disconnected.set(false);
    connect0(null);
  }

  void connect0(CountDownLatch latch) {
    if (connecting.compareAndSet(false, true)) {
      this.latch.set(latch);
      getRaw().connect(remote, rawConnectHandler);
    }
  }

  public void disconnect() {
    disconnect(null);
  }

  public void disconnect(Handler<AsyncResult<Void>> handler) {
    disconnected.set(true);
    NetSocket ns = getSocket();
    if (ns != null) {
      if (handler != null)
        ns.close(handler);
      else
        ns.close();
    }
  }

  public boolean isActive() {
    return getSocket() != null;
  }

  public SocketAddress localAddress() {
    NetSocket ns = getSocket();
    return ns != null ? ns.localAddress() : null;
  }

  public SocketAddress remoteAddress() {
    NetSocket ns = getSocket();
    return ns != null ? ns.remoteAddress() : remote;
  }

  private final AtomicBoolean connecting = new AtomicBoolean(false);
  private final AtomicBoolean disconnected = new AtomicBoolean(false);
  private final AtomicReference<CountDownLatch> latch = new AtomicReference<>();
  private final Handler<AsyncResult<NetSocket>> rawConnectHandler = new Handler<AsyncResult<NetSocket>>() {
    @Override
    public void handle(AsyncResult<NetSocket> event) {
      connecting.compareAndSet(true, false);
      final NetSocket ns = event.result();
      try {
        if (event.succeeded()) {
          try {
            rawListener.onSuccess(ns);
          } finally {
            ns
                .handler(rawListener::onMessage)
                .drainHandler(evt -> rawListener.onDrainHandle())
                .closeHandler(evt -> rawListener.onCloseHandle())
                .endHandler(evt -> rawListener.onEndHandle());
          }
        } else {
          rawListener.onFailure(event.cause());
        }
      } finally {
        rawListener.onComplete(ns);
        CountDownLatch cdl;
        if ((cdl = latch.getAndSet(null)) != null)
          cdl.countDown();
      }
    }
  };

  /**
   * 监听
   */
  private final NetListener rawListener = new NetListener() {

    @Override
    public void onMessage(Buffer buf) {
      byte[] bufBytes = buf.getBytes();
      log.trace("remote[{}] rcv <== data[{}]: {}", remoteAddress(), bufBytes.length, HexUtils.bytesToHex(bufBytes));
      proxyListener.onMessage(VertxTcpClient.this, buf);
    }

    @Override
    public void onException(Throwable e) {
      log.trace("remote[{}] throw: {}", remoteAddress(), e.getMessage());
      proxyListener.onException(VertxTcpClient.this, e);
    }

    @Override
    public void onDrainHandle() {
      log.trace("remote[{}] drain", remoteAddress());
      proxyListener.onDrainHandle(VertxTcpClient.this);
    }

    @Override
    public void onCloseHandle() {
      log.trace("remote[{}] close", remoteAddress());
      setSocket(null);
      proxyListener.onCloseHandle(VertxTcpClient.this);
    }

    @Override
    public void onEndHandle() {
      log.trace("remote[{}] end", remoteAddress());
      proxyListener.onEndHandle(VertxTcpClient.this);
    }

    @Override
    public void onSuccess(NetSocket socket) {
      setSocket(socket);
      log.trace("remote[{}] success", socket.remoteAddress());
      proxyListener.onSuccess(VertxTcpClient.this);
    }

    @Override
    public void onFailure(Throwable e) {
      log.trace("failure: {}", e.getMessage());

      if (!disconnected.get() && isAutoConnect()) {
        // 尝试连接
        log.trace("{} 尝试连接: ", remoteAddress());
        executor.execute(() -> {
          // 自动重连
          while (!(isActive() || disconnected.get())) {
            long startAt = TimeUtils.now(), nextAt = 0L;
            CountDownLatch latch = new CountDownLatch(1);
            connect0(latch);
            CatchUtils.ignore(() -> latch.await());
            if ((nextAt = TimeUtils.diffNow(startAt) - getOptions().getReconnectInterval()) > 0) {
              EventLoop.sleepMillis(nextAt);
            }
          }
        });
      }

      proxyListener.onFailure(VertxTcpClient.this, e);
    }

    @Override
    public void onComplete(NetSocket socket) {
      log.trace("remote[{}] complete: ", remoteAddress());
      proxyListener.onComplete(VertxTcpClient.this);
    }
  };
}
