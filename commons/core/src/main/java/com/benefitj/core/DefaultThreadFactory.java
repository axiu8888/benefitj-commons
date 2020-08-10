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

  public DefaultThreadFactory() {
    this("pool-", "-thread-");
  }

  public DefaultThreadFactory(String prefix, String suffix) {
    SecurityManager s = System.getSecurityManager();
    this.group = (s != null) ? s.getThreadGroup() :
            Thread.currentThread().getThreadGroup();
    this.namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
  }

  public DefaultThreadFactory(ThreadGroup group, String prefix, String suffix) {
    this.group = group;
    this.namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r,
            namePrefix + threadNumber.getAndIncrement(), 0);
    if (t.isDaemon()) {
      t.setDaemon(false);
    }
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }
}
