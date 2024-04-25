package com.benefit.vertx.log;

import com.benefitj.core.ProxyUtils;
import com.benefitj.core.ReflectUtils;

import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicReference;

public interface VertxLogger {

  AtomicReference<VertxLogger> holder = new AtomicReference<>(Slf4jVertxLogger.NONE);

  static VertxLogger get() {
    return holder.get();
  }

  static void set(VertxLogger log) {
    holder.set(log);
  }

  static VertxLogger newProxy(VertxLogger log) {
    return newProxy((proxy, method, args) -> ReflectUtils.invoke(log, method, args));
  }

  static VertxLogger newProxy(InvocationHandler handler) {
    return ProxyUtils.newProxy(VertxLogger.class, handler);
  }

  void trace(String format);

  void trace(String format, Object... arguments);

  void trace(String msg, Throwable t);

}
