package com.benefitj.core;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * 反射工具
 */
public class ReflectUtils {

  /**
   * 判断是否为 Static和final的
   *
   * @param member Member类型
   * @return 返回判断结果
   */
  public static boolean isStaticFinal(Member member) {
    return isStaticFinal(member.getModifiers());
  }

  /**
   * 判断是否为 Static和final的
   *
   * @param modifiers 修饰符
   * @return 返回判断结果
   */
  public static boolean isStaticFinal(int modifiers) {
    return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
  }

  /**
   * 获取参数化类型
   *
   * @param clazz         实现类
   * @param typeParamName 泛型参数名
   * @param <T>           查找的泛型类型
   * @return 返回查找到的泛型类
   */
  public static <T> Class<T> getParameterizedTypeClass(Class<?> clazz, String typeParamName) {
    Class<T> realClass = null;
    Type type = clazz.getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      realClass = (Class<T>) findParameterizedType(((ParameterizedType) type), typeParamName);
    } else {
      if (type == Proxy.class) {
        // 尝试从接口中获取
        Class<?>[] interfaces = clazz.getInterfaces();
        for (Class<?> interfaceClazz : interfaces) {
          for (Type interfaceType : interfaceClazz.getGenericInterfaces()) {
            if (interfaceType instanceof ParameterizedType) {
              realClass = (Class<T>) findParameterizedType((ParameterizedType) interfaceType, typeParamName);
              if (realClass != null) {
                break;
              }
            }
          }
        }
      }
    }
    return realClass;
  }

  /**
   * 查找参数化类型
   *
   * @param parameterizedType 类型对象
   * @param typeParamName     泛型类型名
   * @return 返回查找到的类
   */
  public static Type findParameterizedType(ParameterizedType parameterizedType, String typeParamName) {
    TypeVariable<? extends Class<?>>[] typeParameters = ((Class<?>) parameterizedType.getRawType()).getTypeParameters();
    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
    for (int i = 0; i < actualTypeArguments.length; i++) {
      if (typeParameters[i].getName().equals(typeParamName)) {
        return actualTypeArguments[i];
      }
    }
    return null;
  }

  /**
   * 设置是否可以访问
   *
   * @param ao   可访问对象
   * @param flag 是否可以访问
   */
  public static void setAccessible(AccessibleObject ao, boolean flag) {
    if (ao != null) {
      ao.setAccessible(flag);
    }
  }

  /**
   * 迭代Class
   *
   * @param type        类
   * @param call        *
   * @param filter      过滤器 -> 返回true表示符合要求，需要处理
   * @param consumer    消费者
   * @param interceptor 拦截器 -> 返回true表示停止循环
   */
  public static <T> void foreach(final Class<?> type,
                                 final Function<Class<?>, T[]> call,
                                 final Predicate<T> filter,
                                 final Consumer<T> consumer,
                                 final Predicate<T> interceptor) {
    foreach(type, call, filter, consumer, interceptor, true);
  }

  /**
   * 迭代Class
   *
   * @param type        类
   * @param call        *
   * @param filter      过滤器 -> 返回true表示符合要求，需要处理
   * @param consumer    消费者
   * @param interceptor 拦截器 -> 返回true表示停止循环
   * @param superclass  是否继续迭代父类
   */
  public static <T> void foreach(final Class<?> type,
                                 final Function<Class<?>, T[]> call,
                                 final Predicate<T> filter,
                                 final Consumer<T> consumer,
                                 final Predicate<T> interceptor,
                                 boolean superclass) {
    if (type == null || type == Object.class) {
      return;
    }
    T[] ts = call.apply(type);
    for (T field : ts) {
      if (filter != null) {
        if (filter.test(field)) {
          consumer.accept(field);
        }
      } else {
        consumer.accept(field);
      }
      if (interceptor.test(field)) {
        return;
      }
    }

    if (superclass) {
      foreach(type.getSuperclass(), call, filter, consumer, interceptor, superclass);
    }
  }


  /**
   * 迭代 field
   *
   * @param type        类
   * @param filter      过滤器
   * @param consumer    消费者
   * @param interceptor 拦截器
   */
  public static void foreachField(Class<?> type,
                                  Predicate<Field> filter,
                                  Consumer<Field> consumer,
                                  Predicate<Field> interceptor) {
    foreachField(type, filter, consumer, interceptor, true);
  }

  /**
   * 迭代 method
   *
   * @param type        类
   * @param filter      过滤器
   * @param consumer    处理器
   * @param interceptor 拦截器
   * @param superclass  是否继续迭代父类
   */
  public static void foreachMethod(Class<?> type,
                                   Predicate<Method> filter,
                                   Consumer<Method> consumer,
                                   Predicate<Method> interceptor,
                                   boolean superclass) {
    foreach(type, Class::getDeclaredMethods, filter, consumer, interceptor, superclass);
  }

  /**
   * 迭代 field
   *
   * @param type        类
   * @param filter      过滤器
   * @param consumer    消费者
   * @param interceptor 拦截器
   * @param superclass  是否继续迭代父类
   */
  public static void foreachField(Class<?> type,
                                  Predicate<Field> filter,
                                  Consumer<Field> consumer,
                                  Predicate<Field> interceptor,
                                  boolean superclass) {
    foreach(type, Class::getDeclaredFields, filter, consumer, interceptor, superclass);
  }

  /**
   * 迭代 method
   *
   * @param type        类
   * @param filter      过滤器
   * @param consumer    处理器
   * @param interceptor 拦截器
   */
  public static void foreachMethod(Class<?> type,
                                   Predicate<Method> filter,
                                   Consumer<Method> consumer,
                                   Predicate<Method> interceptor) {
    foreachMethod(type, filter, consumer, interceptor, true);
  }

  /**
   * 是否为泛型字段: 如果字段不为空，判断getType() == getGenericType()
   *
   * @param field 字段
   * @return 如果相等，返回true
   */
  public static boolean isFieldTypeEquals(Field field) {
    return field != null && (field.getType() == field.getGenericType());
  }

  /**
   * 获取某个字段
   *
   * @param type  类型
   * @param field 字段
   * @return 返回获取的字段对象
   */
  @Nullable
  public static Field getField(Class<?> type, String field) {
    if (isNonNull(type, field) && !field.isEmpty() && type != Object.class) {
      try {
        return type.getDeclaredField(field);
      } catch (NoSuchFieldException e) {/* ~ */}
      return getField(type.getSuperclass(), field);
    }
    return null;
  }

  /**
   * 获取字段的类型
   *
   * @param field 字段
   * @param obj   对象
   * @return 返回字段的类型
   */
  @Nullable
  public static Type getFieldOfType(Field field, Object obj) {
    Type genericType = field.getGenericType();
    if (genericType instanceof TypeVariable) {
      return getGenericType(obj.getClass(), 0);
    } else if (genericType instanceof ParameterizedType) {
      return getRawType((ParameterizedType) genericType);
    } else {
      return genericType;
    }
  }

  /**
   * 获取字段的值
   *
   * @param field 字段
   * @param obj   原对象
   * @param <V>   值类型
   * @return 返回获取到的值
   */
  public static <V> V getFieldValue(Field field, Object obj) {
    try {
      setAccessible(field, true);
      return (V) field.get(obj);
    } catch (IllegalAccessException ignore) {/* ~ */}
    return null;
  }

  /**
   * 设置字段的值
   *
   * @param field 字段
   * @param obj   对象
   * @param value 值
   * @return 返回是否设置成功
   */
  public static boolean setFieldValue(Field field, Object obj, Object value) {
    if (field != null && obj != null) {
      try {
        setAccessible(field, true);
        field.set(obj, value);
        return true;
      } catch (IllegalAccessException ignore) {/* ~ */}
    }
    return false;
  }

  /**
   * 获取指定注解的全部属性
   *
   * @param klass           class
   * @param annotationClass 注解
   * @return 返回获取的全部属性
   */
  public static Field getFieldByAnnotation(Class<?> klass,
                                           Class<? extends Annotation> annotationClass) {
    List<Field> fieldList = getFieldByAnnotation(klass, annotationClass, true);
    return fieldList.isEmpty() ? null : fieldList.get(0);
  }

  /**
   * 获取指定注解的全部属性
   *
   * @param klass           class
   * @param annotationClass 注解
   * @return 返回获取的全部属性
   */
  public static List<Field> getFieldByAnnotation(Class<?> klass,
                                                 Class<? extends Annotation> annotationClass,
                                                 boolean first) {
    if (klass == null || klass == Object.class) {
      return Collections.emptyList();
    }

    final List<Field> fields = new LinkedList<>();
    for (Field field : klass.getDeclaredFields()) {
      if (field.isAnnotationPresent(annotationClass)) {
        fields.add(field);
        if (first) {
          return fields;
        }
      }
    }
    List<Field> nextFields = getFieldByAnnotation(klass.getSuperclass(), annotationClass, first);
    fields.addAll(nextFields);
    return fields;
  }

  /**
   * 获取泛型参数，默认返回 null
   *
   * @return 如果是泛型类型，返回类上的泛型数组
   */
  public static Type[] getActualTypeArguments(Type genericSuperclass) {
    return getActualTypeArguments(genericSuperclass, null);
  }

  /**
   * 获取泛型参数
   *
   * @param defaultValues 默认值
   * @return 如果是泛型类型，返回类上的泛型数组
   */
  public static Type[] getActualTypeArguments(Type genericSuperclass, Type[] defaultValues) {
    if (genericSuperclass instanceof ParameterizedType) {
      Type[] arguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
      return arguments != null ? arguments : defaultValues;
    }
    return defaultValues;
  }

  /**
   * 获取某一个位置的泛型参数类型
   *
   * @param index 参数位置
   * @return 返回对应的参数类型
   */
  public static <V> Class<V> getActualType(Type genericSuperclass, int index) {
    Type[] arguments = getActualTypeArguments(genericSuperclass);
    if (arguments != null && arguments.length > index) {
      return (Class<V>) arguments[index];
    }
    return null;
  }

  /**
   * 获取当前类的泛型类型
   *
   * @param clazz 当前类
   * @param index 获取的泛型类型
   * @return 返回对应的泛型类型
   */
  public static Type getGenericType(Class<?> clazz, int index) {
    Type[] params = getActualTypeArguments(clazz.getGenericSuperclass());

    if (index >= params.length || index < 0) {
      return Object.class;
    }

    if (!(params[index] instanceof Class)) {
      return Object.class;
    }

    return params[index];
  }

  /**
   * 获取原类型
   *
   * @param type 原类型
   * @return 返回对应的原类型或Null
   */
  @Nullable
  public static Type getRawType(ParameterizedType type) {
    return type != null ? type.getRawType() : null;
  }

  private static boolean isNotBlank(String s) {
    return s != null && !s.trim().isEmpty();
  }

  /**
   * 不是Class
   *
   * @param o 检查对象
   * @return 不为Class.class对象时返回 true, 否则false
   */
  public static boolean isNotClass(Object o) {
    return o != null && o.getClass() != Class.class;
  }

  private static boolean isNonNull(Object... os) {
    for (Object o : os) {
      if (o == null) {
        return false;
      }
    }
    return true;
  }

}
