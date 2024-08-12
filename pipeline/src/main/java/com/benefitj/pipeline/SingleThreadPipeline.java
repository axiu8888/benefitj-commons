package com.benefitj.pipeline;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.EventLoop;
import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ReflectUtils;

import java.util.concurrent.Callable;

/**
 * 单线程的
 */
public interface SingleThreadPipeline extends Pipeline {

  EventLoop.Single executor();

  static SingleThreadPipeline create() {
    return create(new DefaultPipeline(), EventLoop.newSingle(true));
  }

  static SingleThreadPipeline create(Pipeline pipeline) {
    return create(pipeline, EventLoop.newSingle(true));
  }

  static SingleThreadPipeline create(Pipeline pipeline, EventLoop.Single executor) {
    if (pipeline instanceof SingleThreadPipeline) return (SingleThreadPipeline) pipeline;
    return ProxyUtils.newProxy(SingleThreadPipeline.class, (proxy, method, args) -> {
      try {
        if (method.getName().equals("executor")
            && method.getParameterCount() == 0
            && method.getDeclaringClass().isAssignableFrom(SingleThreadPipeline.class)) {
          return executor;
        }
        if (executor.isInLoop()) {
          return method.isDefault()
              ? ReflectUtils.invokeDefault(pipeline, method, args)
              : ReflectUtils.invoke(pipeline, method, args);
        }
        Callable<Object> call = () -> method.isDefault()
            ? ReflectUtils.invokeDefault(pipeline, method, args)
            : ReflectUtils.invoke(pipeline, method, args);
        return executor.inLoop(call).get();
      } catch (Exception e) {
        throw new IllegalStateException(CatchUtils.findRoot(e));
      }
    });
  }

}
