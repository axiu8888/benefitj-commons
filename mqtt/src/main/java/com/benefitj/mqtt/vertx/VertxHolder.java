package com.benefitj.mqtt.vertx;

import com.benefitj.core.SingletonSupplier;
import io.vertx.core.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;


public class VertxHolder {

  /**
   * singleton instance
   */
  static final SingletonSupplier<Vertx> single = SingletonSupplier.of(Vertx::vertx);

  /**
   * 获取Vertx实例
   */
  public static Vertx get() {
    return single.get();
  }

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
    return get().deployVerticle(verticle, options);
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
  public static io.vertx.core.Future<Void> undeploy(String deploymentID) {
    return get().undeploy(deploymentID);
  }

}
