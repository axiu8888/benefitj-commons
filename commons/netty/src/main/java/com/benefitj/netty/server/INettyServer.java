package com.benefitj.netty.server;

import com.benefitj.netty.INetty;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.util.AttributeKey;

import java.util.Map;

/**
 * 服务端
 */
public interface INettyServer<S extends INettyServer<S>> extends INetty<ServerBootstrap, S> {

  /**
   * 设置线程组
   *
   * @param bossGroup   主线程组
   * @param workerGroup 工作线程组
   * @return 返回当前对象
   */
  S group(EventLoopGroup bossGroup, EventLoopGroup workerGroup);

  /**
   * 主线程组
   */
  EventLoopGroup bossGroup();

  /**
   * 工作线程组
   */
  EventLoopGroup workerGroup();

  /**
   * child的Handler的可选项配置
   *
   * @param childOption 可选项配置
   * @param value       值
   * @param <T>         类型
   * @return 返回当前对象
   */
  <T> S childOption(ChannelOption<T> childOption, T value);


  /**
   * child的handler的可选项配置
   *
   * @param ops 配置可选项
   * @return 返回当前对象
   */
  S childOptions(Map<ChannelOption<?>, Object> ops);

  /**
   * child的选项
   */
  Map<ChannelOption<?>, Object> childOptions();

  /**
   * child的Handler属性
   *
   * @param key   属性
   * @param value 值
   * @param <T>   类型
   * @return 返回当前对象
   */
  <T> S childAttr(AttributeKey<T> key, T value);


  /**
   * child的Handler属性
   *
   * @param childAttrs 属性
   * @return 返回当前对象
   */
  S childAttrs(Map<AttributeKey<?>, Object> childAttrs);

  /**
   * child的属性集合
   */
  Map<AttributeKey<?>, Object> childAttrs();

  /**
   * child的Handler
   */
  ChannelHandler childHandler();

  /**
   * 设置child的Handler
   *
   * @param childHandler child Handler
   * @return 返回当前对象
   */
  S childHandler(ChannelHandler childHandler);

  /**
   * 服务端类型
   */
  ServerType serverType();

}
