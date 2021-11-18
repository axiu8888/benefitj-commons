package com.benefitj.core.executable;

import java.lang.reflect.Executable;

/**
 * 可执行函数的参数查找器
 */
public interface ExecutableArgsFinder {

  /**
   * 默认实例
   */
  ExecutableArgsFinder INSTANCE = new ExecutableArgsFinderImpl();

  /**
   * 查找
   *
   * @param executable  被调用的对象
   * @param provideArgs 提供的可选值
   * @return 返回查找到的值
   */
  Object[] find(Executable executable, Object[] provideArgs);

}
