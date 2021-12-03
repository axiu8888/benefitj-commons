package com.benefitj.frameworks.cglib;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;

/**
 * 增强器构造器
 */
public interface EnhancerBuilder {

  Enhancer enhancer();

  /**
   * 设置父类
   *
   * @param superclass 父类
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setSuperclass(Class superclass) {
    this.enhancer().setSuperclass(superclass);
    return this;
  }

  /**
   * 设置实现的接口
   *
   * @param interfaces 接口数组
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setInterfaces(Class[] interfaces) {
    this.enhancer().setInterfaces(interfaces);
    return this;
  }

  /**
   * 匹配回调函数的下标
   *
   * @param filter 回调函数过滤器
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setCallbackFilter(CallbackFilter filter) {
    this.enhancer().setCallbackFilter(filter);
    return this;
  }

  /**
   * 设置回调 {@link net.sf.cglib.proxy.MethodInterceptor}
   *
   * @param callbacks 回调函数
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setCallbacks(Callback... callbacks) {
    this.enhancer().setCallbacks(callbacks);
    return this;
  }

  /**
   * 设置回调 {@link net.sf.cglib.proxy.MethodInterceptor}
   *
   * @param callback 回调函数
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setCallback(final Callback callback) {
    this.enhancer().setCallback(callback);
    return this;
  }

  /**
   * 是否实用 {@link net.sf.cglib.proxy.Factory}
   *
   * @param useFactory 是否实用工厂方法
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setUseFactory(boolean useFactory) {
    enhancer().setUseFactory(useFactory);
    return this;
  }

  /**
   * 设置是否拦截期间构造
   *
   * @param interceptDuringConstruction 是否拦截期间构造
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setInterceptDuringConstruction(boolean interceptDuringConstruction) {
    enhancer().setInterceptDuringConstruction(interceptDuringConstruction);
    return this;
  }

  /**
   * 设置回调类型
   *
   * @param callbackType 类型
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setCallbackType(Class callbackType) {
    enhancer().setCallbackType(callbackType);
    return this;
  }

  /**
   * 设置回调类型
   *
   * @param callbackTypes 类型数组
   * @return 返回 EnhancerBuilder
   */
  default EnhancerBuilder setCallbackTypes(Class[] callbackTypes) {
    enhancer().setCallbackTypes(callbackTypes);
    return this;
  }

  /**
   * 实例化对象
   *
   * @param <T> 对象类型
   * @return 返回创建的对象
   */
  default <T> T build() {
    return (T) enhancer().create();
  }

  /**
   * 实例化对象
   *
   * @param argumentTypes 参数类型
   * @param arguments     参数
   * @param <T>           对象类型
   * @return 返回创建的对象
   */
  default <T> T build(Class[] argumentTypes, Object[] arguments) {
    return (T) enhancer().create(argumentTypes, arguments);
  }

  /**
   * 创建 EnhancerBuilder
   */
  static EnhancerBuilder newBuilder() {
    final Enhancer enhancer = new Enhancer();
    return () -> enhancer;
  }

}
