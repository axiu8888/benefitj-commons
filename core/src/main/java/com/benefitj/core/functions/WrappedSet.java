package com.benefitj.core.functions;

import com.benefitj.core.concurrent.ConcurrentHashSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;

public interface WrappedSet<T> extends Set<T> {

  Set<T> source();

  @Override
  default int size() {
    return source().size();
  }

  @Override
  default boolean isEmpty() {
    return source().isEmpty();
  }

  @Override
  default boolean contains(Object o) {
    return source().contains(o);
  }

  @Override
  default Iterator<T> iterator() {
    return source().iterator();
  }

  @Override
  default Object[] toArray() {
    return source().toArray();
  }

  @Override
  default <T1> T1[] toArray(T1[] a) {
    return source().toArray(a);
  }

  @Override
  default boolean add(T t) {
    return source().add(t);
  }

  @Override
  default boolean remove(Object o) {
    return source().remove(o);
  }

  @Override
  default boolean containsAll(Collection<?> c) {
    return source().containsAll(c);
  }

  @Override
  default boolean addAll(Collection<? extends T> c) {
    return source().addAll(c);
  }

  @Override
  default boolean retainAll(Collection<?> c) {
    return source().retainAll(c);
  }

  @Override
  default boolean removeAll(Collection<?> c) {
    return source().removeAll(c);
  }

  @Override
  default void clear() {
    source().clear();
  }

  @Override
  default Spliterator<T> spliterator() {
    return source().spliterator();
  }

  static <T> Set<T> newConcurrentHashSet() {
    return newSet(new  ConcurrentHashSet<>());
  }

  static <T> Set<T> newSet(Set<T> source) {
    return (WrappedSet<T>) () -> source;
  }

}
