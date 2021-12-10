package com.benefitj.core.reflection;

import javax.annotation.Nullable;
import java.lang.reflect.*;

public interface FieldDescriptor {

  /**
   * 创建字段类型
   *
   * @param field 字段
   * @return 返回对象
   */
  static FieldDescriptor of(Field field) {
    return new FieldDescriptorImpl(field);
  }

  /**
   * 获取字段
   */
  Field getField();

  /**
   * 设置字段
   *
   * @param field 字段
   */
  void setField(Field field);

  /**
   * 获取字段类型
   */
  default Class<?> getType() {
    return getField().getType();
  }

  /**
   * 获取类型
   */
  default Type getGenericType() {
    return getField().getGenericType();
  }

  /**
   */
  default boolean isAssignableFrom(Class<?> cls) {
    return getType().isAssignableFrom(cls);
  }

  /**
   * 获取实际的字段类型
   *
   * @return 返回实际的类型
   */
  default Class<?> getActualType() {
    return getActualType(null);
  }

  /**
   * 获取实际的字段类型
   *
   * @param obj 字段所在的对象
   * @return 返回实际的类型
   */
  default Class<?> getActualType(@Nullable Object obj) {
    if (isParameterizedType()) {
      return (Class<?>) ((ParameterizedType) getGenericType()).getRawType();
    }
    if (isTypeVariable()) {
      if (obj != null) {
        Object value = getValue(obj);
        if (value != null) {
          return value.getClass();
        }
      }
      return (Class<?>) ((TypeVariable) getGenericType()).getBounds()[0];
    }
    return (Class<?>) getGenericType();
  }

  /**
   * 获取字段的值
   *
   * @param obj 对象
   * @return 返回字段值
   */
  default Object getValue(Object obj) {
    try {
      Field f = getField();
      f.setAccessible(true);
      return f.get(obj);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 设置字段的值
   *
   * @param obj 字段所在对象
   * @param value 被设置的值
   */
  default void setValue(Object obj, Object value) {
    try {
      Field f = getField();
      f.setAccessible(true);
      f.set(obj, value);
    } catch (IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * 判断是否为类型变量，用于创建对象时的辅助类型，
   * 比如：new ArrayList<String>()，这里的String就是创建时的类型，只有在运行时才可以获取
   */
  default boolean isTypeVariable() {
    return getGenericType() instanceof TypeVariable;
  }

  /**
   * 判断是否为参数化的类型，子类继承父类时，子类指定父类泛型的具体类型
   * 比如：
   * class Parent<T> {
   * T id;
   * }
   * <p>
   * class Son extends Parent<String> {}
   * <p>
   * 这里的 String 就是 T 的具体类型
   */
  default boolean isParameterizedType() {
    return getGenericType() instanceof ParameterizedType;
  }

  /**
   * 判断是否为通配符型表达类型，
   * 如 ? ， ? extends Number ，或 ? super Integer
   */
  default boolean isWildcardType() {
    return getGenericType() instanceof WildcardType;
  }

  /**
   * 获取类型名: ParameterizedType、TypeVariable、WildcardType、Class
   */
  default String getTypeName() {
    if (isParameterizedType()) {
      return "ParameterizedType";
    } else if (isTypeVariable()) {
      return "TypeVariable";
    } else if (isWildcardType()) {
      return "WildcardType";
    } else {
      return "Class";
    }
  }


  class FieldDescriptorImpl implements FieldDescriptor {

    private Field field;

    public FieldDescriptorImpl() {
    }

    public FieldDescriptorImpl(Field field) {
      this.field = field;
    }

    @Override
    public Field getField() {
      return field;
    }

    @Override
    public void setField(Field field) {
      this.field = field;
    }

  }
}
