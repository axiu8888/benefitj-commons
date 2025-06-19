package com.benefitj.frameworks.cglib;

import com.benefitj.core.annotation.MethodReturn;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
    return newProxy(superclass, (Class<?>[]) null, interceptor);
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
   * <p>
   * 在高版本JDK上，需要添加 --add-opens java.base/java.lang=ALL-UNNAMED 启动参数
   * 参考: https://blog.csdn.net/FatalFlower/article/details/122589921
   *
   * @param superclass  父类
   * @param interfaces  接口
   * @param interceptor 方案拦截器
   * @return 返回创建的代理对象
   */
  public static <T> T newProxy(Class<?> superclass, Class<?>[] interfaces, MethodInterceptor interceptor) {
    // TODO 在高版本JDK上(15以上)，需要添加 --add-opens java.base/java.lang=ALL-UNNAMED 启动参数
    // 参考: https://blog.csdn.net/FatalFlower/article/details/122589921
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
                               @Nonnull Class<?> interfaces,
                               @Nonnull Object objects) {
    return newProxy(superclass, new Class[]{interfaces}, new Object[]{objects});
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
    final Map<Method, CGLibMethodInvoker> invokers = new ConcurrentHashMap<>(20);
    return newProxy(superclass, interfaces, (obj, method, args, proxy) -> {
      CGLibMethodInvoker invoker = invokers.get(method);
      if (invoker == null) {
        invoker = invokers.computeIfAbsent(method, m -> new CGLibMethodInvoker(obj, m, proxy, interfaces, objects));
      }
      return invoker.invoke(args);
    });
  }

  /**
   * 创建调用父类方法的拦截器
   */
  public static MethodInterceptor newInvokeSuper() {
    return (obj, method, args, proxy) -> proxy.invokeSuper(obj, args);
  }

  /**
   * 创建代理，自定义对象的返回值
   *
   * @param interfaceTypes 接口类型
   * @param handler        处理器
   * @param attrs          属性Map
   * @param <T>            接口类型
   * @return 返回代理对象
   */
  public static <T> T newProxyWithMethodReturn(Class[] interfaceTypes, MethodInterceptor handler, Map<String, Object> attrs) {
    return newProxyWithMethodReturn(interfaceTypes, handler, new MethodReturn.DefaultHandler(attrs));
  }

  /**
   * 创建代理，自定义对象的返回值
   *
   * @param interfaceTypes 接口类型
   * @param handler        处理器
   * @param resultHandler  返回处理器
   * @param <T>            接口类型
   * @return 返回代理对象
   */
  public static <T> T newProxyWithMethodReturn(Class[] interfaceTypes, MethodInterceptor handler, MethodReturn.Handler resultHandler) {
    return newProxy(null, interfaceTypes, (obj, method, args, proxy) ->
        method.isAnnotationPresent(MethodReturn.class)
            ? resultHandler.process(obj, method, args, method.getAnnotation(MethodReturn.class))
            : handler.intercept(proxy, method, args, proxy)
    );
  }

}
