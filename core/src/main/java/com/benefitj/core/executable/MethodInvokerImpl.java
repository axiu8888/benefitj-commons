package com.benefitj.core.executable;

import com.benefitj.core.CatchUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 通用的调用器
 */
public class MethodInvokerImpl implements MethodInvoker {

  /**
   * 参数查找器
   */
  private ExecutableArgsFinder finder = ExecutableArgsFinder.INSTANCE;

  public MethodInvokerImpl() {
  }

  @Override
  public Object invoke(Object bean, Method method, Object... providedArgs) {
    try {
      if (!method.isAccessible()) {
        method.setAccessible(true);
      }
      Object[] args = getArgs(method, providedArgs);
      return method.invoke(bean, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  /**
   * 获取方法參數
   */
  public Object[] getArgs(Method method, Object... providedArgs) {
    return getFinder().find(method, providedArgs);
  }

  public ExecutableArgsFinder getFinder() {
    return finder;
  }

  public void setFinder(ExecutableArgsFinder finder) {
    this.finder = finder;
  }

}
