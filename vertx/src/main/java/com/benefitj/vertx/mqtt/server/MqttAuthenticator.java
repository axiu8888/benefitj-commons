package com.benefitj.vertx.mqtt.server;

import io.vertx.mqtt.MqttEndpoint;

/**
 * 认证器
 */
public interface MqttAuthenticator {

  /**
   * 认证
   *
   * @param endpoint 客户端
   * @return 返回是否通过
   */
  boolean authenticate(MqttEndpoint endpoint);

}
