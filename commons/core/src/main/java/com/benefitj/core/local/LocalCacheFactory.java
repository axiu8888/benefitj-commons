package com.benefitj.core.local;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * 创建本地缓存对象的工厂
 */
public class LocalCacheFactory {
  /**
   * 创建缓存
   *
   * @param <T> 类型
   * @return 返回 LocalCache 的对象
   */
  public static <T> LocalCache<T> newCache() {
    return new ThreadLocalCache<>();
  }

  /**
   * 创建缓存
   *
   * @param call 初始化回调
   * @param <T>  类型
   * @return 返回 LocalCache 的对象
   */
  public static <T> LocalCache<T> newCache(InitialCallback<T> call) {
    return new ThreadLocalCache<>(call);
  }

  /**
   * 创建 WeakHashMap 的缓存
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回 WeakHashMap 的缓存对象
   */
  public static <K, V> LocalMapCache<K, V> newWeakMapCache() {
    return new LocalMapCache<>(WeakHashMap::new);
  }

  /**
   * 创建 WeakHashMap 的缓存
   *
   * @param absentFunction function
   * @param <K>            键
   * @param <V>            值
   * @return 返回 WeakHashMap 的缓存对象
   */
  public static <K, V> LocalMapCache<K, V> newWeakMapCache(Function<K, V> absentFunction) {
    return new LocalMapCache<>(WeakHashMap::new, absentFunction);
  }

  /**
   * 创建 HashMap 的缓存
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回 HashMap 的缓存对象
   */
  public static <K, V> LocalMapCache<K, V> newHashMapCache() {
    return new LocalMapCache<>(HashMap::new);
  }

  /**
   * 创建 HashMap 的缓存
   *
   * @param absentFunction function
   * @param <K>            键
   * @param <V>            值
   * @return 返回 HashMap 的缓存对象
   */
  public static <K, V> LocalMapCache<K, V> newHashMapCache(Function<K, V> absentFunction) {
    return new LocalMapCache<>(HashMap::new, absentFunction);
  }


  /**
   * 创建 ConcurrentHashMap 的缓存
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回 HashMap 的缓存对象
   */
  public static <K, V> LocalMapCache<K, V> newConcurrentHashMapCache() {
    return new LocalMapCache<>(ConcurrentHashMap::new);
  }

  /**
   * 创建引用缓存
   *
   * @param <T> 类型
   * @return 返回 ThreadLocalCache 的对象
   */
  public static <T, R extends Reference<T>> LocalReferenceCache<T, R> newReferenceCache(ReferenceInitialCallback<T, R> call) {
    return new LocalReferenceCache<>(call);
  }

  /**
   * 创建弱引用缓存
   *
   * @param <T> 类型
   * @return 返回 ThreadLocalCache 的对象
   */
  public static <T> LocalReferenceCache<T, WeakReference<T>> newWeakReferenceCache() {
    return newReferenceCache(ReferenceInitialCallback.newCallback(WeakReference::new));
  }

  /**
   * 创建软引用缓存
   *
   * @param <T> 类型
   * @return 返回 ThreadLocalCache 的对象
   */
  public static <T> LocalReferenceCache<T, SoftReference<T>> newSoftReferenceCache() {
    return newReferenceCache(ReferenceInitialCallback.newCallback(SoftReference::new));
  }

  /**
   * 创建 WeakHashMap 的缓存
   *
   * @return 返回 WeakHashMap 的缓存对象
   */
  public static LocalMapCache<Integer, byte[]> newBytesWeakHashMapCache() {
    return newBytesWeakHashMapCache(byte[]::new);
  }

  /**
   * 创建 WeakHashMap 的缓存
   *
   * @return 返回 WeakHashMap 的缓存对象
   */
  public static LocalMapCache<Integer, byte[]> newBytesWeakHashMapCache(Function<Integer, byte[]> absentFunction) {
    return new LocalMapCache<>(WeakHashMap::new, absentFunction);
  }

}
