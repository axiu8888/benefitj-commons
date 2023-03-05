package com.benefitj.core.functions;

/**
 * 多过滤器
 *
 * @param <T> 符合对象：Map
 * @param <K> 键
 * @param <V> 值
 */
public interface MultiFilter<T, K, V> {
  /**
   * Evaluates this predicate on the given arguments.
   *
   * @param t the first input argument
   * @param k the second input argument
   * @param v the third input argument
   * @return {@code true} if the input arguments match the predicate,
   * otherwise {@code false}
   */
  boolean test(T t, K k, V v);

}
