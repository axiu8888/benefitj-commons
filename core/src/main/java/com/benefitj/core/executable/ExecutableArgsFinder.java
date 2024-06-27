package com.benefitj.core.executable;

import com.benefitj.core.SingletonSupplier;

import java.lang.reflect.Executable;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;

/**
 * 可执行函数的参数查找器
 */
public interface ExecutableArgsFinder {

  /**
   * 默认实例
   */
  SingletonSupplier<ExecutableArgsFinder> singleton = SingletonSupplier.of(Impl::new);

  static ExecutableArgsFinder get() {
    return singleton.get();
  }

  /**
   * 查找
   *
   * @param executable  被调用的对象
   * @param provideArgs 提供的可选值
   * @return 返回查找到的值
   */
  Object[] find(Executable executable, Object[] provideArgs);



  /**
   * 可执行函数的参数查找器实现
   */
  class Impl implements ExecutableArgsFinder {

    /**
     * 参数匹配
     */
    private static final Function<Executable, ArgsFinder> FINDER_CREATOR = ArgsFinder::new;

    private final Map<Executable, ArgsFinder> argsFinders = new WeakHashMap<>();

    public Impl() {
    }

    /**
     * 查找匹配的参数值
     *
     * @param executable  方法
     * @param provideArgs 参数对象
     * @return 返回符合的参数值
     */
    @Override
    public Object[] find(Executable executable, Object[] provideArgs) {
      return getFinder(executable).find(provideArgs);
    }

    public ArgsFinder getFinder(Executable executable) {
      return getArgsFinders().computeIfAbsent(executable, FINDER_CREATOR);
    }

    public Map<Executable, ArgsFinder> getArgsFinders() {
      return argsFinders;
    }
  }

}
