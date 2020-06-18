package com.benefitj.core.local;


/**
 * 本地缓存接口
 *
 * @param <T> 类型
 */
public interface LocalCache<T> {

  /**
   * 设置存储的值
   *
   * @param value 值
   */
  void set(T value);

  /**
   * 获取存储的值，如果为null，返回默认值
   *
   * @param defaultValue 默认值
   * @return 返回存储的值
   */
  T get(T defaultValue);

  /**
   * 获取存储的值，如果不存在返回null
   *
   * @return 返回存储的值
   */
  T get();

  /**
   * 移除存储的值
   */
  void remove();

  /**
   * 获取后移除
   */
  T getAndRemove();

  /**
   * 设置初始化回调
   *
   * @param callback 回调
   */
  void setInitialCallback(InitialCallback<T> callback);

  /**
   * 获取初始化值的回调
   *
   * @return 返回回调对象
   */
  InitialCallback<T> getInitialCallback();
}
