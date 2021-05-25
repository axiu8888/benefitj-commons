package com.benefitj.core;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Map
 *
 * @param <K>
 * @param <V>
 */
public interface WrappedMap<K, V> extends Map<K, V> {

  Map<K, V> source();

  @Override
  default int size() {
    return source().size();
  }

  @Override
  default boolean isEmpty() {
    return source().isEmpty();
  }

  @Override
  default boolean containsKey(Object key) {
    return source().containsKey(key);
  }

  @Override
  default boolean containsValue(Object value) {
    return source().containsValue(value);
  }

  @Override
  default V get(Object key) {
    return source().get(key);
  }

  @Override
  default V put(K key, V value) {
    return source().put(key, value);
  }

  @Override
  default V remove(Object key) {
    return source().remove(key);
  }

  @Override
  default void putAll(Map<? extends K, ? extends V> m) {
    source().putAll(m);
  }

  @Override
  default void clear() {
    source().clear();
  }

  @Override
  default Set<K> keySet() {
    return source().keySet();
  }

  @Override
  default Collection<V> values() {
    return source().values();
  }

  @Override
  default Set<Entry<K, V>> entrySet() {
    return source().entrySet();
  }

}
