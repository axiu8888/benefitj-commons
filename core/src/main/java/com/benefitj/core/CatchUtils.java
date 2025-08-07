package com.benefitj.core;

import com.benefitj.core.executable.Instantiator;
import com.benefitj.core.functions.IRunnable;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
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
    Throwable root = findRoot(e);
    return root.getClass().isAssignableFrom(type)
        || e instanceof IllegalStateException//非法状态异常直接忽略
        || e instanceof NullPointerException//空指针需要直接抛出
        ? (RuntimeException) root
        : (RuntimeException) Instantiator.get().create(type, root);
  }

  /**
   * try{} catch(e){}
   */
  public static <T> T tryThrow(Callable<T> call) {
    return tryThrow(call, RuntimeException.class);
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
    tryThrow(r, RuntimeException.class);
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

  public static String getLogStackTrace(@Nullable Throwable tr) {
    if (tr == null) {
      return "";
    }

    // This is to reduce the amount of log spew that apps do in the non-error
    // condition of the network being unavailable.
    Throwable t = tr;
    while (t != null) {
      if (t instanceof UnknownHostException) {
        return "";
      }
      t = t.getCause();
    }

    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    tr.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }

}
