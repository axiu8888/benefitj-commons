package com.benefitj.vertx;

import com.benefitj.core.AttributeMap;
import com.benefitj.core.EventLoop;
import com.benefitj.core.functions.IConsumer;
import com.benefitj.core.log.ILogger;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * 启动器
 */
public abstract class VertxVerticle<Self extends VertxVerticle<Self>> extends AbstractVerticle
    implements EventLoop.Async, AttributeMap {

  /**
   * 日志打印
   */
  protected final ILogger log = VertxLogger.get();

  /**
   * 属性
   */
  private final Map<String, Object> attrs = new ConcurrentHashMap<>();

  protected final Self self;

  public VertxVerticle() {
    this.self = (Self) this;
  }

  @Override
  public Map<String, Object> attrs() {
    return attrs;
  }

  @Override
  public EventLoop eventLoop() {
    return VertxManager.get().getEventLoop();
  }

  @Override
  public abstract void start() throws Exception;

  @Override
  public abstract void stop() throws Exception;


  public static VertxVerticle create(IConsumer<VertxVerticle> start) {
    return create(start, null);
  }

  public static VertxVerticle create(IConsumer<VertxVerticle> start, IConsumer<VertxVerticle> stop) {
    return new VertxVerticle() {
      @Override
      public void start() throws Exception {
        if (start != null) {
          start.accept(this);
        }
      }

      @Override
      public void stop() throws Exception {
        if (stop != null) {
          stop.accept(this);
        }
      }
    };
  }

  protected <T> T awaitForResult(Future<T> future) {
    return awaitForResult(future, 5, TimeUnit.SECONDS);
  }

  protected <T> T awaitForResult(Future<T> future, long timeout, TimeUnit unit) {
    return VertxManager.get().awaitForResult(future, timeout, unit);
  }

  protected <T> void await(Future<T> future, Handler<AsyncResult<T>> handler) {
    await(future, 5, TimeUnit.SECONDS, handler);
  }

  protected <T> void await(Future<T> future, long timeout, TimeUnit unit, Handler<AsyncResult<T>> handler) {
    VertxManager.get().await(future, timeout, unit, handler);
  }


}
