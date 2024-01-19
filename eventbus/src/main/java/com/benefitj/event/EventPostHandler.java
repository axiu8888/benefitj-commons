package com.benefitj.event;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * 抽象事件发送处理器
 *
 * @param <C> 上下文
 * @param <E> 事件类型
 */
public class EventPostHandler<C, E extends Event> {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  /**
   * 过滤器
   */
  private final LinkedHashMap<String, Predicate<E>> filters = new LinkedHashMap<>();

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  /**
   * 事件发射器
   */
  private EventBusPoster poster = EventBusPoster.get();
  /**
   * 事件继续传递的处理器
   */
  private FireNext<C, E> fireNext = (ctx, event) -> {
    // ignore
  };

  public EventPostHandler() {
  }

  public EventPostHandler(EventBusPoster poster) {
    this.setPoster(poster);
  }

  public FireNext<C, E> getFireNext() {
    return fireNext;
  }

  public void setFireNext(FireNext<C, E> fireNext) {
    this.fireNext = fireNext;
  }

  /**
   * 处理事件
   *
   * @param ctx   上下文
   * @param event 事件
   * @throws Exception
   */
  public void process(C ctx, E event) throws Exception {
    final LinkedHashMap<String, Predicate<E>> filters = this.filters;
    if (filters.isEmpty()) {
      postEvent(event);
    } else {
      final Lock readLock = this.lock.readLock();
      try {
        for (Map.Entry<String, Predicate<E>> entry : filters.entrySet()) {
          try {
            if (entry.getValue().test(event)) {
              postEvent(event);
            }
          } catch (Exception e) {
            logger.error("event handle throw: {}", e.getMessage());
          }
        }
      } finally {
        readLock.unlock();
      }
    }

    // 是否继续传递
    if (isPostNext(event)) {
      final FireNext<C, E> fireNext = getFireNext();
      if (fireNext == null) {
        throw new IllegalStateException("The fireNext is null !");
      }
      fireNext.onNext(ctx, event);
    }
  }

  /**
   * @return 获取事件发送器
   */
  public EventBusPoster getPoster() {
    return poster;
  }

  /**
   * 设置事件发射器
   *
   * @param poster 发射器
   */
  public void setPoster(EventBusPoster poster) {
    Preconditions.checkNotNull(poster, "poster");
    this.poster = poster;
  }

  /**
   * @return 事件是否继续传递
   */
  public boolean isPostNext(E event) {
    return false;
  }

  /**
   * 发送事件
   *
   * @param event 事件
   */
  public void postEvent(E event) {
    if (event instanceof RawEvent) {
      // 取出原始数据发送
      getPoster().post(((RawEvent) event).getPayload());
    } else {
      getPoster().post(event);
    }
  }

  /**
   * 添加过滤器
   *
   * @param name   过滤器名称
   * @param filter 过滤器
   * @return 是否添加
   */
  public boolean addFilter(String name, Predicate<E> filter) {
    final Lock writeLock = this.lock.writeLock();
    try {
      return filters.putIfAbsent(name, filter) == null;
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * 移除过滤器
   *
   * @param name 过滤器名称
   * @return 被移除的过滤器
   */
  public Predicate<E> removeFilter(String name) {
    final Lock writeLock = this.lock.writeLock();
    try {
      return filters.remove(name);
    } finally {
      writeLock.unlock();
    }
  }

  /**
   * @return 获取全部的过滤器
   */
  public Map<String, Predicate<E>> filters() {
    final Lock writeLock = this.lock.writeLock();
    try {
      return Collections.unmodifiableMap(filters);
    } finally {
      writeLock.unlock();
    }
  }


  public interface FireNext<C, E> {
    /**
     * 继续传递
     *
     * @param ctx   上下文
     * @param event 事件
     */
    void onNext(C ctx, E event);
  }
}
