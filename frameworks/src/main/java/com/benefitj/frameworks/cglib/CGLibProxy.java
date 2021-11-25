package com.benefitj.frameworks.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

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
  public static final List<MethodFilter> DEFAULT_IGNORE_METHODS = Collections.unmodifiableList(
      Stream.of(Object.class.getDeclaredMethods())
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
   * @param superclass  父类
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T createProxy(Class<?> superclass, MethodInterceptor interceptor) {
    return createProxy(superclass, null, interceptor);
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
   * @param superclass  父类
   * @param interfaces  接口
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T createProxy(Class<?> superclass, Class<?>[] interfaces, MethodInterceptor interceptor) {
    Enhancer enhancer = new Enhancer();
    if (interfaces != null && interfaces.length > 0) {
      // 接口过滤
      enhancer.setInterfaces(interfaces);
    }
    if (superclass != null) {
      enhancer.setSuperclass(superclass);
    }
    enhancer.setCallback(new CGLibProxy(interceptor, true));
    return (T) enhancer.create();
  }

}
