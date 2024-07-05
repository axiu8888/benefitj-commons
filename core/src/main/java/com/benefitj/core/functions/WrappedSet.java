package com.benefitj.core.functions;

import com.benefitj.core.concurrent.ConcurrentHashSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

public interface WrappedSet<T> extends Set<T> {

  Set<T> set();

  @Override
  default int size() {
    return set().size();
  }

  @Override
  default boolean isEmpty() {
    return set().isEmpty();
  }

  @Override
  default boolean contains(Object o) {
    return set().contains(o);
  }

  @Override
  default Iterator<T> iterator() {
    return set().iterator();
  }

  @Override
  default Object[] toArray() {
    return set().toArray();
  }

  @Override
  default <T1> T1[] toArray(T1[] a) {
    return set().toArray(a);
  }

  @Override
  default boolean add(T t) {
    return set().add(t);
  }

  @Override
  default boolean remove(Object o) {
    return set().remove(o);
  }

  @Override
  default boolean containsAll(Collection<?> c) {
    return set().containsAll(c);
  }

  @Override
  default boolean addAll(Collection<? extends T> c) {
    return set().addAll(c);
  }

  @Override
  default boolean retainAll(Collection<?> c) {
    return set().retainAll(c);
  }

  @Override
  default boolean removeAll(Collection<?> c) {
    return set().removeAll(c);
  }

  @Override
  default void clear() {
    set().clear();
  }

  @Override
  default Spliterator<T> spliterator() {
    return set().spliterator();
  }

  static <T> Set<T> newConcurrentHashSet() {
    return newSet(new  ConcurrentHashSet<>());
  }

  static <T> Set<T> newSet(Set<T> set) {
    return (WrappedSet<T>) () -> set;
  }

}
