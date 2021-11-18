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

  Map<K, V> getOriginal();

  @Override
  default int size() {
    return getOriginal().size();
  }

  @Override
  default boolean isEmpty() {
    return getOriginal().isEmpty();
  }

  @Override
  default boolean containsKey(Object key) {
    return getOriginal().containsKey(key);
  }

  @Override
  default boolean containsValue(Object value) {
    return getOriginal().containsValue(value);
  }

  @Override
  default V get(Object key) {
    return getOriginal().get(key);
  }

  @Override
  default V put(K key, V value) {
    return getOriginal().put(key, value);
  }

  @Override
  default V remove(Object key) {
    return getOriginal().remove(key);
  }

  @Override
  default void putAll(Map<? extends K, ? extends V> m) {
    getOriginal().putAll(m);
  }

  @Override
  default void clear() {
    getOriginal().clear();
  }

  @Override
  default Set<K> keySet() {
    return getOriginal().keySet();
  }

  @Override
  default Collection<V> values() {
    return getOriginal().values();
  }

  @Override
  default Set<Entry<K, V>> entrySet() {
    return getOriginal().entrySet();
  }

  @Override
  default V getOrDefault(Object key, V defaultValue) {
    return getOriginal().getOrDefault(key, defaultValue);
  }

  @Override
  default void forEach(BiConsumer<? super K, ? super V> action) {
    getOriginal().forEach(action);
  }

  @Override
  default void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
    getOriginal().replaceAll(function);
  }

  @Override
  default V putIfAbsent(K key, V value) {
    return getOriginal().putIfAbsent(key, value);
  }

  @Override
  default boolean remove(Object key, Object value) {
    return getOriginal().remove(key, value);
  }

  @Override
  default boolean replace(K key, V oldValue, V newValue) {
    return getOriginal().replace(key, oldValue, newValue);
  }

  @Override
  default V replace(K key, V value) {
    return getOriginal().replace(key, value);
  }

  @Override
  default V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
    return getOriginal().computeIfAbsent(key, mappingFunction);
  }

  @Override
  default V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return getOriginal().computeIfPresent(key, remappingFunction);
  }

  @Override
  default V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
    return getOriginal().compute(key, remappingFunction);
  }

  @Override
  default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
    return getOriginal().merge(key, value, remappingFunction);
  }

}
