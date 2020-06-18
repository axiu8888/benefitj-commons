package com.benefitj.event;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 事件发射器
 */
public class EventBusPoster {

  private static final Logger logger = LoggerFactory.getLogger(EventBusPoster.class);

  /**
   * 实例
   */
  private static final EventBusPoster INSTANCE = new EventBusPoster(newFixExecutor(2));

  public static EventBusPoster getInstance() {
    return EventBusPoster.INSTANCE;
  }

  /**
   * 初始化状态
   */
  private final AtomicBoolean initialized = new AtomicBoolean(false);

  /**
   * 同步的事件
   */
  private EventBus eventBus;
  /**
   * 异步事件
   */
  private AsyncEventBus asyncEventBus;
  /**
   * 异步的执行器
   */
  private Executor asyncExecutor;
  /**
   * 事件的类型
   */
  private Class<?> eventType = Object.class;
  /**
   * 默认的发送类型：同步或异步，默认是异步发送
   */
  private final AtomicBoolean asyncState = new AtomicBoolean(true);

  public EventBusPoster() {
    this(null);
  }

  public EventBusPoster(Executor executor) {
    setAsyncExecutor(executor);
  }

  public EventBusPoster(EventBus eventBus, AsyncEventBus asyncEventBus) {
    this.setEventBus(eventBus);
    this.setAsyncEventBus(asyncEventBus);
  }

  public void setEventBus(EventBus eventBus) {
    checkNotNull(eventBus, "eventBus");
    checkArgument(this.eventBus == null, "The eventBus is not null !");
    this.eventBus = eventBus;
  }

  protected EventBus getEventBus() {
    return eventBus;
  }

  public void setAsyncEventBus(AsyncEventBus asyncEventBus) {
    checkNotNull(asyncEventBus, "asyncEventBus");
    checkArgument(this.asyncEventBus == null, "The asyncEventBus is not null !");
    this.asyncEventBus = asyncEventBus;
  }

  protected AsyncEventBus getAsyncEventBus() {
    return asyncEventBus;
  }

  protected Executor getAsyncExecutor() {
    return asyncExecutor;
  }

  protected void setAsyncExecutor(Executor asyncExecutor) {
    checkNotNull(asyncExecutor, "asyncEventBus");
    checkArgument(this.asyncExecutor == null, "The asyncExecutor is not null !");
    this.asyncExecutor = asyncExecutor;
  }

  protected Class<?> getEventType() {
    return eventType;
  }

  /**
   * 设置事件类型
   *
   * @param eventType 事件类型
   */
  public void setEventType(Class<?> eventType) {
    this.eventType = eventType;
  }

  /**
   * 设置异步状态
   *
   * @param async 是否为异步发送
   */
  public void setAsyncState(boolean async) {
    this.asyncState.set(async);
  }

  /**
   * @return 返回是否为异步发送
   */
  public boolean isAsyncState() {
    return asyncState.get();
  }

  /**
   * 是否是支持发送和处理的事件
   *
   * @param event 事件
   * @return 返回是否支持
   */
  public boolean support(Object event) {
    return eventType.isInstance(event);
  }

  /**
   * 发送事件
   *
   * @param event 事件
   */
  public void post(Object event) {
    post(event, isAsyncState());
  }

  /**
   * 发送事件
   *
   * @param event 事件
   */
  public final void post(Object event, boolean async) {
    if (!support(event)) {
      throw new IllegalArgumentException("Unsupported event type!");
    }

    if (initialized.get()) {
      if (async) {
        getAsyncEventBus().post(event);
      } else {
        getEventBus().post(event);
      }
    }
  }

  /**
   * 发送事件
   *
   * @param event 事件
   */
  public void postSync(Object event) {
    post(event, false);
  }

  /**
   * 发送事件
   *
   * @param event 事件
   */
  public void postAsync(Object event) {
    post(event, true);
  }

  /**
   * 注册
   *
   * @param adapter 事件处理器
   */
  public void register(EventAdapter adapter) {
    if (adapter != null) {
      checkAndInit();
      getEventBus().register(adapter);
      getAsyncEventBus().register(adapter);
      logger.debug("Register event adapter class: {}", adapter.getClass());
    }
  }

  /**
   * 注册
   *
   * @param adapters 事件处理器
   */
  public void register(Collection<? extends EventAdapter> adapters) {
    for (EventAdapter handler : adapters) {
      register(handler);
    }
  }

  /**
   * 取消注册
   *
   * @param adapter 事件处理器
   */
  public void unregister(EventAdapter adapter) {
    if (adapter != null) {
      checkAndInit();
      getEventBus().unregister(adapter);
      getAsyncEventBus().unregister(adapter);
      logger.debug("Unregister event adapter class: {}", adapter.getClass());
    }
  }

  /**
   * 取消注册
   *
   * @param adapters 事件处理器
   */
  public void unregister(Collection<? extends EventAdapter> adapters) {
    for (EventAdapter adapter : adapters) {
      unregister(adapter);
    }
  }

  private void checkAndInit() {
    if (!initialized.get()) {
      if (getEventBus() == null) {
        setEventBus(new EventBus());
      }
      if (getAsyncExecutor() == null) {
        setAsyncExecutor(newSingleThreadExecutor());
      }
      if (getAsyncEventBus() == null) {
        setAsyncEventBus(new AsyncEventBus(getAsyncExecutor()));
      }
      initialized.set(true);
    }
  }

  /**
   * 创建单线程的调度器
   */
  public static ExecutorService newSingleThreadExecutor() {
    return newExecutor(threadFactory -> Executors.newSingleThreadExecutor());
  }

  /**
   * 创建调度器
   */
  public static ExecutorService newFixExecutor(int ratio) {
    final int availableProcessors = Runtime.getRuntime().availableProcessors();
    return Executors.newFixedThreadPool(availableProcessors * ratio, new DefaultThreadFactory());
  }

  /**
   * 创建调度器
   */
  public static ExecutorService newExecutor(Function<ThreadFactory, ExecutorService> function) {
    return function.apply(new DefaultThreadFactory());
  }

  /**
   * The default thread factory
   */
  protected static class DefaultThreadFactory implements ThreadFactory {
    private static final AtomicInteger poolNumber = new AtomicInteger(1);
    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    DefaultThreadFactory() {
      SecurityManager s = System.getSecurityManager();
      group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      namePrefix = "EventBus-" + poolNumber.getAndIncrement() + "@";
    }

    @Override
    public Thread newThread(Runnable r) {
      Thread t = new Thread(group, r,
          namePrefix + threadNumber.getAndIncrement(),
          0);
      if (t.isDaemon())
        t.setDaemon(false);
      if (t.getPriority() != Thread.NORM_PRIORITY)
        t.setPriority(Thread.NORM_PRIORITY);
      return t;
    }
  }
}
