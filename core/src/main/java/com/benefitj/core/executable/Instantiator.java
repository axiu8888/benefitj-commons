package com.benefitj.core.executable;

import javax.annotation.Nullable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * 实例化器
 */
public interface Instantiator {

  /**
   * 实例化器对象
   */
  Instantiator INSTANCE = new InstantiatorImpl();

  /**
   * 创建对象
   *
   * @param type 类型
   * @param args 参数
   * @param <T>  类型
   * @return 返回创建的对象
   */
  <T> T create(Class<T> type, @Nullable Object... args);


  /**
   * 默认的实例化器
   */
  class InstantiatorImpl implements Instantiator {

    @Override
    public <T> T create(Class<T> type, Object... args) {
      try {
        if (args != null && args.length > 0) {
          for (Constructor<?> c : type.getConstructors()) {
            // 匹配参数
            if (isParameterTypesMatch(c.getParameterTypes(), args)) {
              return (T) c.newInstance(args);
            }
          }
        }
        if (args != null && args.length != 0) {
          throw new IllegalStateException("无法实例化\"" + type + "\"的对象，没有对应参数的构造函数!");
        }
        return type.newInstance();
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    }

  }

  /**
   * 检查参数是否匹配
   *
   * @param parameterTypes 参数类型
   * @param args           参数
   * @return 返回校验结果
   */
  static boolean isParameterTypesMatch(Class<?>[] parameterTypes, @Nullable Object[] args) {
    if (parameterTypes != null && args != null) {
      if (parameterTypes.length != args.length) {
        return false;
      }
      for (int i = 0; i < parameterTypes.length; i++) {
        if (args[i] != null && !parameterTypes[i].isInstance(args[i])) {
          return false;
        }
      }
      return true;
    }
    return parameterTypes == null && (args == null || args.length == 0);
  }

}
