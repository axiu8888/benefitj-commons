package com.benefitj.frameworks;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * CGLib代理
 */
public class CGLibProxy implements MethodInterceptor {

  /**
   * 默认忽略的方法
   */
  public static final List<MethodFilter> DEFAULT_IGNORE_METHODS = Collections.unmodifiableList(Stream.of(Object.class.getDeclaredMethods())
      .map(m -> new MethodFilter(m.getName(), m.getParameterTypes()))
      .collect(Collectors.toList()));

  /**
   * 忽略的方法
   */
  private final List<MethodFilter> ignoreMethods = new ArrayList<>();
  /**
   * 方法拦截器
   */
  private MethodInterceptor interceptor;

  public CGLibProxy() {
  }

  public CGLibProxy(MethodInterceptor interceptor, boolean ignoreMethods) {
    this.interceptor = interceptor;
    if (ignoreMethods) {
      addDefaultIgnoreMethods();
    }
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    if (checkIgnore(obj, method, args)) {
      return proxy.invokeSuper(obj, args);
    }
    return getInterceptor().intercept(obj, method, args, proxy);
  }

  /**
   * 检查是否忽略
   *
   * @param obj    对象
   * @param method 方法
   * @param args   参数
   * @return 返回是否忽略的结果
   */
  public boolean checkIgnore(Object obj, Method method, Object[] args) {
    for (MethodFilter mf : ignoreMethods) {
      if (mf.match(obj, method, args)) {
        return true;
      }
    }
    return false;
  }

  public MethodInterceptor getInterceptor() {
    return interceptor;
  }

  public void setInterceptor(MethodInterceptor interceptor) {
    this.interceptor = interceptor;
  }

  public List<MethodFilter> getIgnoreMethods() {
    return ignoreMethods;
  }

  public void addDefaultIgnoreMethods() {
    getIgnoreMethods().addAll(DEFAULT_IGNORE_METHODS);
  }

  /**
   * 创建代理
   *
   * @param klass       类
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T createProxy(Class<?> klass, MethodInterceptor interceptor) {
    return createProxy(klass, null, interceptor);
  }

  /**
   * 创建代理
   *
   * @param interfaces  接口
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T createProxy(Class<?>[] interfaces, MethodInterceptor interceptor) {
    return createProxy(null, interfaces, interceptor);
  }

  /**
   * 创建代理
   *
   * @param klass       类
   * @param interfaces  接口
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T createProxy(Class<?> klass, Class<?>[] interfaces, MethodInterceptor interceptor) {
    Enhancer enhancer = new Enhancer();
    if (interfaces != null && interfaces.length > 0) {
      enhancer.setInterfaces(interfaces);
    }
    if (klass != null) {
      enhancer.setSuperclass(klass);
    }
    enhancer.setCallback(new CGLibProxy(interceptor, true));
    return (T) enhancer.create();
  }


  public static class MethodFilter {

    private static final Class<?>[] EMPTY_TYPES = new Class<?>[0];

    /**
     * 对象类型
     */
    private Class<?> klass;
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


    public Class<?> getKlass() {
      return klass;
    }

    public MethodFilter setKlass(Class<?> klass) {
      this.klass = klass;
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

}
