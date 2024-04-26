package com.benefit.vertx.tcp;

import com.benefit.vertx.AutoConnectTimer;
import com.benefit.vertx.IConnector;
import com.benefit.vertx.VertxHolder;
import com.benefit.vertx.log.VertxLogger;
import com.benefitj.core.CatchUtils;
import com.benefitj.core.HexUtils;
import com.benefitj.core.ProxyUtils;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * TCP 客户端
 */
public class VertxTcpClient {

  protected final VertxLogger log = VertxLogger.get();
  /**
   * 代理监听
   */
  private final Listener<VertxTcpClient> proxyListener = ProxyUtils.newCopyListProxy(Listener.class);
  /**
   * 连接器
   */
  private final IConnector connector = new IConnector() {
    @Override
    public boolean isConnected() {
      return _self_().isActive();
    }

    @Override
    public void doConnect() {
      CountDownLatch latch = new CountDownLatch(1);
      _self_().connect0(latch);
      CatchUtils.ignore(() -> latch.await());
    }
  };

  private NetClient raw;
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
  private SocketAddress remote;

  public VertxTcpClient(NetClientOptions options) {
    this.raw = VertxHolder.createNetClient(options);
    this.options = options;
  }

  public VertxTcpClient _self_() {
    return this;
  }

  public NetClient getRaw() {
    return raw;
  }

  public VertxTcpClient setRaw(NetClient raw) {
    this.raw = raw;
    return _self_();
  }

  /**
   * 获取 Socket
   */
  public NetSocket getSocket() {
    return socket;
  }

  /**
   * 设置 Socket
   *
   * @return 返回 VertxTcpClient
   */
  protected VertxTcpClient setSocket(NetSocket socket) {
    this.socket = socket;
    return _self_();
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
    return _self_();
  }


  public AutoConnectTimer getAutoConnectTimer() {
    return autoConnectTimer;
  }

  public VertxTcpClient setAutoConnectTimer(AutoConnectTimer timer) {
    this.autoConnectTimer = timer != null ? timer : AutoConnectTimer.NONE;
    return _self_();
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
    return _self_();
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
    return _self_();
  }

  /**
   * 连接
   *
   * @param host 主机
   * @param port 端口
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient connect(String host, int port) {
    return connect(SocketAddress.inetSocketAddress(port, host));
  }

  /**
   * 连接
   *
   * @param remote 远程地址
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient connect(SocketAddress remote) {
    if (isActive()) return _self_();
    this.remote = remote;
    this.disconnected.set(false);
    connect0(null);
    return _self_();
  }

  void connect0(CountDownLatch latch) {
    if (connecting.compareAndSet(false, true)) {
      this.latch.set(latch);
      if (remote == null)
        throw new IllegalStateException("未设置远程连接的地址!");
      getRaw().connect(remote, rawConnectHandler);
    } else {
      if(latch != null)
        latch.countDown();
    }
  }

  /**
   * 断开
   *
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient disconnect() {
    return disconnect(null);
  }

  /**
   * 断开
   *
   * @param handler 结果处理
   * @return 返回 VertxTcpClient
   */
  public VertxTcpClient disconnect(Handler<AsyncResult<Void>> handler) {
    disconnected.set(true);
    NetSocket ns = getSocket();
    if (ns != null) {
      if (handler != null)
        ns.close(handler);
      else
        ns.close();
    }
    return _self_();
  }

  /**
   * 是否连接
   */
  public boolean isActive() {
    return getSocket() != null;
  }

  /**
   * 本地地址
   */
  public SocketAddress localAddress() {
    NetSocket ns = getSocket();
    return ns != null ? ns.localAddress() : null;
  }

  /**
   * 远程地址
   */
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
      try {
        setSocket(null);
        proxyListener.onCloseHandle(VertxTcpClient.this);
      } finally {
        getAutoConnectTimer().start(connector);
      }
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
      try {
        proxyListener.onFailure(VertxTcpClient.this, e);
      } finally {
        if (!disconnected.get())
          getAutoConnectTimer().start(connector);
      }
    }

    @Override
    public void onComplete(NetSocket socket) {
      log.trace("remote[{}] complete: ", remoteAddress());
      proxyListener.onComplete(VertxTcpClient.this);
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
