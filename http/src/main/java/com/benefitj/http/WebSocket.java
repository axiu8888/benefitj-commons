package com.benefitj.http;

import java.util.Map;

/**
 * WEB SOCKET
 */
public interface WebSocket extends okhttp3.WebSocket {

  /**
   * 获取ID
   */
  String getId();

  /**
   * 是否已打开
   */
  boolean isOpen();

  /**
   * 获取URL
   */
  String getUrl();

  /**
   * 关闭
   */
  default void close() {
    close(1000, "");
  }

  /**
   * 属性
   */
  Map<String, Object> attrs();

  /**
   * 获取属性值
   *
   * @param key 键
   * @param <T> 属性类型
   * @return 返回属性对象
   */
  default <T> T getAttr(String key) {
    return (T) attrs().get(key);
  }

  /**
   * 设置属性值
   *
   * @param key   键
   * @param value 值
   * @param <T>   属性类型
   */
  default <T> void setAttr(String key, T value) {
    attrs().put(key, value);
  }

}
