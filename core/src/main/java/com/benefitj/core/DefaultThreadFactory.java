package com.benefitj.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default thread factory
 */
public class DefaultThreadFactory implements ThreadFactory {

  private static final AtomicInteger poolNumber = new AtomicInteger(1);
  private final ThreadGroup group;
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  private final String namePrefix;
  private boolean daemon = false;

  public DefaultThreadFactory() {
    this("pool-", "-thread-", false);
  }

  public DefaultThreadFactory(boolean daemon) {
    this("pool-", "-thread-", daemon);
  }

  public DefaultThreadFactory(String prefix, String suffix) {
    this(prefix, suffix, false);
  }

  public DefaultThreadFactory(String prefix, String suffix, boolean daemon) {
    SecurityManager s = System.getSecurityManager();
    this.group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();
    this.namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
    this.setDaemon(daemon);
  }

  public DefaultThreadFactory(ThreadGroup group, String prefix, String suffix) {
    this.group = group;
    this.namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r,
        namePrefix + threadNumber.getAndIncrement(), 0);
    t.setDaemon(daemon);
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }

  public ThreadGroup getGroup() {
    return group;
  }

  public AtomicInteger getThreadNumber() {
    return threadNumber;
  }

  public int getNextThreadNumber() {
    return getThreadNumber().incrementAndGet();
  }

  public String getNamePrefix() {
    return namePrefix;
  }

  public boolean isDaemon() {
    return daemon;
  }

  public void setDaemon(boolean daemon) {
    this.daemon = daemon;
  }

}
