package com.benefitj.core.executable;

import com.benefitj.core.ReflectUtils;
import com.benefitj.core.functions.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * 字段代理
 */
public interface FieldProxy {

  /**
   * 字段
   */
  Field field();

  /**
   * 设置是否优先调用 set/get 方法
   */
  void setMethodFirst(boolean methodFirst);

  /**
   * 是否优先调用 set/get 方法
   */
  boolean isMethodFirst();

  /**
   * 设置值
   *
   * @param target 目标对象
   * @param value  值
   */
  default void set(Object target, Object value) {
    if (isMethodFirst()) {
      Method setter = getSetter();
      if (setter != null) {
        ReflectUtils.invoke(target, setter, value);
        return;
      }
    }
    ReflectUtils.setFieldValue(field(), target, value);
  }

  /**
   * 获取值
   *
   * @param target 目标对象
   * @return 返回值
   */
  default <T> T get(Object target) {
    if (isMethodFirst()) {
      Method getter = getGetter();
      if (getter != null) {
        return ReflectUtils.invoke(target, getter);
      }
    }
    return ReflectUtils.getFieldValue(field(), target);
  }

  /**
   * 获取getter方法
   */
  default Method getGetter() {
    return ReflectUtils.getGetter(field(), field().getDeclaringClass());
  }

  /**
   * 获取setter方法
   */
  default Method getSetter() {
    return ReflectUtils.getSetter(field(), field().getDeclaringClass());
  }

  /**
   * 创建字段代理
   */
  static FieldProxy create(Field field) {
    return new Impl(field);
  }

  /**
   * 创建字段代理
   */
  static FieldProxy create(Field field, boolean methodFirst) {
    return new Impl(field, methodFirst);
  }


  class Impl implements FieldProxy {

    final Field field;
    final Pair<Boolean, Method> setter = Pair.of(false, null);
    final Pair<Boolean, Method> getter = Pair.of(false, null);
    boolean methodFirst = false;

    public Impl(Field field) {
      this(field, true);
    }

    public Impl(Field field, boolean methodFirst) {
      if (field == null) {
        throw new IllegalArgumentException("字段不能为null");
      }
      this.field = field;
      this.methodFirst = methodFirst;
    }

    @Override
    public Field field() {
      return field;
    }

    @Override
    public void setMethodFirst(boolean methodFirst) {
      this.methodFirst = methodFirst;
    }

    @Override
    public boolean isMethodFirst() {
      return methodFirst;
    }

    @Override
    public Method getSetter() {
      return process_$etter(getter, false);
    }

    @Override
    public Method getGetter() {
      return process_$etter(getter, true);
    }


    protected Method process_$etter(Pair<Boolean, Method> holder, boolean isGetter) {
      if (holder.getKey()) return holder.getValue();
      holder.setKey(true);
      holder.setValue(isGetter
          ? ReflectUtils.getGetter(field(), field().getDeclaringClass())
          : ReflectUtils.getSetter(field(), field().getDeclaringClass())
      );
      return holder.getValue();
    }

  }

}
