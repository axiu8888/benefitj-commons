package com.benefitj.netty;

import com.benefitj.netty.client.TcpNettyClient;
import com.benefitj.netty.client.UdpNettyClient;
import com.benefitj.netty.server.TcpNettyServer;
import com.benefitj.netty.server.UdpNettyServer;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * Netty 客户端/服务端实例
 */
public class NettyFactory {

  /**
   * 创建TCP的服务端
   */
  public static TcpNettyServer newTcpServer() {
    return new TcpNettyServer();
  }

  /**
   * 创建TCP的服务端
   *
   * @param port     本地监听端口
   * @param handlers Handler
   * @return 返回创建的TCP服务端
   */
  public static TcpNettyServer newTcpServer(int port, ChannelHandler... handlers) {
    return newTcpServer(port, Arrays.asList(handlers));
  }

  /**
   * 创建TCP的服务端
   *
   * @param port         本地监听端口
   * @param childHandler Handler
   * @return 返回创建的TCP服务端
   */
  public static TcpNettyServer newTcpServer(int port, final Consumer<Channel> childHandler) {
    return new TcpNettyServer()
        .localAddress(port)
        .childHandler(childHandler);
  }

  /**
   * 创建TCP的服务端
   *
   * @param port     本地监听端口
   * @param handlers Handler
   * @return 返回创建的TCP服务端
   */
  public static TcpNettyServer newTcpServer(int port, final List<ChannelHandler> handlers) {
    return new TcpNettyServer()
        .localAddress(port)
        .childHandler(new ChannelInitializer<SocketChannel>() {
          @Override
          protected void initChannel(SocketChannel ch) throws Exception {
            handlers.forEach(ch.pipeline()::addLast);
          }
        });
  }

  /**
   * 创建UDP的服务端
   */
  public static UdpNettyServer newUdpServer() {
    return new UdpNettyServer();
  }


  /**
   * 创建TCP的客户端，建议使用 vertx 的TCP
   */
  @Deprecated
  public static TcpNettyClient newTcpClient() {
    return new TcpNettyClient();
  }

  /**
   * 创建UDP的客户端
   */
  public static UdpNettyClient newUdpClient() {
    return new UdpNettyClient();
  }

}
