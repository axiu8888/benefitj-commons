package com.benefitj.mqtt.vertx;

import io.vertx.core.*;


public class VertxHolder {

  /**
   * 部署
   *
   * @param verticle Verticle对象
   * @return 返回部署结果
   */
  public static Future<String> deploy(Verticle verticle) {
    return deploy(verticle, new DeploymentOptions());
  }

  /**
   * 部署
   *
   * @param verticle Verticle对象
   * @param options  部署参数
   * @return 返回部署结果
   */
  public static Future<String> deploy(Verticle verticle, DeploymentOptions options) {
    return getInstance().deployVerticle(verticle, options);
  }

  /**
   * 取消部署
   *
   * @param verticle Verticle对象
   * @return 返回结果
   */
  public static Future<Void> undeploy(AbstractVerticle verticle) {
    return undeploy(verticle.deploymentID());
  }

  /**
   * 取消部署
   *
   * @param deploymentID verticle ID
   * @return 返回结果
   */
  public static Future<Void> undeploy(String deploymentID) {
    return getInstance().undeploy(deploymentID);
  }

  /**
   * singleton instance
   */
  private static volatile Vertx INSTANCE;

  /**
   * 获取Vertx实例
   */
  public static Vertx getInstance() {
    Vertx vertx = INSTANCE;
    if (vertx == null) {
      synchronized (VertxHolder.class) {
        if ((vertx = INSTANCE) == null) {
          INSTANCE = vertx = Vertx.vertx();
        }
      }
    }
    return vertx;
  }

}
