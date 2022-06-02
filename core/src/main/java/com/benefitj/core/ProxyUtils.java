package com.benefitj.core;

import com.benefitj.core.concurrent.ConcurrentHashSet;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ProxyUtils {

  /**
   * 创建List的代理
   *
   * @param interfaceType 接口类型
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newListProxy(Class<T> interfaceType) {
    return newListProxy(interfaceType, Collections.synchronizedList(new ArrayList<>()));
  }

  /**
   * 创建List的代理
   *
   * @param interfaceType 接口类型
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newCopyListProxy(Class<T> interfaceType) {
    return newListProxy(interfaceType, new CopyOnWriteArrayList<>());
  }

  /**
   * 创建List的代理
   *
   * @param interfaceType 接口类型
   * @param list          代理的List
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newListProxy(Class<T> interfaceType, List<?> list) {
    if (interfaceType.isAssignableFrom(List.class)) {
      throw new IllegalStateException("不支持继承自List的接口");
    }
    ClassLoader loader = interfaceType.getClassLoader();
    return (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType, List.class}, (proxy, method, args) -> {
      if (method.getDeclaringClass().isAssignableFrom(List.class)) {
        return ReflectUtils.invoke(list, method, args);
      } else {
        Object value = null;
        for (Object target : list) {
          Object newValue = ReflectUtils.invoke(target, method, args);
          if (newValue != null) {
            value = newValue;
          }
        }
        return getReturnValue(method, value);
      }
    });
  }

  /**
   * 创建Set的代理
   *
   * @param interfaceType 接口类型
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newSetProxy(Class<T> interfaceType) {
    return newSetProxy(interfaceType, new ConcurrentHashSet<>());
  }

  /**
   * 创建Set的代理
   *
   * @param interfaceType 接口类型
   * @param set           代理的Set
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newSetProxy(Class<T> interfaceType, Set<?> set) {
    if (interfaceType.isAssignableFrom(Set.class)) {
      throw new IllegalStateException("不支持继承自List的接口");
    }
    ClassLoader loader = interfaceType.getClassLoader();
    return (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType, Set.class}, (proxy, method, args) -> {
      if (method.getDeclaringClass().isAssignableFrom(Set.class)) {
        return ReflectUtils.invoke(set, method, args);
      } else {
        Object value = null;
        for (Object target : set) {
          Object newValue = ReflectUtils.invoke(target, method, args);
          if (newValue != null) {
            value = newValue;
          }
        }
        return getReturnValue(method, value);
      }
    });
  }

  /**
   * 创建Map的代理
   *
   * @param interfaceType 接口类型
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newMapProxy(Class<T> interfaceType) {
    return newMapProxy(interfaceType, new ConcurrentHashMap());
  }

  /**
   * 创建Map的代理
   *
   * @param interfaceType 接口类型
   * @param map           代理的Map
   * @param <T>           代理类型
   * @return 返回创建的代理
   */
  public static <T> T newMapProxy(Class<T> interfaceType, Map map) {
    if (interfaceType.isAssignableFrom(Map.class)) {
      throw new IllegalStateException("不支持继承自Map的接口");
    }
    ClassLoader loader = interfaceType.getClassLoader();
    return (T) Proxy.newProxyInstance(loader, new Class[]{interfaceType, Map.class}, (proxy, method, args) -> {
      if (method.getDeclaringClass().isAssignableFrom(Map.class)) {
        return ReflectUtils.invoke(map, method, args);
      } else {
        Object value = null;
        for (Object target : map.values()) {
          value = ReflectUtils.invoke(target, method, args);
        }
        return getReturnValue(method, value);
      }
    });
  }

  private static Object getReturnValue(Method method, Object value) {
    if (value == null && method.getReturnType().isPrimitive()) {
      Class<?> returnType = method.getReturnType();
      if (returnType == int.class
          || returnType == long.class
          || returnType == short.class
          || returnType == byte.class) {
        return 0;
      } else if (returnType == float.class) {
        return 0.f;
      } else if (returnType == double.class) {
        return 0.0;
      } else if (returnType == boolean.class) {
        return false;
      } else if (returnType == char.class) {
        return ' ';
      }
    }
    return value;
  }

}
