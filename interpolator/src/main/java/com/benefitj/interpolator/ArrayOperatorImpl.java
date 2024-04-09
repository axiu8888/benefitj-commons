package com.benefitj.interpolator;


import com.benefitj.core.ReflectUtils;

import java.lang.reflect.Array;

/**
 * 数组操作器
 *
 * @param <T> 数组类型
 */
public abstract class ArrayOperatorImpl<T> implements ArrayOperator<T> {

  protected Class<?> type;

  public ArrayOperatorImpl() {
    this.type = ReflectUtils.getParameterizedTypeClass(getClass(), "T");
  }

  public ArrayOperatorImpl(Class<?> type) {
    this.type = type;
  }

  @Override
  public T get(Object array, int index) {
    return (T) Array.get(array, index);
  }

  @Override
  public void set(Object array, int index, T value) {
    Array.set(array, index, value);
  }

  @Override
  public Object newArray(int len) {
    return Array.newInstance(type.getComponentType(), len);
  }

  @Override
  public T add(int index, T v1, T v2) {
    if (PrimitiveOperator.support(type.getComponentType())) {
      return (T) PrimitiveOperator.of(type.getComponentType()).add((Number) v1, (Number) v2);
    }
    return null;
  }

  @Override
  public T subtract(int index, T v1, T v2) {
    if (PrimitiveOperator.support(type.getComponentType())) {
      return (T) PrimitiveOperator.of(type.getComponentType()).subtract((Number) v1, (Number) v2);
    }
    return null;
  }

  @Override
  public T multiply(int index, T v1, T v2) {
    if (PrimitiveOperator.support(type.getComponentType())) {
      return (T) PrimitiveOperator.of(type.getComponentType()).multiply((Number) v1, (Number) v2);
    }
    return null;
  }

  @Override
  public T divide(int index, T v1, T v2) {
    if (PrimitiveOperator.support(type.getComponentType())) {
      return (T) PrimitiveOperator.of(type.getComponentType()).divide((Number) v1, (Number) v2);
    }
    return null;
  }

}
