package com.benefitj.core;

import com.benefitj.core.executable.Instantiator;
import com.benefitj.core.functions.IRunnable;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 异常处理
 */
public class CatchUtils {

  /**
   * 查找调用链的根节点异常
   */
  public static Throwable findRoot(Throwable error) {
    return find(error, e -> StringUtils.isNotBlank(e.getMessage()) || e.getCause() == null);
  }

  /**
   * 查找调用链的异常
   */
  public static Throwable find(Throwable error, Predicate<Throwable> filter) {
    Throwable e = error;
    while (!filter.test(e)) e = e.getCause();
    return e;
  }

  /**
   * 抛出异常
   *
   * @param e    异常
   * @param type 要求的异常类型
   * @return 返回异常对象
   */
  public static RuntimeException throwing(Throwable e, Class<?> type) {
    Throwable error = findRoot(e);
    return error.getClass().isAssignableFrom(type)
        ? (RuntimeException) e
        : (RuntimeException) Instantiator.get().create(type, error.getMessage(), error);
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
  public static <T> T tryThrow(Callable<T> call, Function<Throwable, T> mappedFunc) {
    try {
      return call.call();
    } catch (Throwable e) {
      return mappedFunc.apply(e);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call, Class<? extends RuntimeException> exception) {
    try {
      return call.call();
    } catch (Throwable e) {
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
    } catch (Throwable e) {
      throw throwing(e, exception);
    }
  }

  /**
   * try{} catch(e){}
   */
  public static void tryThrow(IRunnable r, Consumer<Throwable> mappedFunc) {
    try {
      r.run();
    } catch (Throwable e) {
      mappedFunc.accept(e);
    }
  }

  /**
   * 忽略异常
   */
  public static void ignore(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException ignored) {}
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
    } catch (Throwable e) {
      return defaultValue;
    }
  }

  /**
   * 忽略异常
   */
  public static <T> T ignore(Callable<T> call, Function<Throwable, T> mappedFunc) {
    try {
      return call.call();
    } catch (Throwable e) {
      return mappedFunc.apply(e);
    }
  }

}
