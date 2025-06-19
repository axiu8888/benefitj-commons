package com.benefitj.vertx.tcp.client;

import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import io.vertx.core.net.SocketAddress;

public interface VerxtClient {

  NetSocket getSocket();

  /**
   * 是否连接
   */
  default boolean isActive() {
    return getSocket() != null;
  }

  /**
   * 本地地址
   */
  default SocketAddress localAddress() {
    NetSocket sock = getSocket();
    return sock != null ? sock.localAddress() : null;
  }

  /**
   * 远程地址
   */
  default SocketAddress remoteAddress() {
    NetSocket sock = getSocket();
    return sock != null ? sock.remoteAddress() : null;
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回Future
   */
  default Future<Void> write(String msg) {
    return write(Buffer.buffer(msg));
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回Future
   */
  default Future<Void> write(byte[] msg) {
    return write(Buffer.buffer(msg));
  }

  /**
   * 发送消息
   *
   * @param msg 消息
   * @return 返回Future
   */
  default Future<Void> write(Buffer msg) {
    NetSocket sock = getSocket();
    if (sock != null) {
      return sock.write(msg);
    }
    return Future.failedFuture(new IllegalStateException("[tcp client] 未连接!"));
  }

}
