package com.benefitj.http;

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

}
