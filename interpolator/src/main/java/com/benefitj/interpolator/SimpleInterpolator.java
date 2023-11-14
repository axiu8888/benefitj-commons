package com.benefitj.interpolator;

import com.benefitj.core.ReflectUtils;

import java.lang.reflect.Array;

/**
 * 通用插值器
 *
 * @param <T> 数组类型
 */
public abstract class SimpleInterpolator<T> extends ArrayInterpolator<T, T, T> {

  Class<T> type;

  public SimpleInterpolator(int srcFrequency, int destFrequency) {
    this(srcFrequency, destFrequency, false);
  }

  public SimpleInterpolator(int srcFrequency, int destFrequency, boolean cachedDest) {
    super(srcFrequency, destFrequency, cachedDest);
    this.type = ReflectUtils.getParameterizedTypeClass(getClass(), "T");
  }

  @Override
  protected T createBuffer(int length) {
    return (T) Array.newInstance(type.isArray() ? type.getComponentType() : type, length);
  }

  @Override
  protected T createDest(int destFrequency) {
    return (T) Array.newInstance(type.isArray() ? type.getComponentType() : type, destFrequency);
  }

  @Override
  public void accelerate(T src, T buf, int ratio) {
    int index = 0;
    int length = Array.getLength(src);
    for (int i = 0; i < length; i++) {
      Object v = Array.get(src, i);
      for (int j = 0; j < ratio; j++) {
        Array.set(buf, index++, v);
      }
    }
  }

  @Override
  public void decelerate(T dest, T buf, int ratio) {
    int length = Array.getLength(buf);
    for (int i = 0, j = 0; i < length; i += ratio, j++) {
      Object value = Array.get(buf, i);
      Array.set(dest, j, value);
    }
  }
}
