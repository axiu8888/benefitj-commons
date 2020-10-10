package com.benefitj.netty;

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
  private boolean daemon;

  public DefaultThreadFactory() {
    this("pool-", "-thread-");
  }

  public DefaultThreadFactory(String prefix, String suffix) {
    this(prefix, suffix, isDaemon());
  }

  public DefaultThreadFactory(String prefix, String suffix, boolean daemon) {
    SecurityManager s = System.getSecurityManager();
    group = (s != null) ? s.getThreadGroup() :
        Thread.currentThread().getThreadGroup();
    namePrefix = prefix + poolNumber.getAndIncrement() + suffix;
    this.daemon = daemon;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(group, r,
        namePrefix + threadNumber.getAndIncrement(),
        0);
    t.setDaemon(daemon);
    if (t.getPriority() != Thread.NORM_PRIORITY)
      t.setPriority(Thread.NORM_PRIORITY);
    return t;
  }

  public static boolean isDaemon() {
    return Thread.currentThread().isDaemon();
  }

}
