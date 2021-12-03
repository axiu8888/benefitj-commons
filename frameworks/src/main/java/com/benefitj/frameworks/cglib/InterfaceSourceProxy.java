package com.benefitj.frameworks.cglib;

import com.benefitj.core.ReflectUtils;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;

/**
 * 通过代理操作原对象
 */
public class InterfaceSourceProxy implements MethodInterceptor {

  /**
   * 创建接口代理对象
   *
   * @param interfaces 接口数组
   * @param objects    被代理的对象数组
   * @param <T>        返回的类型
   * @return 返回创建的代理
   */
  public static <T> T newProxy(@Nonnull Class<?>[] interfaces, @Nonnull Object[] objects) {
    return newProxy(null, interfaces, objects);
  }

  /**
   * 创建接口代理对象
   *
   * @param superclass 父类
   * @param interfaces 接口数组
   * @param objects    被代理的对象数组
   * @param <T>        返回的类型
   * @return 返回创建的代理
   */
  public static <T> T newProxy(@Nullable Class<?> superclass,
                               @Nonnull Class<?>[] interfaces,
                               @Nonnull Object[] objects) {
    InterfaceSourceProxy[] proxies = new InterfaceSourceProxy[objects.length];
    for (int i = 0; i < proxies.length; i++) {
      proxies[i] = new InterfaceSourceProxy(objects[i]);
    }
    return EnhancerBuilder.newBuilder()
        // 接口
        .setInterfaces(interfaces)
        // 父类
        .setSuperclass(superclass)
        // 回调函数的匹配方法
        .setCallbackFilter(m -> callbackFilter(m, objects))
        // 回调
        .setCallbacks(proxies)
        .create();
  }

  private Object source;

  public InterfaceSourceProxy(Object source) {
    this.source = source;
  }

  @Override
  public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
    return ReflectUtils.invoke(source, method, args);
  }

  private static int callbackFilter(Method method, Object[] objects) {
    for (int i = 0; i < objects.length; i++) {
      if (method.getDeclaringClass().isAssignableFrom(objects[i].getClass())) {
        return i;
      }
    }
    return 0;
  }

}
