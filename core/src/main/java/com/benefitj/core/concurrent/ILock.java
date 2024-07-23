package com.benefitj.core.concurrent;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.functions.IRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁
 */
public interface ILock {

  Lock lock();

  /**
   * 执行读锁，并返回期待结果
   */
  default <V> V lock(Callable<V> c) {
    final Lock lock = lock();
    lock.lock();
    try {
      return c.call();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 执行读锁
   */
  default void lock(IRunnable r) {
    final Lock lock = lock();
    lock.lock();
    try {
      r.run();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      lock.unlock();
    }
  }

  /**
   * 新建锁
   *
   * @param fair 是否公平
   * @return 返回新建的锁
   */
  static ILock newLock(boolean fair) {
    return new Impl(fair);
  }


  class Impl implements ILock {

    final Lock lock;

    public Impl() {
      this(false);
    }

    public Impl(boolean fair) {
      this.lock = new ReentrantLock(fair);
    }

    @Override
    public Lock lock() {
      return null;
    }

  }

}
