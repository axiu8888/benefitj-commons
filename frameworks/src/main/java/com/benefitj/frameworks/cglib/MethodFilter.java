package com.benefitj.frameworks.cglib;

import javax.annotation.Nullable;
import java.lang.reflect.Method;

public class MethodFilter {

  private static final Class<?>[] EMPTY_TYPES = new Class<?>[0];

  /**
   * 对象类型
   */
  private Class<?> type;
  /**
   * 方法
   */
  private Method method;
  /**
   * 名称
   */
  private String name;
  /**
   * 参数类型
   */
  private Class<?>[] parameterTypes = EMPTY_TYPES;

  public MethodFilter() {
  }

  public MethodFilter(String name, Class<?>[] parameterTypes) {
    this.name = name;
    this.parameterTypes = parameterTypes != null ? parameterTypes : EMPTY_TYPES;
  }

  /**
   * 匹配是否是同一个方法
   *
   * @param obj    对象
   * @param method 方法
   * @param args   参数
   * @return 返回匹配的结果
   */
  public boolean match(@Nullable Object obj, Method method, @Nullable Object[] args) {
    if (method.equals(getMethod())) {
      return true;
    }

    if (method.getName().equals(getName())) {
      Class<?>[] types = getParameterTypes();
      types = types != null ? types : EMPTY_TYPES;
      if (types.length == method.getParameterCount()) {
        for (int i = 0; i < types.length; i++) {
          if (!method.getParameterTypes()[i].isAssignableFrom(types[i])) {
            return false;
          }
        }
        return true;
      }
    }

    return false;
  }


  public Class<?> getType() {
    return type;
  }

  public MethodFilter setType(Class<?> type) {
    this.type = type;
    return this;
  }

  public Method getMethod() {
    return method;
  }

  public MethodFilter setMethod(Method method) {
    this.method = method;
    return this;
  }

  public String getName() {
    return name;
  }

  public MethodFilter setName(String name) {
    this.name = name;
    return this;
  }

  public Class<?>[] getParameterTypes() {
    return parameterTypes;
  }

  public MethodFilter setParameterTypes(Class<?>[] parameterTypes) {
    this.parameterTypes = parameterTypes;
    return this;
  }
}
