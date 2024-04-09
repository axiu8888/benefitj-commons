package com.benefitj.interpolator;

import java.util.Collection;
import java.util.LinkedList;

/**
 * 固定长度的列表
 *
 * @param <T>
 */
public class FixedList<T> extends LinkedList<T> {

  final int maxlen;

  public FixedList(int maxlen) {
    this.maxlen = maxlen;
  }

  protected void removeOne(boolean last) {
    if (size() > maxlen) {
      if (last) {
        removeLast();
      } else {
        removeFirst();
      }
    }
  }

  @Override
  public void addFirst(T t) {
    super.addFirst(t);
    removeOne(true); // 移除最后一个
  }

  @Override
  public void addLast(T t) {
    super.addLast(t);
    removeOne(false); // 移除第一个
  }

  @Override
  public boolean add(T t) {
    try {
      return super.add(t);
    } finally {
      removeOne(false); // 默认移除第一个
    }
  }

  @Override
  public void add(int index, T element) {
    try {
      super.add(index, element);
    } finally {
      removeOne(false);
    }
  }

  @Override
  public boolean addAll(Collection<? extends T> c) {
    for (T t : c) {
      add(t);
    }
    return true;
  }

}
