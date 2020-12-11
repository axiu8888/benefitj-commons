package com.benefitj.core.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁委托类
 */
public class ReadWriteLockDelegate {

  private final ReadWriteLock lock;

  public ReadWriteLockDelegate() {
    this(false);
  }

  public ReadWriteLockDelegate(boolean fair) {
    this.lock = new ReentrantReadWriteLock(fair);
  }

  /**
   * 执行读锁，并返回期待结果
   */
  public <V> V readLock(Callable<V> c) {
    final V v;
    final Lock readLock = this.lock.readLock();
    readLock.lock();
    try {
      v = c.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      readLock.unlock();
    }
    return v;
  }

  /**
   * 执行读锁
   */
  public void readLock(Runnable r) {
    final Lock readLock = this.lock.readLock();
    readLock.lock();
    try {
      r.run();
    } finally {
      readLock.unlock();
    }
  }

  /**
   * 执行写锁，并返回期待结果
   */
  public <V> V writeLock(Callable<V> c) {
    final Lock writeLock = this.lock.writeLock();
    writeLock.lock();
    try {
      return c.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * 执行写锁
   */
  public void writeLock(Runnable r) {
    final Lock writeLock = this.lock.writeLock();
    writeLock.lock();
    try {
      r.run();
    } finally {
      writeLock.unlock();
    }
  }

  public ReadWriteLock getLock() {
    return lock;
  }
  
}
