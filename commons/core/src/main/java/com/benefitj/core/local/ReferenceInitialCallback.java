package com.benefitj.core.local;

import java.lang.ref.Reference;
import java.util.function.Function;

/**
 * 初始化引用
 *
 * @param <T> 元素
 * @param <R> 引用
 */
public interface ReferenceInitialCallback<T, R extends Reference<T>> extends InitialCallback<R> {

  /**
   * 初始化值
   *
   * @return 返回初始化值
   */
  @Override
  R initialValue();

  /**
   * 创建新引用
   *
   * @param t 存储的元素
   * @return 返回创建的新引用
   */
  R newReference(T t);

  /**
   * 默认的回调
   *
   * @param <E>
   * @param <R>
   */
  class DefaultReferenceInitialCallback<E, R extends Reference<E>> implements ReferenceInitialCallback<E, R> {

    private final Function<E, R> creator;

    public DefaultReferenceInitialCallback(Function<E, R> creator) {
      this.creator = creator;
    }

    @Override
    public R initialValue() {
      return null;
    }

    @Override
    public R newReference(E e) {
      return e != null ? creator.apply(e) : null;
    }
  }

  static <E, R extends Reference<E>> DefaultReferenceInitialCallback<E, R> newCallback(Function<E, R> creator) {
    return new DefaultReferenceInitialCallback<>(creator);
  }
}
