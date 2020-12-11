package com.benefitj.core.concurrent;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ConcurrentHashSet
 *
 * @param <E>
 */
public class ConcurrentHashSet<E> extends AbstractSet<E> {

  private final Map<E, Boolean> map = new ConcurrentHashMap<>();

  public ConcurrentHashSet() {
    super();
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean contains(Object o) {
    return map.containsKey(o);
  }

  @Override
  public Iterator<E> iterator() {
    return map.keySet().iterator();
  }

  @Override
  public boolean add(E o) {
    return map.putIfAbsent(o, Boolean.TRUE) == null;
  }

  @Override
  public boolean remove(Object o) {
    return Boolean.TRUE.equals(map.remove(o));
  }

  @Override
  public void clear() {
    map.clear();
  }
}

