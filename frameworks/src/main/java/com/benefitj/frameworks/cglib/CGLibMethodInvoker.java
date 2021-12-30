package com.benefitj.frameworks.cglib;

import com.benefitj.core.ReflectUtils;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicReference;

/**
 * CGLib方法调用器
 */
public class CGLibMethodInvoker {

  private Object proxy;
  private Method method;
  private MethodProxy methodProxy;
  private Class<?>[] interfaces;
  private Object[] objects;

  private final AtomicReference<Object> targetRef = new AtomicReference<>();
  private final AtomicReference<Method> targetMethodRef = new AtomicReference<>();

  private MethodHandles.Lookup lookup;

  public CGLibMethodInvoker(Object proxy,
                            Method method,
                            MethodProxy methodProxy,
                            Class<?>[] interfaces,
                            Object[] objects) {
    this.proxy = proxy;
    this.method = method;
    this.methodProxy = methodProxy;
    this.interfaces = interfaces;
    this.objects = objects;
  }

  public Object invoke(Object[] args) throws Throwable {
    Method standard = getMethod();
    if (standard.isDefault()) {
      MethodHandles.Lookup lookup = this.lookup;
      if (lookup == null) {
        lookup = (this.lookup = ReflectUtils.newLookup(standard));
      }
      return ReflectUtils.invokeDefault(lookup, getProxy(), standard, args);
    }
    Object target = find(getInterfaces(), getObjects());
    if (target != null) {
      Method targetMethod = targetMethodRef.get();
      if (targetMethod == null) {
        targetMethod = ReflectUtils.getMethod(target.getClass()
            , standard.getName(), standard.getParameterTypes());
        if (targetMethod != null) {
          targetMethodRef.set(targetMethod);
        }
      }
      if (targetMethod != null) {
        return ReflectUtils.invoke(target, targetMethod, args);
      }
    }

    // 来自代理的接口
    if (standard.getDeclaringClass().isAssignableFrom(getProxy().getClass())) {
      return getMethodProxy().invokeSuper(getProxy(), args);
    }
    throw new IllegalStateException("无法调用方法: " + standard.getDeclaringClass().getName() + "." + standard.getName());
  }

  public Object find(Class<?>[] interfaces, Object[] objects) {
    Object target = targetRef.get();
    if (target != null) {
      return target != Void.class ? target : null;
    }

    Class<?> methodClass = getMethod().getDeclaringClass();
    for (Class<?> type : interfaces) {
      if (methodClass.isAssignableFrom(type)) {
        // 查找与此接口匹配的可选对象
        for (Object obj : objects) {
          if (type.isInstance(obj) || type.isAssignableFrom(methodClass)) {
            this.targetRef.set(obj);
            return obj;
          }
        }
      }
    }
    for (Object obj : objects) {
      if (methodClass.isAssignableFrom(obj.getClass())) {
        this.targetRef.set(obj);
        return obj;
      }
    }
    this.targetRef.set(Void.class);
    return null;
  }

  public Object getProxy() {
    return proxy;
  }

  public void setProxy(Object proxy) {
    this.proxy = proxy;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  public MethodProxy getMethodProxy() {
    return methodProxy;
  }

  public void setMethodProxy(MethodProxy methodProxy) {
    this.methodProxy = methodProxy;
  }

  public Class<?>[] getInterfaces() {
    return interfaces;
  }

  public void setInterfaces(Class<?>[] interfaces) {
    this.interfaces = interfaces;
  }

  public Object[] getObjects() {
    return objects;
  }

  public void setObjects(Object[] objects) {
    this.objects = objects;
  }

}
