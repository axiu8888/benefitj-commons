package com.benefitj.core.functions;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Map
 *
 * @param <K>
 * @param <V>
 */
public interface WrappedMap<K, V> extends Map<K, V> {

  Map<K, V> map();

  @Override
  default int size() {
    return map().size();
  }

  @Override
  default boolean isEmpty() {
    return map().isEmpty();
  }

  @Override
  default boolean containsKey(Object key) {
    return map().containsKey(key);
  }

  @Override
  default boolean containsValue(Object value) {
    return map().containsValue(value);
  }

  @Override
  default V get(Object key) {
    return map().get(key);
  }

  @Override
  default V put(K key, V value) {
    return map().put(key, value);
  }

  @Override
  default V remove(Object key) {
    return map().remove(key);
  }

  @Override
  default void putAll(Map<? extends K, ? extends V> m) {
    map().putAll(m);
  }

  @Override
  default void clear() {
    map().clear();
  }

  @Override
  default Set<K> keySet() {
    return map().keySet();
  }

  @Override
  default Collection<V> values() {
    return map().values();
  }

  @Override
  default Set<Entry<K, V>> entrySet() {
    return map().entrySet();
  }

  @Override
  default V getOrDefault(Object key, V defaultValue) {
    return map().getOrDefault(key, defaultValue);
  }

  @Override
  default void forEach(BiConsumer<? super K, ? super V> action) {
    map().forEach(action);
  }

  @Override
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    map().replaceAll(function);
  }

  @Override
  default V putIfAbsent(K key, V value) {
    return map().putIfAbsent(key, value);
  }

  @Override
  default boolean remove(Object key, Object value) {
    return map().remove(key, value);
  }

  @Override
  default boolean replace(K key, V oldValue, V newValue) {
    return map().replace(key, oldValue, newValue);
  }

  @Override
  default V replace(K key, V value) {
    return map().replace(key, value);
  }

  @Override
  default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    return map().computeIfAbsent(key, mappingFunction);
  }

  @Override
  default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return map().computeIfPresent(key, remappingFunction);
  }

  @Override
  default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return map().compute(key, remappingFunction);
  }

  @Override
  default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return map().merge(key, value, remappingFunction);
  }

}
