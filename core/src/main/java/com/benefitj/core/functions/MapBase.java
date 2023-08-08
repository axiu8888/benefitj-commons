package com.benefitj.core.functions;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class MapBase<K, V> implements Map<K, V> {

  transient Map<K, V> _internal;

  public MapBase() {
    this(new LinkedHashMap<>());
  }

  public MapBase(Map<K, V> map) {
    this._internal = map;
  }

  @Override
  public int size() {
    return _internal.size();
  }

  @Override
  public boolean isEmpty() {
    return _internal.isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return _internal.containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return _internal.containsValue(value);
  }

  @Override
  public V get(Object key) {
    return _internal.get(key);
  }

  @Override
  public V put(K key, V value) {
    return _internal.put(key, value);
  }

  @Override
  public V remove(Object key) {
    return _internal.remove(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    _internal.putAll(m);
  }

  @Override
  public void clear() {
    _internal.clear();
  }

  @Override
  public Set<K> keySet() {
    return _internal.keySet();
  }

  @Override
  public Collection<V> values() {
    return _internal.values();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return _internal.entrySet();
  }

  @Override
  public boolean equals(Object o) {
    return _internal.equals(o);
  }

  @Override
  public int hashCode() {
    return _internal.hashCode();
  }

}
