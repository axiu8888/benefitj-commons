package com.benefit.vertx;

import io.vertx.core.Verticle;

public interface VerticleInitializer<T extends Verticle> {

  /**
   * 初始化
   *
   * @param verticle
   */
  void onInitialize(T verticle);

}
