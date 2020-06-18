package com.benefitj.core.local;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * 缓存Map类型
 *
 * @param <K> 键
 * @param <V> 值
 */
public class LocalMapCache<K, V> implements Map<K, V> {

  public static final BiFunction DEFAULT_COMPUTE = (k, v) -> v;
  public static final Function DEFAULT_ABSENT = (k) -> null;
  public static final BiFunction DEFAULT_PRESENT = (k, v) -> v;

  /**
   * Thread缓存
   */
  private final LocalCache<Map<K, V>> localCache;

  private BiFunction<? super K, ? super V, ? extends V> computeFunction = DEFAULT_COMPUTE;
  private Function<? super K, ? extends V> absentFunction = DEFAULT_ABSENT;
  private BiFunction<? super K, ? super V, ? extends V> presentFunction = DEFAULT_PRESENT;

  public LocalMapCache() {
    this.localCache = new ThreadLocalCache<>();
  }

  public LocalMapCache(InitialCallback<Map<K, V>> initialValueCall) {
    this();
    this.setInitialCallback(initialValueCall);
  }

  public LocalMapCache(InitialCallback<Map<K, V>> initialValueCall, Function<K, V> absentFunction) {
    this();
    this.setInitialCallback(initialValueCall);
    this.setAbsentFunction(absentFunction);
  }

  public final LocalMapCache<K, V> setInitialCallback(InitialCallback<Map<K, V>> callback) {
    getLocalCache().setInitialCallback(callback);
    return this;
  }

  protected LocalCache<Map<K, V>> getLocalCache() {
    LocalCache<Map<K, V>> cache = this.localCache;
    if (cache == null) {
      throw new IllegalStateException("Please call setInitialCallback(callback) .");
    }
    return cache;
  }

  public BiFunction<? super K, ? super V, ? extends V> getComputeFunction() {
    return computeFunction;
  }

  public LocalMapCache<K, V> setComputeFunction(BiFunction<? super K, ? super V, ? extends V> computeFunction) {
    this.computeFunction = computeFunction != null ? computeFunction : DEFAULT_COMPUTE;
    return this;
  }

  public Function<? super K, ? extends V> getAbsentFunction() {
    return absentFunction;
  }

  public LocalMapCache<K, V> setAbsentFunction(Function<? super K, ? extends V> absentFunction) {
    this.absentFunction = absentFunction != null ? absentFunction : DEFAULT_ABSENT;
    return this;
  }

  public BiFunction<? super K, ? super V, ? extends V> getPresentFunction() {
    return presentFunction;
  }

  public LocalMapCache<K, V> setPresentFunction(BiFunction<? super K, ? super V, ? extends V> presentFunction) {
    this.presentFunction = presentFunction != null ? presentFunction : DEFAULT_PRESENT;
    return this;
  }

  public Map<K, V> getMap() {
    return getLocalCache().get();
  }

  @Override
  public int size() {
    return getMap().size();
  }

  @Override
  public boolean isEmpty() {
    return getMap().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return getMap().containsKey(key);
  }

  @Override
  public boolean containsValue(Object value) {
    return getMap().containsKey(value);
  }

  @Override
  public V get(Object key) {
    return getMap().get(key);
  }

  @Override
  public V put(K key, V value) {
    return getMap().put(key, value);
  }

  @Override
  public V remove(Object key) {
    return getMap().remove(key);
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    getMap().putAll(m);
  }

  @Override
  public void clear() {
    getMap().clear();
  }

  @Override
  public Set<K> keySet() {
    return getMap().keySet();
  }

  @Override
  public Collection<V> values() {
    return getMap().values();
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return getMap().entrySet();
  }

  public V compute(K key) {
    return compute(key, computeFunction);
  }

  public V computeIfAbsent(K key) {
    return computeIfAbsent(key, absentFunction);
  }

  public V computeIfPresent(K key) {
    return computeIfPresent(key, presentFunction);
  }
}

