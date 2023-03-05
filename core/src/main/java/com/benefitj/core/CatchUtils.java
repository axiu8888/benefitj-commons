package com.benefitj.core;

import com.benefitj.core.executable.Instantiator;
import com.benefitj.core.functions.IFunction;
import com.benefitj.core.functions.IRunnable;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 异常处理
 */
public class CatchUtils {
  /**
   * 实例化器
   */
  static final Instantiator instantiator = Instantiator.INSTANCE;

  /**
   * 抛出异常
   *
   * @param e    异常
   * @param type 要求的异常类型
   * @return 返回异常对象
   */
  public static RuntimeException throwing(Throwable e, Class<?> type) {
    if (StringUtils.isBlank(e.getMessage()) && e.getCause() != null) {
      if (e.getCause().getClass().isAssignableFrom(type)) {
        return (RuntimeException) e.getCause();
      }
      return (RuntimeException) instantiator.create(type, e.getCause().getMessage(), e.getCause());
    }
    if (e.getClass().isAssignableFrom(type)) {
      return (RuntimeException) e;
    }
    return (RuntimeException) instantiator.create(type, e.getMessage(), e);
  }

  /**
   * 抛出异常
   *
   * @param e          异常
   * @param mappedFunc 映射函数
   * @return 返回异常对象
   */
  public static RuntimeException throwing(Throwable e, IFunction<Throwable, RuntimeException> mappedFunc) {
    if (!(e instanceof RuntimeException)) {
      try {
        return mappedFunc.apply(e);
      } catch (Exception ex) {
        return new IllegalStateException(ex.getMessage(), ex);
      }
    }
    return (RuntimeException) e;
  }

  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call) {
    return tryThrow(call, IllegalStateException.class);
  }

  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call, Function<Exception, T> mappedFunc) {
    try {
      return call.call();
    } catch (Exception e) {
      return mappedFunc.apply(e);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call, Class<? extends RuntimeException> exception) {
    try {
      return call.call();
    } catch (Exception e) {
      throw throwing(e, exception);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static void tryThrow(IRunnable r) {
    tryThrow(r, IllegalStateException.class);
  }

  /**
   * try{} catch(e){}
   */
  public static void tryThrow(IRunnable r, Class<? extends RuntimeException> exception) {
    try {
      r.run();
    } catch (Exception e) {
      throw throwing(e, exception);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static void tryThrow(IRunnable r, Consumer<Exception> mappedFunc) {
    try {
      r.run();
    } catch (Exception e) {
      mappedFunc.accept(e);
    }
  }

  /**
   * 忽略异常
   */
  public static void ignore(IRunnable r) {
    tryThrow(r, e -> {/* ~ */});
  }

  /**
   * 忽略异常
   */
  public static <T> T ignore(Callable<T> call) {
    return ignore(call, (T) null);
  }

  /**
   * 忽略异常
   */
  public static <T> T ignore(Callable<T> call, T defaultValue) {
    try {
      return call.call();
    } catch (Exception e) {
      return defaultValue;
    }
  }

  /**
   * 忽略异常
   */
  public static <T> T ignore(Callable<T> call, Function<Exception, T> mappedFunc) {
    try {
      return call.call();
    } catch (Exception e) {
      return mappedFunc.apply(e);
    }
  }

}
