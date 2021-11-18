package com.benefitj.core.executable;

import java.lang.reflect.Method;


/**
 * 简单方法调用器
 */
public class SimpleMethodInvoker extends MethodInvokerImpl {

  /**
   * 对象
   */
  private Object bean;
  /**
   * 方法
   */
  private Method method;

  public SimpleMethodInvoker(Method method) {
    this.method = method;
  }

  public SimpleMethodInvoker(Object bean, Method method) {
    this.bean = bean;
    this.method = method;
  }

  /**
   * 调用方法
   *
   * @param providedArgs 可提供的参数
   * @return 返回结果
   */
  public Object invoke(Object... providedArgs) {
    return invoke(getBean(), getMethod(), providedArgs);
  }

  public Object getBean() {
    return bean;
  }

  public void setBean(Object bean) {
    this.bean = bean;
  }

  public Method getMethod() {
    return method;
  }

  public void setMethod(Method method) {
    this.method = method;
  }

}
