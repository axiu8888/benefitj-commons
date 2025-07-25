package com.benefitj.core;

import com.benefitj.core.executable.Instantiator;
import com.benefitj.core.functions.IConsumer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 反射工具
 */
public class ReflectUtils {

  public static final Predicate<Field> NOT_STATIC_FINAL = f ->
      !(Modifier.isStatic(f.getModifiers()) || Modifier.isFinal(f.getModifiers()));

  public static final Predicate<Field> NOT_STATIC_FINAL_VOLATILE = f ->
      NOT_STATIC_FINAL.test(f) || !Modifier.isVolatile(f.getModifiers());

  /**
   * 判断是否被static和final修饰
   *
   * @param member Member类型
   * @return 返回判断结果
   */
  public static boolean isStaticFinal(Member member) {
    return isStaticFinal(member.getModifiers());
  }

  /**
   * 判断是否被static和final修饰
   *
   * @param modifiers 修饰符
   * @return 返回判断结果
   */
  public static boolean isStaticFinal(int modifiers) {
    return Modifier.isStatic(modifiers) && Modifier.isFinal(modifiers);
  }

  /**
   * 判断是否被static或final修饰
   *
   * @param member Member类型
   * @return 返回判断结果
   */
  public static boolean isStaticOrFinal(Member member) {
    return isStaticOrFinal(member.getModifiers());
  }

  /**
   * 判断是否被static或final修饰
   *
   * @param modifiers 修饰符
   * @return 返回判断结果
   */
  public static boolean isStaticOrFinal(int modifiers) {
    return Modifier.isStatic(modifiers) || Modifier.isFinal(modifiers);
  }

  /**
   * 是否被注解注释
   *
   * @param target     检车对象(Class/Method/Field/Constructor)
   * @param annotation 注解
   * @return 返回结果
   */
  public static boolean isAnnotationPresent(Object target, Class<? extends Annotation> annotation) {
    if (target instanceof AnnotatedElement) {
      return ((AnnotatedElement) target).isAnnotationPresent(annotation);
    }
    return target.getClass().isAnnotationPresent(annotation);
  }

  /**
   * 是否被注解注释
   *
   * @param element     Class、Field、Method
   * @param annotations 注解
   * @return 返回是否被注释
   */
  public static boolean isAnyAnnotationPresent(AnnotatedElement element,
                                               Collection<Class<? extends Annotation>> annotations) {
    return isAnnotationPresent(element, annotations, false);
  }

  /**
   * 是否被注解注释
   *
   * @param element     Class、Field、Method
   * @param annotations 注解
   * @return 返回是否被注释
   */
  public static boolean isAllAnnotationPresent(AnnotatedElement element,
                                               Collection<Class<? extends Annotation>> annotations) {
    return isAnnotationPresent(element, annotations, true);
  }

  /**
   * 是否被注解注释
   *
   * @param element     Class、Field、Method
   * @param annotations 注解
   * @return 返回是否被注释
   */
  public static boolean isAnnotationPresent(AnnotatedElement element,
                                            Class<? extends Annotation>[] annotations,
                                            boolean allMatches) {
    return isAnnotationPresent(element, Arrays.asList(annotations), allMatches);
  }

  /**
   * 是否被注解注释
   *
   * @param element     Class、Field、Method
   * @param annotations 注解
   * @return 返回是否被注释
   */
  public static boolean isAnnotationPresent(AnnotatedElement element,
                                            Collection<Class<? extends Annotation>> annotations,
                                            boolean allMatches) {
    if (annotations != null && !annotations.isEmpty()) {
      for (Class<? extends Annotation> annotation : annotations) {
        if (allMatches) {
          if (!element.isAnnotationPresent(annotation)) {
            return false;
          }
        } else {
          if (element.isAnnotationPresent(annotation)) {
            return true;
          }
        }
      }
    }
    return false;
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
                return realClass;
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
   * @param ao 可访问对象
   */
  public static <T extends AccessibleObject> T setAccessible(T ao) {
    return setAccessible(ao, true);
  }

  /**
   * 设置是否可以访问
   *
   * @param ao   可访问对象
   * @param flag 是否可以访问
   */
  public static <T extends AccessibleObject> T setAccessible(T ao, boolean flag) {
    if (ao != null) {
      ao.setAccessible(flag);
    }
    return ao;
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
  public static <T> void find(final Class<?> type,
                              final Function<Class<?>, T[]> call,
                              final Predicate<T> filter,
                              final Consumer<T> consumer,
                              final Predicate<T> interceptor) {
    find(type, call, filter, consumer, interceptor, true, true);
  }

  /**
   * 迭代Class
   *
   * @param type            类
   * @param call            *
   * @param filter          过滤器 -> 返回true表示符合要求，需要处理
   * @param consumer        消费者
   * @param interceptor     拦截器 -> 返回true表示停止循环
   * @param superclass      是否继续迭代父类
   * @param fromTopToBottom 是否从上往下查找
   */
  public static <T> void find(final Class<?> type,
                              final Function<Class<?>, T[]> call,
                              final Predicate<T> filter,
                              final Consumer<T> consumer,
                              final Predicate<T> interceptor,
                              boolean superclass,
                              boolean fromTopToBottom) {
    if (type == null || type == Object.class) {
      return;
    }

    List<Class<?>> classes = new LinkedList<>();
    Class<?> cls = type;
    do {
      classes.add(cls);
      if (superclass) {
        cls = cls.getSuperclass();
      } else {
        cls = Object.class;
      }
    } while (cls != null && cls != Object.class);
    if (fromTopToBottom) {
      Collections.reverse(classes);
    }

    for (Class<?> klass : classes) {
      T[] ts = call.apply(klass);
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
    }
  }

  /**
   * 处理字段，如果出现
   *
   * @param klass     类
   * @param fieldName 字段名
   * @param consumer  处理
   */
  public static void opFieldIfPresent(Class<?> klass, String fieldName, IConsumer<Field> consumer) {
    opFieldIfPresent(klass, f -> f.getName().equalsIgnoreCase(fieldName), consumer);
  }

  /**
   * 处理字段，如果出现
   *
   * @param klass    类
   * @param matcher  匹配器
   * @param consumer 处理
   */
  public static void opFieldIfPresent(Class<?> klass, Predicate<Field> matcher, IConsumer<Field> consumer) {
    Field field = findFirstField(klass, matcher);
    if (field != null) {
      try {
        consumer.accept(field);
      } catch (Exception e) {
        throw new IllegalStateException(CatchUtils.findRoot(e));
      }
    }
  }

  /**
   * 迭代 field
   *
   * @param type     类
   * @param filter   过滤器，过滤出匹配的字段
   * @param consumer 消费者，处理接收到的结果
   */
  public static void findFields(Class<?> type,
                                Predicate<Field> filter,
                                Consumer<Field> consumer) {
    findFields(type, filter, consumer, f -> false);
  }

  /**
   * 迭代 field
   *
   * @param type     类
   * @param filter   过滤器，过滤出匹配的字段
   * @param consumer 消费者，处理接收到的结果
   */
  public static void findFields(Class<?> type,
                                Predicate<Field> filter,
                                Consumer<Field> consumer,
                                Predicate<Field> interceptor) {
    findFields(type, filter, consumer, interceptor, true, false);
  }

  /**
   * 迭代 field
   *
   * @param type            类
   * @param filter          过滤器
   * @param consumer        消费者
   * @param interceptor     拦截器
   * @param superclass      是否继续迭代父类
   * @param fromTopToBottom 是否从上往下查找
   */
  public static void findFields(Class<?> type,
                                Predicate<Field> filter,
                                Consumer<Field> consumer,
                                Predicate<Field> interceptor,
                                boolean superclass,
                                boolean fromTopToBottom) {
    find(type, Class::getDeclaredFields, filter, consumer, interceptor, superclass, fromTopToBottom);
  }

  /**
   * 迭代字段，并返回处理后的结果集
   *
   * @param type           类型
   * @param filter         过滤器，过滤出匹配的字段
   * @param mappedFunction 处理Field，并返回结果
   * @param <T>            类型
   * @return 返回处理后的结果集
   */
  public static <T> List<T> getFields(Class<?> type, Predicate<Field> filter, Function<Field, T> mappedFunction) {
    final List<T> list = new LinkedList<>();
    findFields(type
        , filter
        , f -> list.add(mappedFunction.apply(f))
        , f -> false
    );
    return list;
  }

  /**
   * 迭代字段，并返回处理后的结果集
   *
   * @param type   类型
   * @param filter 过滤器，过滤出匹配的字段
   * @return 返回处理后的结果集
   */
  public static Map<String, Field> getFieldMap(Class<?> type, Predicate<Field> filter) {
    final Map<String, Field> fieldMap = new LinkedHashMap<>();
    findFields(type
        , filter
        , f -> fieldMap.putIfAbsent(f.getName(), f)
        , f -> false
    );
    return fieldMap;
  }

  /**
   * 获取某个字段
   *
   * @param type 类型
   * @param name 字段名
   * @return 返回获取的字段对象
   */
  @Nullable
  public static Field findFirstField(Class<?> type, String name) {
    return findFirstField(type, f -> f.getName().equals(name));
  }

  /**
   * 查找第一个匹配的字段
   *
   * @param type    类型
   * @param matcher 匹配
   * @return 返回获取的字段对象
   */
  @Nullable
  public static Field findFirstField(Class<?> type, Predicate<Field> matcher) {
    final AtomicReference<Field> ref = new AtomicReference<>();
    findFields(type, matcher, ref::set, f -> ref.get() != null);
    return ref.get();
  }

  /**
   * 获取字段的类型
   *
   * @param field 字段
   * @param obj   对象
   * @return 返回字段的类型
   */
  @Nullable
  public static Class<?> getFieldOfType(Field field, @Nullable Object obj) {
    Type genericType = field.getGenericType();
    if (genericType instanceof TypeVariable) {
      return (Class<?>) ((TypeVariable) genericType).getBounds()[0];
    } else if (genericType instanceof ParameterizedType) {
      return (Class<?>) ((ParameterizedType) genericType).getRawType();
    } else {
      return (Class<?>) genericType;
    }
  }

  /**
   * 获取字段的值
   *
   * @param obj     原对象
   * @param matcher 匹配器
   * @param <V>     值类型
   * @return 返回获取到的值
   */
  public static <V> V getFieldValue(Object obj, Predicate<Field> matcher) {
    if (obj == null) {
      return null;
    }
    Field field = findFirstField(obj.getClass(), matcher);
    return field != null ? getFieldValue(field, obj) : null;
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
  public static boolean setFieldValue(String field, Object obj, Object value) {
    Class<?> klass = obj.getClass();
    Field f = findFirstField(klass, field);
    if (f != null) {
      return setFieldValue(f, obj, value);
    }
    return false;
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
    if (field != null) {
      try {
        setAccessible(field, true);
        field.set(obj, value);
        return true;
      } catch (IllegalAccessException ignore) {/* ~ */}
    }
    return false;
  }

  /**
   * 设置字段的值
   *
   * @param obj     对象
   * @param value   值
   * @param matcher 匹配
   * @return 返回是否设置成功
   */
  public static boolean setFieldValue(Object obj, Object value, Predicate<Field> matcher) {
    Field filed = findFirstField(obj.getClass(), matcher);
    return setFieldValue(filed, obj, value);
  }

  /**
   * 处理方法，如果出现
   *
   * @param klass      类
   * @param methodName 方法名
   * @param consumer   处理
   */
  public static void opMethodIfPresent(Class<?> klass, String methodName, IConsumer<Method> consumer) {
    opMethodIfPresent(klass, m -> m.getName().equalsIgnoreCase(methodName), consumer);
  }

  /**
   * 处理方法，如果出现
   *
   * @param klass    类
   * @param matcher  匹配器
   * @param consumer 处理
   */
  public static void opMethodIfPresent(Class<?> klass, Predicate<Method> matcher, IConsumer<Method> consumer) {
    Method m = findFirstMethod(klass, matcher);
    if (m != null) {
      try {
        consumer.accept(m);
      } catch (Exception e) {
        throw new IllegalStateException(CatchUtils.findRoot(e));
      }
    }
  }

  /**
   * 迭代 method
   *
   * @param type     类
   * @param filter   过滤器
   * @param consumer 处理器
   */
  public static void findMethods(Class<?> type,
                                 Predicate<Method> filter,
                                 Consumer<Method> consumer) {
    findMethods(type, filter, consumer, m -> false);
  }

  /**
   * 迭代 method
   *
   * @param type        类
   * @param filter      过滤器
   * @param consumer    处理器
   * @param interceptor 拦截器
   */
  public static void findMethods(Class<?> type,
                                 Predicate<Method> filter,
                                 Consumer<Method> consumer,
                                 Predicate<Method> interceptor) {
    findMethods(type, filter, consumer, interceptor, true, false);
  }

  /**
   * 迭代 method
   *
   * @param type            类
   * @param filter          过滤器
   * @param consumer        处理器
   * @param interceptor     拦截器
   * @param superclass      是否继续迭代父类
   * @param fromTopToBottom 是否从上往下查找
   */
  public static void findMethods(Class<?> type,
                                 Predicate<Method> filter,
                                 Consumer<Method> consumer,
                                 Predicate<Method> interceptor,
                                 boolean superclass,
                                 boolean fromTopToBottom) {
    find(type, Class::getDeclaredMethods, filter, consumer, interceptor, superclass, fromTopToBottom);
  }

  /**
   * 迭代方法，并返回处理的集合
   *
   * @param type     类型
   * @param filter   过滤器，过滤出匹配的方法
   * @param function 处理Method，并返回结果
   * @param <T>      类型
   * @return 返回处理后的结果集
   */
  public static <T> List<T> getMethods(Class<?> type, Predicate<Method> filter, Function<Method, T> function) {
    final List<T> list = new LinkedList<>();
    findMethods(type
        , filter
        , m -> list.add(function.apply(m))
        , m -> false
    );
    return list;
  }

  /**
   * 获取 method
   *
   * @param type 类
   * @param name 方法名
   * @return 返回获取到的Method
   */
  public static Method findFirstMethod(Class<?> type, String name) {
    return findFirstMethod(type, m -> m.getName().equals(name));
  }

  /**
   * 获取 method
   *
   * @param type    类
   * @param matcher 匹配器
   * @return 返回获取到的Method
   */
  public static Method findFirstMethod(Class<?> type, @Nonnull Predicate<Method> matcher) {
    final AtomicReference<Method> ref = new AtomicReference<>();
    findMethods(type, matcher, ref::set, m -> ref.get() != null);
    return ref.get();
  }

  /**
   * 获取 method
   *
   * @param type            类
   * @param name            方法名
   * @param parametersTypes 参数类型
   * @return 返回获取到的Method
   */
  public static Method findFirstMethod(Class<?> type, String name, Class<?>[] parametersTypes) {
    if (type != Object.class) {
      try {
        return type.getDeclaredMethod(name, parametersTypes);
      } catch (NoSuchMethodException e) {
        return findFirstMethod(type.getSuperclass(), name, parametersTypes);
      }
    }
    return null;
  }

  /**
   * 获取 method
   *
   * @param type           类
   * @param annotationType 注解类型
   * @return 返回 methods
   */
  public static List<Method> getMethods(Class<?> type, Class<? extends Annotation> annotationType) {
    return getMethods(type, m -> m.isAnnotationPresent(annotationType));
  }

  /**
   * 获取 method
   *
   * @param type    类
   * @param matcher 匹配器
   * @return 返回 methods
   */
  public static List<Method> getMethods(Class<?> type, @Nullable Predicate<Method> matcher) {
    final LinkedList<Method> methods = new LinkedList<>();
    findMethods(type, matcher, methods::add, m -> false);
    return methods;
  }

  /**
   * 获取 get method
   *
   * @param type 类
   * @return 返回 methods
   */
  public static List<Method> getGetterMethods(Class<?> type) {
    return getMethods(type, ReflectUtils::isGetterMethod);
  }

  /**
   * 获取 method
   *
   * @param type 类
   * @return 返回 methods
   */
  public static List<Method> getSetterMethods(Class<?> type) {
    return getMethods(type, ReflectUtils::isSetterMethod);
  }

  /**
   * 是否为 get 方法
   *
   * @param m 方法
   * @return 返回是否为 get 方法
   */
  public static boolean isGetterMethod(Method m) {
    if (m.getReturnType() != void.class && m.getParameterCount() == 0) {
      return m.getName().startsWith("get") || m.getName().startsWith("is");
    }
    return false;
  }

  /**
   * 是否为 set 方法
   *
   * @param m 方法
   * @return 返回是否为 set 方法
   */
  public static boolean isSetterMethod(Method m) {
    return m.getParameterCount() == 1 && m.getName().startsWith("set");
  }

  /**
   * 调用方法
   *
   * @param obj    对象
   * @param method 方法
   * @param args   参数
   * @param <T>    返回值类型
   * @return 返回返回值
   */
  public static <T> T invoke(Object obj, Method method, Object... args) {
    try {
      if (method.isDefault()) return invokeDefault(obj, method, args);
      setAccessible(method, true);
      return (T) method.invoke(obj, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(CatchUtils.findRoot(e));
    }
  }

  /**
   * 调用方法
   *
   * @param obj     对象
   * @param matcher 匹配
   * @param args    参数
   * @param <T>     返回值类型
   * @return 返回返回值
   */
  public static <T> T invoke(Object obj, Predicate<Method> matcher, Object... args) {
    Method method = findFirstMethod(obj.getClass(), matcher);
    return method != null ? invoke(obj, method, args) : null;
  }

  /**
   * 调用接口的默认方法
   *
   * @param obj    对象
   * @param method 方法
   * @param args   参数
   * @param <T>    返回值类型
   * @return 返回返回值
   */
  public static <T> T invokeDefault(MethodHandles.Lookup lookup, Object obj, Method method, Object... args) {
    try {
      MethodHandle methodHandle = lookup.unreflectSpecial(method, method.getDeclaringClass()).bindTo(obj);
      return invokeMethodHandle(methodHandle, args);
    } catch (Throwable e) {
      throw new IllegalStateException(getFullClassMethod(method) + " 调用失败, cause: " + e.getMessage());
    }
  }

  /**
   * 调用接口的默认方法
   *
   * @param obj    对象
   * @param method 方法
   * @param args   参数
   * @param <T>    返回值类型
   * @return 返回返回值
   */
  public static <T> T invokeDefault(Object obj, Method method, Object... args) {
    if (!method.isDefault()) throw new IllegalArgumentException("不是默认方法!");
    try {
      MethodHandle methodHandle = DefaultMethods.lookupMethodHandle(method).bindTo(obj);
      return invokeMethodHandle(methodHandle, args);
    } catch (Throwable e) {
      throw new IllegalStateException(getFullClassMethod(method) + " 调用失败, cause: " + e.getMessage());
    }
  }

  public static <T> T invokeMethodHandle(MethodHandle methodHandle, Object[] args) throws Throwable {
    return (T) (args != null
        ? methodHandle.invokeWithArguments(args)
        : methodHandle.invoke()
    );
  }

  /**
   * 加载类
   *
   * @param className 类
   * @param <T>       类
   * @return 返回加载的类
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> classForName(String className) {
    return classForName(className, true);
  }

  /**
   * 加载类
   *
   * @param className 类
   * @param throwing  是否抛出异常
   * @param <T>       类
   * @return 返回加载的类
   */
  @SuppressWarnings("unchecked")
  public static <T> Class<T> classForName(String className, boolean throwing) {
    try {
      return (Class<T>) Class.forName(className);
    } catch (Exception e) {
      if (throwing) throw new IllegalStateException(CatchUtils.findRoot(e));
      else return null;
    }
  }

  /**
   * 创建对象实例
   *
   * @param klass 类
   * @param args  参数
   * @param <T>   类型
   * @return 返回对象实例
   */
  public static <T> T newInstance(String klass, Object... args) {
    return newInstance(classForName(klass), args);
  }

  /**
   * 创建对象实例
   *
   * @param klass 类
   * @param args  参数
   * @param <T>   类型
   * @return 返回对象实例
   */
  public static <T> T newInstance(Class<T> klass, Object... args) {
    return Instantiator.get().create(klass, args);
  }

  /**
   * 创建Lookup对象
   *
   * @param method 方法
   * @return 返回创建的对象
   */
  public static MethodHandles.Lookup newLookup(Method method) {
    Method newLookup = findFirstMethod(MethodHandles.Lookup.class, "newLookup", new Class[]{Class.class, Class.class, int.class});
    if (newLookup != null && Modifier.isStatic(newLookup.getModifiers())) {
      return invoke(null, newLookup, new Object[]{method.getDeclaringClass(), null
          , MethodHandles.Lookup.PRIVATE | MethodHandles.Lookup.PROTECTED | MethodHandles.Lookup.PACKAGE | MethodHandles.Lookup.PUBLIC});
    }
    return newInstance(MethodHandles.Lookup.class, method.getDeclaringClass());
  }

  /**
   * 检查参数是否匹配
   *
   * @param parameterTypes 参数类型
   * @param args           参数
   * @return 返回校验结果
   */
  public static boolean isParameterTypesMatch(Class<?>[] parameterTypes, Object[] args) {
    if (parameterTypes != null && args != null) {
      if (parameterTypes.length != args.length) {
        return false;
      }
      for (int i = 0; i < parameterTypes.length; i++) {
        if (args[i] != null && !parameterTypes[i].isInstance(args[i])) {
          return false;
        }
      }
      return true;
    }
    return parameterTypes == null && (args == null || args.length == 0);
  }

  /**
   * 查找匹配的参数值
   *
   * @param argType 要求的类型
   * @param args    参数对象
   * @return 返回符合的参数值
   */
  public static Object findMatch(Class<?> argType, Object[] args) {
    return findMatch(argType, args, 0);
  }

  /**
   * 查找匹配的参数值
   *
   * @param argType 参数的类型
   * @param args    参数对象
   * @param start   开始的位置
   * @return 返回符合的参数值
   */
  public static Object findMatch(Class<?> argType, Object[] args, int start) {
    return findMatch(args, start, (arg, position) -> argType.isInstance(arg));
  }

  /**
   * 查找匹配的参数值
   *
   * @param args  参数对象
   * @param start 开始的位置
   * @return 返回符合的参数值
   */
  public static Object findMatch(Object[] args, int start, FindMatcher matcher) {
    if (start <= args.length) {
      for (int i = start; i < args.length; i++) {
        if (matcher.match(args[i], i)) {
          return args[i];
        }
      }
    }
    return null;
  }

  /**
   * 获取方法名和参数值
   *
   * @param parameters 参数
   * @param values     值
   * @return 返回转换的Map
   */
  public static Map<String, Object> getParameterValues(Parameter[] parameters, Object[] values) {
    Map<String, Object> map = new LinkedHashMap<>();
    for (int i = 0; i < parameters.length; i++) {
      map.put(parameters[i].getName(), values[i]);
    }
    return map;
  }

  public static String getClassMethodName(Method method) {
    return method.getDeclaringClass().getName() + "." + method.getName();
  }

  public static String getFullClassMethod(Method method) {
    String parameters = "";
    if (method.getParameters().length > 0) {
      parameters = Stream.of(method.getParameters()).map(p -> p.getType() + " " + p.getName()).collect(Collectors.joining(", "));
    }
    return getClassMethodName(method) + "(" + parameters + ")";
  }

  /**
   * 查找 getter 方法
   *
   * @param field 字段
   * @param clazz 类
   * @return 获取方法
   */
  public static Method getGetter(Field field, Class<?> clazz) {
    return getGetter(field.getName(), clazz);
  }

  /**
   * 查找 getter 方法
   *
   * @param field 字段
   * @param clazz 类
   * @return 获取方法
   */
  public static Method getGetter(String field, Class<?> clazz) {
    return findMethod(field, "get", clazz);
  }

  /**
   * 查找 setter 方法
   *
   * @param field 字段
   * @param clazz 类
   * @return 获取方法
   */
  public static Method getSetter(Field field, Class<?> clazz) {
    return getSetter(field.getName(), clazz);
  }

  /**
   * 查找 setter 方法
   *
   * @param field 字段
   * @param clazz 类
   * @return 获取方法
   */
  public static Method getSetter(String field, Class<?> clazz) {
    return findMethod(field, "set", clazz);
  }

  /**
   * 查找方法
   *
   * @param field  字段
   * @param prefix 前缀
   * @param clazz  类
   * @return 返回查找到的方法
   */
  public static Method findMethod(Field field, String prefix, Class<?> clazz) {
    return findMethod(field.getName(), prefix, clazz);
  }

  /**
   * 查找方法
   *
   * @param fieldName 字段名
   * @param prefix    前缀
   * @param clazz     类
   * @return 返回查找到的方法
   */
  public static Method findMethod(String fieldName, String prefix, Class<?> clazz) {
    String name = prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    final List<Method> methods = new LinkedList<>();
    findMethods(clazz
        , m -> m.getParameterCount() == 0 && m.getName().equalsIgnoreCase(name)
        , methods::add
        , m -> false
        , true
        , false
    );
    if (methods.isEmpty()) return null;
    if (methods.size() == 1) return methods.get(0);
    for (Method m : methods) {
      if (m.getName().equals(name)) {
        return m;
      }
    }
    return methods.get(0);
  }

  public interface FindMatcher {

    /**
     * 是否匹配
     *
     * @param value    值
     * @param position 下标
     * @return 返回是否匹配
     */
    boolean match(Object value, int position);
  }

}
