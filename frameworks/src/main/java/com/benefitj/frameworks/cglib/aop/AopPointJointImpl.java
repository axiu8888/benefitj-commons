package com.benefitj.frameworks.cglib.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class AopPointJointImpl implements AopPointJoint {

  private MethodProxy proxy;
  private Object source;
  private Method method;
  private Object[] args;

  private Object returnValue;

  public AopPointJointImpl() {
  }

  public AopPointJointImpl(MethodProxy proxy, Object source, Method method, Object[] args) {
    this.proxy = proxy;
    this.source = source;
    this.method = method;
    this.args = args;
  }

  @Override
  public MethodProxy getProxy() {
    return proxy;
  }

  public void setProxy(MethodProxy proxy) {
    this.proxy = proxy;
  }

  @Override
  public Object getSource() {
    return source;
  }

  public void setSource(Object source) {
    this.source = source;
  }

  @Override
  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

  @Override
  public Object[] getArgs() {
    return args;
  }

  public void setArgs(Object[] args) {
    this.args = args;
  }

  @Override
  public Object getReturnValue() {
    return returnValue;
  }

  @Override
  public void setReturnValue(Object returnValue) {
    this.returnValue = returnValue;
  }
}
