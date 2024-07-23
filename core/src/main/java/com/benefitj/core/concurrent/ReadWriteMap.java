package com.benefitj.core.concurrent;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 读写锁的Map
 */
public interface ReadWriteMap<K, V> extends Map<K, V> {

  @Override
  int size();

  @Override
  boolean isEmpty();

  @Override
  boolean containsKey(Object key);

  @Override
  boolean containsValue(Object value);

  @Override
  V get(Object key);

  @Override
  V put(K key, V value);

  @Override
  V remove(Object key);

  @Override
  void putAll(Map<? extends K, ? extends V> m);

  @Override
  void clear();

  @Override
  Set<K> keySet();

  @Override
  Collection<V> values();

  @Override
  Set<Entry<K, V>> entrySet();

  @Override
  boolean equals(Object o);

  @Override
  int hashCode();

  @Override
  default V getOrDefault(Object key, V defaultValue) {
    return Map.super.getOrDefault(key, defaultValue);
  }

  @Override
  default void forEach(BiConsumer<? super K, ? super V> action) {
    Map.super.forEach(action);
  }

  @Override
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    Map.super.replaceAll(function);
  }

  @Override
  default V putIfAbsent(K key, V value) {
    return Map.super.putIfAbsent(key, value);
  }

  @Override
  default boolean remove(Object key, Object value) {
    return Map.super.remove(key, value);
  }

  @Override
  default boolean replace(K key, V oldValue, V newValue) {
    return Map.super.replace(key, oldValue, newValue);
  }

  @Override
  default V replace(K key, V value) {
    return Map.super.replace(key, value);
  }

  @Override
  default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    return Map.super.computeIfAbsent(key, mappingFunction);
  }

  @Override
  default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return Map.super.computeIfPresent(key, remappingFunction);
  }

  @Override
  default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return Map.super.compute(key, remappingFunction);
  }

  @Override
  default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return Map.super.merge(key, value, remappingFunction);
  }


  class Impl<K, V> implements ReadWriteMap<K, V> {

    /**
     * 可重入读写锁，保证并发读写安全性
     */
    final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    final Lock readLock = readWriteLock.readLock();
    final Lock writeLock = readWriteLock.writeLock();

    final Map<K, V> map;

    public Impl(Map<K, V> map) {
      this.map = map;
    }

    public Map<K, V> map() {
      return map;
    }

    protected <T> T readLock(Function<Map<K, V>, T> fun) {
      readLock.lock();
      try {
        return fun.apply(map());
      } finally {
        readLock.unlock();
      }
    }

    protected <T> T writeLock(Function<Map<K, V>, T> fun) {
      writeLock.lock();
      try {
        return fun.apply(map());
      } finally {
        writeLock.unlock();
      }
    }

    @Override
    public int size() {
      return readLock(Map::size);
    }

    @Override
    public boolean isEmpty() {
      return readLock(Map::isEmpty);
    }

    @Override
    public boolean containsKey(Object key) {
      return readLock(m -> m.containsKey(key));
    }

    @Override
    public boolean containsValue(Object value) {
      return readLock(m -> m.containsValue(value));
    }

    @Override
    public V get(Object key) {
      return readLock(m -> m.get(key));
    }

    @Override
    public V put(K key, V value) {
      return writeLock(m -> m.put(key, value));
    }

    @Override
    public V remove(Object key) {
      return writeLock(m -> m.remove(key));
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
      writeLock(map -> {
        map.putAll(m);
        return 0;
      });
    }

    @Override
    public void clear() {
      writeLock(m -> {
        m.clear();
        return 0;
      });
    }

    @Override
    public Set<K> keySet() {
      return readLock(Map::keySet);
    }

    @Override
    public Collection<V> values() {
      return readLock(Map::values);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
      return readLock(Map::entrySet);
    }

    @Override
    public V getOrDefault(Object key, V defaultValue) {
      return readLock(m -> m.getOrDefault(key, defaultValue));
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> action) {
      readLock(m -> {
        m.forEach(action);
        return 0;
      });
    }

    @Override
    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
      writeLock(m -> {
        m.replaceAll(function);
        return 0;
      });
    }

    @Override
    public V putIfAbsent(K key, V value) {
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
    public boolean remove(Object key, Object value) {
      return writeLock(m -> m.remove(key, value));
    }

    @Override
    public boolean replace(K key, V oldValue, V newValue) {
      return writeLock(m -> m.replace(key, oldValue, newValue));
    }

    @Override
    public V replace(K key, V value) {
      return writeLock(m -> m.replace(key, value));
    }

    @Override
    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
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
    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      return writeLock(m -> m.computeIfPresent(key, remappingFunction));
    }

    @Deprecated
    @Override
    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      return writeLock(m -> m.compute(key, remappingFunction));
    }

    @Deprecated
    @Override
    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      return writeLock(m -> m.merge(key, value, remappingFunction));
    }

  }

}
