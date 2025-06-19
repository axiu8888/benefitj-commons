package com.benefitj.vertx.tcp.client;

import com.benefitj.core.*;
import com.benefitj.vertx.VertxHolder;
import com.benefitj.vertx.VertxVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * TCP 客户端
 */
public class VertxTcpClient extends VertxVerticle<VertxTcpClient> implements VerxtClient {

  /**
   * 代理监听
   */
  private final Listener<VertxTcpClient> proxyListener = ProxyUtils.newCopyListProxy(Listener.class);


  private NetClient original;
  private NetSocket socket;
  /**
   * 配置
   */
  private NetClientOptions options;
  /**
   * 重新连接的定时器
   */
  private AutoConnectTimer autoConnectTimer = AutoConnectTimer.NONE;
  /**
   * 远程地址
   */
  private SocketAddress remoteAddress;

  public VertxTcpClient(NetClientOptions options) {
    this.options = options;
  }

  @Override
  public void start() throws Exception {
    if (getOriginal() != null) return;//ignore

    this.setOriginal(VertxHolder.createNetClient(getOptions()));
    connect0(null);
  }

  @Override
  public void stop() throws Exception {
    NetClient original = getOriginal();
    if (original != null) {
      setOriginal(null);
      original.close().onComplete(res -> {
        if (res.succeeded()) {
          log.trace("[{}] tcp client stop success", remoteAddress());
        } else {
          log.warn("[{}] tcp client stop fail: {}", remoteAddress(), res.cause().getMessage());
        }
      });
    }
  }

  public NetClient getOriginal() {
    return original;
  }

  protected VertxTcpClient setOriginal(NetClient original) {
    this.original = original;
    return this;
  }

  /**
   * 获取 Socket
   */
  public NetSocket getSocket() {
    return socket;
  }

  /**
   * 获取配置
   */
  public NetClientOptions getOptions() {
    return options;
  }

  /**
   * 设置配置
   *
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient setOptions(NetClientOptions options) {
    this.options = options;
    return this;
  }

  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public VertxTcpClient setAutoConnectTimer(AutoConnectTimer timer) {
    this.autoConnectTimer = timer != null ? timer : AutoConnectTimer.NONE;
    return this;
  }

  @Override
  public SocketAddress remoteAddress() {
    if (isActive()) return VerxtClient.super.remoteAddress();
    return remoteAddress;
  }

  /**
   * 添加监听
   *
   * @param l 监听
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient addListener(Listener<VertxTcpClient> l) {
    if (!((List) proxyListener).contains(l)) {
      ((List) proxyListener).add(l);
    }
    return this;
  }

  /**
   * 移除监听
   *
   * @param l 监听
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient removeListener(Listener<VertxTcpClient> l) {
    if (l != null) {
      ((List) proxyListener).remove(l);
    }
    return this;
  }

  /**
   * 连接
   *
   * @param host 主机
   * @param port 端口
   * @return 返回 VertxTcpClient
   */
  public Future<String> connect(String host, int port) {
    return connect(SocketAddress.inetSocketAddress(port, host));
  }

  /**
   * 连接
   *
   * @param remote 远程地址
   * @return 返回 VertxTcpClient
   */
  public Future<String> connect(SocketAddress remote) {
    if (isActive() && connecting.get()) return Future.succeededFuture();//已连接或正在尝试连接中，直接返回
    this.remoteAddress = remote;
    this.disconnected.set(false);
    return VertxHolder.deploy(this);
  }

  CountDownLatch2 connect0(CountDownLatch2 latch) {
    if (connecting.compareAndSet(false, true)) {
      SocketAddress remote = remoteAddress();
      if (remote == null) throw new IllegalStateException("[tcp client] 未设置远程连接的地址!");
      this.latch.set(latch);
      getOriginal().connect(remote).onComplete(intervalConnectHandler);
    } else {
      if (latch != null) async(latch::countDown);
    }
    return latch;
  }

  /**
   * 断开
   */
  public Future<Void> disconnect() {
    try {
      NetSocket sock = getSocket();
      return sock != null ? sock.close() : Future.succeededFuture();
    } finally {
      disconnected.set(true);
    }
  }

  /**
   * 连接器
   */
  private final AutoConnectTimer.Connector connector = new AutoConnectTimer.Connector() {
    @Override
    public boolean isConnected() {
      return self.isActive();
    }

    @Override
    public void doConnect() {
      log.info("自动连接中: {}", DateFmtter.fmtNowS());
      self.connect0(CountDownLatch2.ignore(1)).await();
    }
  };
  private final AtomicBoolean connecting = new AtomicBoolean(false);
  private final AtomicBoolean disconnected = new AtomicBoolean(false);
  private final AtomicReference<CountDownLatch2> latch = new AtomicReference<>();
  private final Handler<AsyncResult<NetSocket>> intervalConnectHandler = new Handler<AsyncResult<NetSocket>>() {
    @Override
    public void handle(AsyncResult<NetSocket> res) {
      connecting.compareAndSet(true, false);
      final NetSocket sock = res.result();
      try {
        if (res.succeeded()) {
          try {
            nl0.onSuccess(self, sock);
          } finally {
            sock
                .handler(event -> nl0.onMessage(self, event))
                .drainHandler(evt -> nl0.onDrainHandle(self))
                .closeHandler(evt -> nl0.onCloseHandle(self))
                .endHandler(evt -> nl0.onEndHandle(self));
          }
        } else {
          nl0.onFailure(self, res.cause());
        }
      } finally {
        nl0.onComplete(self, sock);
        CountDownLatch2 cdl;
        if ((cdl = latch.getAndSet(null)) != null)
          cdl.countDown();
      }
    }
  };

  /**
   * 监听
   */
  private final NetListener nl0 = new NetListener() {

    @Override
    public void onMessage(VertxTcpClient client, Buffer buf) {
      byte[] bufBytes = buf.getBytes();
      log.trace("[tcp client] remote[{}] rcv <== data[{}]: {}", remoteAddress(), bufBytes.length, HexUtils.bytesToHex(bufBytes));
      proxyListener.onMessage(self, buf);
    }

    @Override
    public void onException(VertxTcpClient client, Throwable e) {
      log.trace("[tcp client] remote[{}] onException: {}", remoteAddress(), e.getMessage());
      proxyListener.onException(self, e);
    }

    @Override
    public void onDrainHandle(VertxTcpClient client) {
      log.trace("[tcp client] remote[{}] drain", remoteAddress());
      proxyListener.onDrainHandle(self);
    }

    @Override
    public void onCloseHandle(VertxTcpClient client) {
      log.trace("[tcp client] remote[{}] onCloseHandle", remoteAddress());
      try {
        self.socket = null;
        proxyListener.onCloseHandle(self);
      } finally {
        getAutoConnectTimer().start(connector);
      }
    }

    @Override
    public void onEndHandle(VertxTcpClient client) {
      log.trace("[tcp client] remote[{}] onEndHandle", remoteAddress());
      proxyListener.onEndHandle(self);
    }

    @Override
    public void onSuccess(VertxTcpClient client, NetSocket socket) {
      self.socket = socket;
      log.trace("[tcp client] remote[{}] onSuccess", remoteAddress());
      proxyListener.onSuccess(self);
    }

    @Override
    public void onFailure(VertxTcpClient client, Throwable e) {
      log.trace("[tcp client] onFailure: {}", e.getMessage());
      try {
        proxyListener.onFailure(self, e);
      } finally {
        if (!disconnected.get()) {
          getAutoConnectTimer().start(connector);
        }
      }
    }

    @Override
    public void onComplete(VertxTcpClient client, NetSocket socket) {
      log.trace("[tcp client] remote[{}] onComplete: ", remoteAddress());
      proxyListener.onComplete(self);
    }
  };

  /**
   * 监听
   *
   * @param <T>
   */
  public interface Listener<T> {

    // *********************************************************************************** //

    /**
     * 处理数据
     */
    void onMessage(T socket, Buffer buf);

    /**
     * 出现异常
     *
     * @param e 异常
     */
    default void onException(T socket, Throwable e) {
      e.printStackTrace();
    }

    /**
     *
     */
    default void onDrainHandle(T socket) {
    }

    /**
     * socket被关闭
     */
    default void onCloseHandle(T socket) {
    }

    /**
     * 结束
     */
    default void onEndHandle(T socket) {
    }

    // *********************************************************************************** //

    /**
     * 连接成功
     *
     * @param socket SOCKET
     */
    default void onSuccess(T socket) {
    }

    default void onFailure(T socket, Throwable e) {
      e.printStackTrace();
    }

    default void onComplete(T socket) {
    }

    // *********************************************************************************** //

  }

}
