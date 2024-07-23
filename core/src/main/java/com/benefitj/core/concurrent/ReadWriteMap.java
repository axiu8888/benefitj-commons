package com.benefitj.core.concurrent;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 读写加锁的Map
 */
public interface ReadWriteMap<K, V> extends Map<K, V> {

  Map<K, V> map();

  ReadWriteLock lock();

  default <T> T readLock(Function<Map<K, V>, T> fun) {
    Lock rl = lock().readLock();
    rl.lock();
    try {
      return fun.apply(map());
    } finally {
      rl.unlock();
    }
  }

  default <T> T writeLock(Function<Map<K, V>, T> fun) {
    Lock wl = lock().writeLock();
    wl.lock();
    try {
      return fun.apply(map());
    } finally {
      wl.unlock();
    }
  }

  @Override
  default int size() {
    return readLock(Map::size);
  }

  @Override
  default boolean isEmpty() {
    return readLock(Map::isEmpty);
  }

  @Override
  default boolean containsKey(Object key) {
    return readLock(m -> m.containsKey(key));
  }

  @Override
  default boolean containsValue(Object value) {
    return readLock(m -> m.containsValue(value));
  }

  @Override
  default V get(Object key) {
    return readLock(m -> m.get(key));
  }

  @Override
  default V put(K key, V value) {
    return writeLock(m -> m.put(key, value));
  }

  @Override
  default V remove(Object key) {
    return writeLock(m -> m.remove(key));
  }

  @Override
  default void putAll(Map<? extends K, ? extends V> m) {
    writeLock(map -> {
      map.putAll(m);
      return 0;
    });
  }

  @Override
  default void clear() {
    writeLock(m -> {
      m.clear();
      return 0;
    });
  }

  @Override
  default Set<K> keySet() {
    return readLock(Map::keySet);
  }

  @Override
  default Collection<V> values() {
    return readLock(Map::values);
  }

  @Override
  default Set<Entry<K, V>> entrySet() {
    return readLock(Map::entrySet);
  }

  @Override
  default V getOrDefault(Object key, V defaultValue) {
    return readLock(m -> m.getOrDefault(key, defaultValue));
  }

  @Override
  default void forEach(BiConsumer<? super K, ? super V> action) {
    readLock(m -> {
      m.forEach(action);
      return 0;
    });
  }

  @Override
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    writeLock(m -> {
      m.replaceAll(function);
      return 0;
    });
  }

  @Override
  default V putIfAbsent(K key, V value) {
    V v;
    if ((v = get(key)) == null) {
      return writeLock(m -> {
        V v2;
        return (v2 = m.get(key)) == null ? v2 : m.put(key, value);
      });
    }
    return v;
  }

  @Override
  default boolean remove(Object key, Object value) {
    return writeLock(m -> m.remove(key, value));
  }

  @Override
  default boolean replace(K key, V oldValue, V newValue) {
    return writeLock(m -> m.replace(key, oldValue, newValue));
  }

  @Override
  default V replace(K key, V value) {
    return writeLock(m -> m.replace(key, value));
  }

  @Override
  default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    Objects.requireNonNull(mappingFunction);
    V v;
    if ((v = get(key)) == null) {
      return writeLock(m -> {
        V v2;
        if ((v2 = m.get(key)) == null) {
          V newValue;
          if ((newValue = mappingFunction.apply(key)) != null) {
            m.put(key, newValue);
            return newValue;
          }
        }
        return v2;
      });
    }
    return v;
  }

  @Deprecated
  @Override
  default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return writeLock(m -> m.computeIfPresent(key, remappingFunction));
  }

  @Deprecated
  @Override
  default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return writeLock(m -> m.compute(key, remappingFunction));
  }

  @Deprecated
  @Override
  default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return writeLock(m -> m.merge(key, value, remappingFunction));
  }

  class Impl<K, V> implements ReadWriteMap<K, V> {

    /**
     * 可重入读写锁，保证并发读写安全性
     */
    final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    final Map<K, V> map;

    public Impl() {
      this(new HashMap<>());
    }

    public Impl(Map<K, V> map) {
      this.map = map;
    }

    @Override
    public Map<K, V> map() {
      return map;
    }

    @Override
    public ReentrantReadWriteLock lock() {
      return lock;
    }
  }

}
