package com.benefitj.core.concurrent;

import com.benefitj.core.CatchUtils;
import com.benefitj.core.functions.IRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 读写锁
 */
public interface IReadWriteLock {

  ReadWriteLock lock();

  /**
   * 执行读锁，并返回期待结果
   */
  default <V> V readLock(Callable<V> c) {
    final V v;
    final Lock readLock = this.lock().readLock();
    readLock.lock();
    try {
      v = c.call();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      readLock.unlock();
    }
    return v;
  }

  /**
   * 执行读锁
   */
  default void readLock(IRunnable r) {
    final Lock readLock = this.lock().readLock();
    readLock.lock();
    try {
      r.run();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      readLock.unlock();
    }
  }

  /**
   * 执行写锁，并返回期待结果
   */
  default <V> V writeLock(Callable<V> c) {
    final Lock writeLock = this.lock().writeLock();
    writeLock.lock();
    try {
      return c.call();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * 执行写锁
   */
  default void writeLock(IRunnable r) {
    final Lock writeLock = this.lock().writeLock();
    writeLock.lock();
    try {
      r.run();
    } catch (Exception e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    } finally {
      writeLock.unlock();
    }
  }


  class Impl implements IReadWriteLock {

    final ReadWriteLock lock;

    public Impl() {
      this(false);
    }

    public Impl(boolean fair) {
      this.lock = new ReentrantReadWriteLock(fair);
    }

    @Override
    public ReadWriteLock lock() {
      return lock;
    }
  }

}
