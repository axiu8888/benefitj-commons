package com.benefitj.frameworks.cglib.aop;

import com.benefitj.frameworks.cglib.CGLibProxy;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Around 代理
 *
 * @param <T>
 */
public class AopAdviceProxy<T> implements MethodInterceptor {

  /**
   * 创建代理
   *
   * @param target 代理的对象
   * @param advice 通知
   * @return 返回代理
   */
  public static <T> T newProxy(T target, PointAdvice advice) {
    return newProxy(target, (Class<T>) target.getClass(), advice);
  }

  /**
   * 创建代理
   *
   * @param target 代理的对象
   * @param type   类型
   * @param advice 通知
   * @return 返回代理
   */
  public static <T> T newProxy(T target, Class<? extends T> type, PointAdvice advice) {
    return CGLibProxy.newProxy(type, new AopAdviceProxy<>(target, type, advice));
  }

  private final Map<Method, Boolean> methods = new ConcurrentHashMap<>();

  private T target;
  private Class<? extends T> type;
  private PointAdvice advice;

  public AopAdviceProxy() {
  }

  public AopAdviceProxy(T target, Class<? extends T> type, PointAdvice advice) {
    this.target = target;
    this.type = type;
    this.advice = advice;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    if (support(method)) {
      T target = this.getTarget();
      AopPointJoint joint = new AopPointJointImpl(proxy, target, method, args);
      PointAdvice advice = this.getAdvice();
      try {
        advice.doBefore(joint);
        Object resultValue = proxy.invoke(target, args);
        joint.setReturnValue(resultValue);
        advice.doAfter(joint);
        return joint.getReturnValue();
      } catch (Throwable e) {
        advice.doError(joint, e);
        throw e;
      } finally {
        advice.doAfterReturning(joint);
      }
    }
    return proxy.invoke(this.getTarget(), args);
  }

  public boolean support(Method method) {
    Boolean flag = methods.get(method);
    if (flag == null) {
      flag = methods.computeIfAbsent(method, m -> {
        if (getType().isAnnotationPresent(ProxyAround.class)) {
          ProxyAround around = getType().getAnnotation(ProxyAround.class);
          if ((around.value() & method.getModifiers()) > 0) {
            return true;
          }
        }
        ProxyAround around = method.getAnnotation(ProxyAround.class);
        return around != null && (around.value() & method.getModifiers()) > 0;
      });
    }
    return Boolean.TRUE.equals(flag);
  }

  public T getTarget() {
    return target;
  }

  public void setTarget(T target) {
    this.target = target;
  }

  public Class<? extends T> getType() {
    return type;
  }

  public void setType(Class<? extends T> type) {
    this.type = type;
  }

  public PointAdvice getAdvice() {
    return advice;
  }

  public void setAdvice(PointAdvice advice) {
    this.advice = advice;
  }

}
