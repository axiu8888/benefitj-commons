package com.benefitj.core.functions;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Map
 *
 * @author dxa
 */
public abstract class MapBase<K, V> implements WrappedMap<K, V> {

  transient Map<K, V> _internal;

  public MapBase() {
    this(new LinkedHashMap<>());
  }

  public MapBase(Map<K, V> map) {
    this._internal = map;
  }

  @Override
  public Map<K, V> map() {
    return this._internal;
  }

}
