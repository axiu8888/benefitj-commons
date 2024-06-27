package com.benefitj.core.executable;

import com.benefitj.core.ReflectUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务代理
 */
public class ServiceProxy implements InvocationHandler {

  /**
   * 创建代理对象
   *
   * @param interfaceType 接口类型
   * @param target        被代理的对象
   * @param <T>           返回的接口代理
   * @return 返回代理
   */
  public static <T> T newProxy(Class<? extends T> interfaceType, Object target) {
    Class[] interfaces = {interfaceType};
    ServiceProxy handler = new ServiceProxy(interfaceType, target);
    return (T) Proxy.newProxyInstance(interfaceType.getClassLoader(), interfaces, handler);
  }

  /**
   * 方法调用者
   */
  private final Map<Method, MethodInvoker> invokers = new ConcurrentHashMap<>(20);

  /**
   * 接口类型
   */
  private Class<?> interfaceType;
  /**
   * 被代理的目标对象
   */
  private Object target;

  public ServiceProxy(Class<?> interfaceType, Object target) {
    this.interfaceType = interfaceType;
    this.target = target;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return getInvoker(proxy, method).invoke(args);
  }

  public MethodInvoker getInvoker(Object proxy, Method method) {
    MethodInvoker invoker = getInvokers().get(method);
    if (invoker == null) {
      invoker = getInvokers().computeIfAbsent(method, m -> createInvoker(proxy, method));
    }
    return invoker;
  }

  protected MethodInvoker createInvoker(Object proxy, Method method) {
    Object target = getTarget();
    Method targetMethod = ReflectUtils.getMethod(target.getClass(), method.getName(), method.getParameterTypes());
    if (targetMethod != null) {
      return new NormalMethodInvoker(target, targetMethod);
    } else {
      targetMethod = ReflectUtils.getMethod(getInterfaceType(), method.getName(), method.getParameterTypes());
    }
    if (targetMethod == null) {
      throw new IllegalStateException("无法发现方法所在的类!");
    } else {
      if (targetMethod.isDefault()) {
        return new DefaultMethodInvoker(proxy, targetMethod);
      } else {
        return new NormalMethodInvoker(proxy, targetMethod);
      }
    }
  }

  public Map<Method, MethodInvoker> getInvokers() {
    return invokers;
  }

  public Class<?> getInterfaceType() {
    return interfaceType;
  }

  public Object getTarget() {
    return target;
  }


  public interface MethodInvoker {

    /**
     * 检查方法是否匹配
     *
     * @param args 方法参数
     * @return 返回匹配的对象
     */
    Object invoke(Object[] args) throws Throwable;

  }

  /**
   * 方法调用抽象类
   */
  public static abstract class BaseMethodInvoker implements MethodInvoker {

    private Object target;
    private Method method;

    public BaseMethodInvoker() {
    }

    public BaseMethodInvoker(Object target, Method method) {
      this.target = target;
      this.method = method;
    }

    public Object getTarget() {
      return target;
    }

    public void setTarget(Object target) {
      this.target = target;
    }

    public Method getMethod() {
      return method;
    }

    public void setMethod(Method method) {
      this.method = method;
    }
  }

  /**
   * 普通的方法代理
   */
  public static class NormalMethodInvoker extends BaseMethodInvoker {

    public NormalMethodInvoker(Object target, Method method) {
      super(target, method);
    }

    @Override
    public Object invoke(Object[] args) throws Throwable {
      return ReflectUtils.invoke(getTarget(), getMethod(), args);
    }

  }

  /**
   * 默认方法代理
   */
  public static class DefaultMethodInvoker extends BaseMethodInvoker {

    private MethodHandles.Lookup lookup;

    public DefaultMethodInvoker(Object target, Method method) {
      super(target, method);
      if (!method.isDefault()) {
        throw new IllegalStateException("不是默认方法!");
      }
      this.lookup = ReflectUtils.newLookup(method);
    }

    @Override
    public Object invoke(Object[] args) throws Throwable {
      return ReflectUtils.invokeDefault(lookup, getTarget(), getMethod(), args);
    }

  }

}
