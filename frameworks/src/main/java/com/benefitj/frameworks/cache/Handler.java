package com.benefitj.frameworks.cache;

import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public interface Handler {

  /**
   * 处理
   *
   * @param method 函数
   * @param args   参数
   * @param proxy  代理对象
   * @return 返回调用结果
   */
  Object process(Method method, Object[] args, MethodProxy proxy);

}
