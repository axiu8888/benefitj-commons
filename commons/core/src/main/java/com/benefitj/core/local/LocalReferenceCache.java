package com.benefitj.core.local;

import java.lang.ref.Reference;

/**
 * 本地缓存，使用引用持有对象
 *
 * @param <E> 元素
 * @param <R> 引用
 */
public class LocalReferenceCache<E, R extends Reference<E>> implements LocalCache<E> {

  private final ThreadLocalCache<R> localCache = new ThreadLocalCache<>();

  public LocalReferenceCache() {
  }

  public LocalReferenceCache(ReferenceInitialCallback<E, R> callback) {
    this.localCache.setInitialCallback(callback);
  }

  protected ThreadLocalCache<R> getCache() {
    return localCache;
  }

  /**
   * 获取当前缓存对象的引用对象
   *
   * @return 返回引用的对象
   */
  protected R getReference() {
    return getCache().get();
  }

  @Override
  public void set(E value) {
    R reference = getReferenceCallback().newReference(value);
    getCache().set(reference);
  }

  @Override
  public E get(E defaultValue) {
    R r = getCache().get();
    return r != null ? r.get() : defaultValue;
  }

  @Override
  public E get() {
    return get(null);
  }

  @Override
  public void remove() {
    getCache().remove();
  }

  @Override
  public E getAndRemove() {
    R r = getCache().get();
    E e = r != null ? r.get() : null;
    remove();
    return e;
  }

  public void setReferenceCallback(ReferenceInitialCallback<E, R> callback) {
    getCache().setInitialCallback(callback);
  }

  public ReferenceInitialCallback<E, R> getReferenceCallback() {
    return (ReferenceInitialCallback<E, R>) getCache().getInitialCallback();
  }

  /**
   * 不支持，请使用 {@link #setReferenceCallback(ReferenceInitialCallback)}
   *
   * @param callback 回调
   */
  @Deprecated
  @Override
  public void setInitialCallback(InitialCallback<E> callback) {
    throw new UnsupportedOperationException("不支持，请使用#setReferenceCallback(ReferenceInitialCallback<E, R> callback)");
  }

  /**
   * 不支持，请使用 {@link #getReferenceCallback()}
   */
  @Deprecated
  @Override
  public InitialCallback<E> getInitialCallback() {
    throw new UnsupportedOperationException("不支持，请使用#getReferenceCallback()");
  }

}
