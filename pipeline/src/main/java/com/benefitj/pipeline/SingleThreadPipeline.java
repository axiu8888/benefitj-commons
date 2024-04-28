package com.benefitj.pipeline;

import com.benefitj.core.EventLoop;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ReflectUtils;

import java.lang.annotation.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * 单线程的
 */
public interface SingleThreadPipeline extends Pipeline {

  @SafeRun
  @Override
  void fireNext(Object msg);

  @SafeRun
  @Override
  void fireNext(String baseName, Object msg);

  @SafeRun
  @Override
  void firePrev(Object msg);

  @SafeRun
  @Override
  void firePrev(String baseName, Object msg);

  @SafeRun
  void safe(Runnable r);

  @SafeRun
  <T, U> U safe(Function<T, U> mapped);

  static SingleThreadPipeline newInstance() {
    return wrap(new DefaultPipeline(), EventLoop.newSingle(true));
  }

  static SingleThreadPipeline wrap(Pipeline pipeline) {
    return wrap(pipeline, EventLoop.newSingle(true));
  }

  static SingleThreadPipeline wrap(Pipeline pipeline, ExecutorService executor) {
    if (pipeline instanceof SingleThreadPipeline) return (SingleThreadPipeline) pipeline;
    final Thread[] singles = new Thread[1];
    return ProxyUtils.newProxy(SingleThreadPipeline.class, (proxy, method, args) -> {
      if (method.isAnnotationPresent(SafeRun.class)) {
        if (singles[0] != Thread.currentThread()) {
          // 调用函数
          return executor.submit(() -> {
            try {
              return method.isDefault()
                  ? ReflectUtils.invokeDefault(pipeline, method, args)
                  : ReflectUtils.invoke(pipeline, method, args);
            } finally {
              if (singles[0] == null) {
                singles[0] = Thread.currentThread();
              } else {
                if (singles[0] != Thread.currentThread()) {
                  throw new IllegalStateException("线程池不是单线程: " + executor);
                }
              }
            }
          }).get();
        }
      }
      return method.isDefault()
          ? ReflectUtils.invokeDefault(pipeline, method, args)
          : ReflectUtils.invoke(pipeline, method, args);
    });
  }

  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.METHOD)
  @Inherited
  @interface SafeRun {
  }

}
