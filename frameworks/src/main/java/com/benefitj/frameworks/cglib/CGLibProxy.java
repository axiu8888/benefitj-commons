package com.benefitj.frameworks.cglib;

import com.benefitj.core.ReflectUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.annotation.Nonnull;
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
  public static <T> T newProxy(Class<?> superclass, MethodInterceptor interceptor) {
    return newProxy(superclass, null, interceptor);
  }

  /**
   * 创建代理
   *
   * @param interfaces  接口
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T newProxy(Class<?>[] interfaces, MethodInterceptor interceptor) {
    return newProxy(null, interfaces, interceptor);
  }

  /**
   * 创建代理
   *
   * @param superclass  父类
   * @param interfaces  接口
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T newProxy(Class<?> superclass, Class<?>[] interfaces, MethodInterceptor interceptor) {
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

  /**
   * 创建代理
   *
   * @param superclass 父类
   * @param interfaces 接口类型
   * @param objects    可选的代理对象，代理对象的下标最好与接口的位置一致
   * @param <T>        返回的类型
   * @return 返回对象代理
   */
  public static <T> T newProxy(@Nullable Class<?> superclass,
                               @Nonnull Class<?>[] interfaces,
                               @Nonnull Object[] objects) {
    return newProxy(superclass, interfaces,
        (obj, method, args, proxy) -> invoke(obj, method, args, proxy, interfaces, objects));
  }

  /**
   * 查找匹配的对象
   *
   * @param method     方法
   * @param interfaces 接口数组
   * @param objects    对象数组
   * @return 返回查找结果
   */
  public static Object findObject(Method method, Class<?>[] interfaces, Object[] objects) {
    Class<?> methodClass = method.getDeclaringClass();
    for (Class<?> i : interfaces) {
      if (methodClass.isAssignableFrom(i)) {
        // 查找与此接口匹配的可选对象
        for (Object obj : objects) {
          if (i.isInstance(obj) || i.isAssignableFrom(methodClass)) {
            return obj;
          }
        }
      }
    }
    for (Object obj : objects) {
      if (methodClass.isAssignableFrom(obj.getClass())) {
        return obj;
      }
    }
    return null;
  }

  /**
   * 调用方法
   *
   * @param obj        代理的对象
   * @param method     方法
   * @param args       方法参数
   * @param proxy      代理
   * @param interfaces 接口数组
   * @param objects    对象数组
   * @return 返回调用结果
   * @throws Throwable
   */
  public static Object invoke(Object obj,
                              Method method,
                              Object[] args,
                              MethodProxy proxy,
                              Class<?>[] interfaces,
                              Object[] objects) throws Throwable {
    Object delegate = CGLibProxy.findObject(method, interfaces, objects);
    if (delegate != null) {
      try {
        Method delegateMethod = delegate.getClass()
            .getDeclaredMethod(method.getName(), method.getParameterTypes());
        return ReflectUtils.invoke(delegate, delegateMethod, args);
      } catch (NoSuchMethodException e) { /* ~ */ }
      return ReflectUtils.invoke(delegate, method, args);
    }
    return proxy.invokeSuper(obj, args);
  }


  /**
   * 创建调用父类方法的拦截器
   */
  public static MethodInterceptor newInvokeSuper() {
    return (obj, method, args, proxy) -> proxy.invokeSuper(obj, args);
  }

}
