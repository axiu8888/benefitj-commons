package com.benefitj.core.local;

/**
 * 线程缓存
 *
 * @param <T>
 */
public class ThreadLocalCache<T> implements LocalCache<T> {
  /**
   * 默认的回调
   */
  private static final InitialCallback NULL = () -> null;

  private InitialCallback<T> callback;
  /**
   * 线程缓存对象
   */
  private final ThreadLocal<T> local = ThreadLocal.withInitial(() -> getInitialCallback().initialValue());

  public ThreadLocalCache() {
    this(NULL);
  }

  public ThreadLocalCache(InitialCallback<T> callback) {
    this.setInitialCallback(callback);
  }

  /**
   * 获取 ThreadLocal 对象
   *
   * @return 返回ThreadLocal对象
   */
  public ThreadLocal<T> getLocal() {
    return local;
  }

  /**
   * 设置存储的值
   *
   * @param value 值
   */
  @Override
  public void set(T value) {
    getLocal().set(value);
  }

  /**
   * 获取存储的值，如果为null，返回默认值
   *
   * @param defaultValue 默认值
   * @return 返回存储的值
   */
  @Override
  public T get(T defaultValue) {
    T t = getLocal().get();
    return t != null ? t : defaultValue;
  }

  /**
   * 获取存储的值，如果不存在返回null
   *
   * @return 返回存储的值
   */
  @Override
  public T get() {
    return get(null);
  }

  /**
   * 移除存储的值
   */
  @Override
  public void remove() {
    getLocal().remove();
  }

  @Override
  public T getAndRemove() {
    ThreadLocal<T> l = getLocal();
    T t = l.get();
    l.remove();
    return t;
  }

  /**
   * 设置初始化回调
   *
   * @param callback 回调
   */
  @Override
  public void setInitialCallback(InitialCallback<T> callback) {
    this.callback = callback != null ? callback : NULL;
  }

  /**
   * 获取初始化值的回调
   *
   * @return 返回回调对象
   */
  @Override
  public InitialCallback<T> getInitialCallback() {
    return callback;
  }

}
