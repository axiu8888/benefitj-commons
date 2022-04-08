package com.benefitj.core.concurrent;

import com.benefitj.core.CatchUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 锁委托类
 */
public class LockDelegate {

  private final Lock lock;

  public LockDelegate() {
    this(false);
  }

  public LockDelegate(boolean fair) {
    this.lock = new ReentrantLock(fair);
  }

  /**
   * 执行读锁，并返回期待结果
   */
  public <V> V lock(Callable<V> c) {
    final Lock lock = this.lock;
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
  public void lock(Runnable r) {
    final Lock lock = this.lock;
    lock.lock();
    try {
      r.run();
    } finally {
      lock.unlock();
    }
  }

  public Lock getLock() {
    return lock;
  }

  public Condition newCondition() {
    return getLock().newCondition();
  }
}
