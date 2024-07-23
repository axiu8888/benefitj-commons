package com.benefitj.core.executable;

import com.benefitj.core.CatchUtils;

import java.lang.reflect.InvocationTargetException;
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
    return new Impl();
  }


  /**
   * 通用的调用器
   */
  class Impl implements MethodInvoker {

    /**
     * 参数查找器
     */
    private ExecutableArgsFinder finder = ExecutableArgsFinder.get();

    public Impl() {
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

}
