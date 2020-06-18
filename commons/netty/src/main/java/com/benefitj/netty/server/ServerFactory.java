package com.benefitj.netty.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

import java.util.Arrays;
import java.util.List;

/**
 * Netty的服务端工厂
 */
public class ServerFactory {

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
            final ChannelPipeline pipeline = ch.pipeline();
            handlers.forEach(pipeline::addLast);
          }
        });
  }

  /**
   * 创建UDP的服务端
   */
  public static UdpNettyServer newUdpServer() {
    return new UdpNettyServer();
  }
}
