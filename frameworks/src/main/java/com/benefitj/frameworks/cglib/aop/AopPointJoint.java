package com.benefitj.frameworks.cglib.aop;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * AOP切入点
 */
public interface AopPointJoint {

  /**
   * 获取方法代理
   */
  MethodProxy getProxy();

  /**
   * 获取源对象
   */
  Object getSource();

  /**
   * 代理的方法
   */
  Method getMethod();

  /**
   * 参数
   */
  Object[] getArgs();

  /**
   * 获取返回值
   */
  Object getReturnValue();

  /**
   * 设置返回值
   *
   * @param value 返回值
   */
  void setReturnValue(Object value);


  /**
   * 创建切入点对象
   *
   * @param proxy  代理
   * @param source 源对象
   * @param method 代理的方法
   * @param args   方法参数
   * @return 返回切入点
   */
  static AopPointJoint newPointJoint(MethodProxy proxy, Object source, Method method, Object[] args) {
    return new AopPointJointImpl(proxy, source, method, args);
  }

}
