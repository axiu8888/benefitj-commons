package com.benefitj.core.executable;

import java.lang.reflect.Method;

/**
 * 方法调用器
 */
public interface MethodInvoker {

  /**
   * 调用
   *
   * @param bean         对象
   * @param method       方法
   * @param providedArgs 可选的参数
   * @return 返回调用结果
   */
  Object invoke(Object bean, Method method, Object... providedArgs);


  /**
   * 创建方法调用器
   */
  static MethodInvoker newInvoker() {
    return new MethodInvokerImpl();
  }

}
