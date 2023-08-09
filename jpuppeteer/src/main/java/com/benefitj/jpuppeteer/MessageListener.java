package com.benefitj.jpuppeteer;


import com.alibaba.fastjson2.JSONObject;

/**
 * 监听消息
 */
public interface MessageListener {

  /**
   * 消息监听
   *
   * @param method 被触发的方法
   * @param msg    消息
   */
  void onMessage(String method, JSONObject msg);

  /**
   * 匹配后处理消息
   */
  interface MatchMessageListener extends MessageListener {

    /**
     * 处理消息
     *
     * @param method 函数
     * @param msg    消息
     */
    void onHandle(String method, JSONObject msg);

    /**
     * 匹配
     *
     * @param method 函数
     * @param msg    消息
     * @return 返回是否匹配
     */
    boolean match(String method, JSONObject msg);

    @Override
    default void onMessage(String method, JSONObject msg) {
      if (match(method, msg)) {
        onHandle(method, msg);
      }
    }
  }

  /**
   * 匹配函数名
   */
  abstract class MethodMatchMessageListener implements MatchMessageListener {

    String pattern;

    public MethodMatchMessageListener(String pattern) {
      this.pattern = pattern;
    }

    @Override
    public boolean match(String method, JSONObject msg) {
      return method.equals(pattern) || pattern.matches(method);
    }

  }

}

