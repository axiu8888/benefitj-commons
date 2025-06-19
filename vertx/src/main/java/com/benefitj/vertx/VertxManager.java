package com.benefitj.vertx;

import com.benefitj.core.CountDownLatch2;
import com.benefitj.core.EventLoop;
import com.benefitj.core.SingletonSupplier;
import com.benefitj.core.local.LocalCache;
import com.benefitj.core.local.LocalCacheFactory;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class VertxManager {

  static final SingletonSupplier<VertxManager> singleton = SingletonSupplier.of(VertxManager::new);

  public static VertxManager get() {
    return singleton.get();
  }

  final AtomicReference<EventLoop> eventLoop = new AtomicReference<>(EventLoop.io());

  public EventLoop getEventLoop() {
    return eventLoop.get();
  }

  public void setEventLoop(EventLoop loop) {
    eventLoop.set(loop);
  }


  final LocalCache<Thread> localThread = LocalCacheFactory.newCache();

  public boolean isLocalThread() {
    return isLocalThread(Thread.currentThread());
  }

  public boolean isLocalThread(Thread current) {
    return localThread.get() == current;
  }

  public void setLocalThread() {
    setLocalThread(Thread.currentThread());
  }

  public void setLocalThread(Thread local) {
    localThread.set(local);
  }

  public void removeLocalThread() {
    localThread.remove();
  }


  public void safeLocalThread(Runnable r) {
    try {
      setLocalThread();
      r.run();
    } finally {
      removeLocalThread();
    }
  }


  private final LocalCache<AtomicReference> resultCache = LocalCacheFactory.newCache(AtomicReference::new);
  private final LocalCache<AtomicReference> errCache = LocalCacheFactory.newCache(AtomicReference::new);

  public <T> T awaitForResult(Future<T> future) {
    return awaitForResult(future, 5, TimeUnit.SECONDS);
  }

  public <T> T awaitForResult(Future<T> future, long timeout, TimeUnit unit) {
    final AtomicReference<T> ref = resultCache.get();
    final AtomicReference<Throwable> err = errCache.get();
    ref.set(null);
    await(future, timeout, unit, res -> {
      if (res.succeeded()) ref.set(res.result());
      else err.set(res.cause());
    });
    Throwable cause = err.getAndSet(null);
    if (cause != null) {
      throw new IllegalStateException(cause);
    }
    return ref.getAndSet(null);
  }

  public <T> void await(Future<T> future, Handler<AsyncResult<T>> handler) {
    await(future, 5, TimeUnit.SECONDS, handler);
  }

  public <T> void await(Future<T> future, long timeout, TimeUnit unit, Handler<AsyncResult<T>> handler) {
    if (isLocalThread()) {
      throw new IllegalStateException("请不要在当前线程调用此方法, 此方法会导致线程阻塞, thread: " + Thread.currentThread().getName());
    }
    final CountDownLatch2 latch = CountDownLatch2.ignore(1);
    future.onComplete(res -> {
      try {
        handler.handle(res);
      } finally {
        latch.countDown();
      }
    });
    latch.await(timeout, unit);
  }

}
