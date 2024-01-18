package com.benefitj.frameworks.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Policy;
import com.github.benmanes.caffeine.cache.RemovalListener;
import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * 缓存
 */
public interface CaffeineCache<K, V> extends Cache<K, V> {


  static <K, V> Caffeine<K, V> newBuilder() {
    return (Caffeine<K, V>) Caffeine.newBuilder();
  }

  static <K, V> RemovalListener<K, V> emptyRemovalListener() {
    return (RemovalListener<K, V>) REMOVAL_LISTENER;
  }

  RemovalListener<Object, Object> REMOVAL_LISTENER = (o, o2, removalCause) -> {
    // nothing
  };

  Cache<K, V> getCache();

  @Override
  @Nullable
  default V getIfPresent(K k) {
    return getCache().getIfPresent(k);
  }

  @Override
  @PolyNull
  default V get(K k, Function<? super K, ? extends @PolyNull V> function) {
    return getCache().get(k, function);
  }

  @Override
  default Map<K, V> getAllPresent(Iterable<? extends K> iterable) {
    return getCache().getAllPresent(iterable);
  }

  @Override
  default Map<K, V> getAll(Iterable<? extends K> iterable, Function<? super Set<? extends K>, ? extends Map<? extends K, ? extends V>> function) {
    return getCache().getAll(iterable, function);
  }

  @Override
  default void put(K k, V v) {
    getCache().put(k, v);
  }

  @Override
  default void putAll(Map<? extends K, ? extends V> map) {
    getCache().putAll(map);
  }

  @Override
  default void invalidate(K k) {
    getCache().invalidate(k);
  }

  @Override
  default void invalidateAll(Iterable<? extends K> iterable) {
    getCache().invalidateAll(iterable);
  }

  @Override
  default void invalidateAll() {
    getCache().invalidateAll();
  }

  @Override
  @NonNegative
  default long estimatedSize() {
    return getCache().estimatedSize();
  }

  @Override
  default CacheStats stats() {
    return getCache().stats();
  }

  @Override
  default ConcurrentMap<K, V> asMap() {
    return getCache().asMap();
  }

  @Override
  default void cleanUp() {
    getCache().cleanUp();
  }

  @Override
  default Policy<K, V> policy() {
    return getCache().policy();
  }

}
