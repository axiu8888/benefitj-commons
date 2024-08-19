package com.benefitj.core.functions;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

public interface FunctionMap<K, V> extends WrappedMap<K, V> {

  /**
   * 包装
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回包装的Map
   */
  static <K, V> FunctionMap<K, V> hashMap() {
    return wrap(new HashMap<>());
  }

  /**
   * 包装
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回包装的Map
   */
  static <K, V> FunctionMap<K, V> linkedHashMap() {
    return wrap(new LinkedHashMap<>());
  }

  /**
   * 包装
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回包装的Map
   */
  static <K, V> FunctionMap<K, V> treeMap(Comparator<K> comparator) {
    return wrap(new TreeMap<>(comparator));
  }

  /**
   * 包装
   *
   * @param <K> 键
   * @param <V> 值
   * @return 返回包装的Map
   */
  static <K, V> FunctionMap<K, V> concurrentHashMap() {
    return wrap(new ConcurrentHashMap<>());
  }

  /**
   * 包装
   *
   * @param map 原始Map
   * @param <K> 键
   * @param <V> 值
   * @return 返回包装的Map
   */
  static <K, V> FunctionMap<K, V> wrap(Map<K, V> map) {
    return new FunctionMapImpl<>(map);
  }

  default V computeIfAbsent(K key) {
    return map().computeIfAbsent(key, getAbsentFunction());
  }

  default V computeIfPresent(K key) {
    return map().computeIfPresent(key, getPresentFunction());
  }

  default V compute(K key) {
    return map().compute(key, getComputeFunction());
  }

  Function<? super K, ? extends V> getAbsentFunction();

  FunctionMap<K, V> setAbsentFunction(Function<? super K, ? extends V> absentFunction);

  BiFunction<? super K, ? super V, ? extends V> getPresentFunction();

  FunctionMap<K, V> setPresentFunction(BiFunction<? super K, ? super V, ? extends V> presentFunction);

  BiFunction<? super K, ? super V, ? extends V> getComputeFunction();

  FunctionMap<K, V> setComputeFunction(BiFunction<? super K, ? super V, ? extends V> computeFunction);


  class FunctionMapImpl<K, V> implements FunctionMap<K, V> {

    private final Map<K, V> map;

    private Function<? super K, ? extends V> absentFunction;
    private BiFunction<? super K, ? super V, ? extends V> presentFunction;
    private BiFunction<? super K, ? super V, ? extends V> computeFunction;

    public FunctionMapImpl(Map<K, V> map) {
      this.map = map;
    }

    @Override
    public Map<K, V> map() {
      return map;
    }

    @Override
    public Function<? super K, ? extends V> getAbsentFunction() {
      return absentFunction;
    }

    @Override
    public FunctionMap<K, V> setAbsentFunction(Function<? super K, ? extends V> absentFunction) {
      this.absentFunction = absentFunction;
      return this;
    }

    @Override
    public BiFunction<? super K, ? super V, ? extends V> getPresentFunction() {
      return presentFunction;
    }

    @Override
    public FunctionMap<K, V> setPresentFunction(BiFunction<? super K, ? super V, ? extends V> presentFunction) {
      this.presentFunction = presentFunction;
      return this;
    }

    @Override
    public BiFunction<? super K, ? super V, ? extends V> getComputeFunction() {
      return computeFunction;
    }

    @Override
    public FunctionMap<K, V> setComputeFunction(BiFunction<? super K, ? super V, ? extends V> computeFunction) {
      this.computeFunction = computeFunction;
      return this;
    }
  }

}
