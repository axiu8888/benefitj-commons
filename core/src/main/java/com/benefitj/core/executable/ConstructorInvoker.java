package com.benefitj.core.executable;

import com.benefitj.core.CatchUtils;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ConstructorInvoker<T> {

  private ExecutableArgsFinder finder = ExecutableArgsFinder.get();

  private final Class<T> klass;

  private Constructor<T>[] constructors;

  public ConstructorInvoker(Class<T> klass) {
    this.klass = klass;
    this.constructors = sort((Constructor<T>[]) klass.getConstructors());
  }

  public ConstructorInvoker(Class<T> klass, ExecutableArgsFinder finder) {
    this(klass);
    this.finder = finder;
  }

  /**
   * 排序
   *
   * @param constructors 构造函数数组
   * @return 返回排序后的数组
   */
  protected Constructor<T>[] sort(Constructor<T>[] constructors) {
    // 排序，参数多的在前面
    Arrays.sort(constructors, (o1, o2) ->
        -Integer.compare(o1.getParameterCount(), o2.getParameterCount()));
    return constructors;
  }

  /**
   * 创建实例对象
   *
   * @param provideArgs 可选的参数
   * @return 返回创建的对象
   */
  public T newInstance(@Nullable Object... provideArgs) {
    try {
      Constructor<T>[] constructors = getConstructors();
      if (provideArgs != null && provideArgs.length > 0) {
        for (Constructor<T> constructor : constructors) {
          if (constructor.getParameterCount() <= provideArgs.length) {
            Object[] args = getFinder().find(constructor, provideArgs);
            if (args != null && args.length > 0) {
              constructor.setAccessible(true);
              return constructor.newInstance(args);
            }
          }
        }
      }

      // 找一个最匹配的构造函数
      Constructor<T> constructor = constructors[constructors.length - 1];
      if (constructor.getParameterCount() >= 0) {
        throw new IllegalStateException("无法找到匹配的对应参数的构造函数!");
      }
      constructor.setAccessible(true);
      return constructor.newInstance();
    } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
      throw CatchUtils.throwing(e, IllegalStateException.class);
    }
  }

  public Class<T> getKlass() {
    return klass;
  }

  public Constructor<T>[] getConstructors() {
    return constructors;
  }

  public ExecutableArgsFinder getFinder() {
    return finder;
  }

  public void setFinder(ExecutableArgsFinder finder) {
    this.finder = finder;
  }

}
