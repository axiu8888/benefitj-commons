package com.benefitj.http;

import com.benefitj.core.ReflectUtils;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Scheduler;

import java.lang.reflect.Method;

/**
 * 调用处理器
 *
 * @param <T>
 */
public interface ApiInvocationHandler<T> {

  /**
   * 调用方法
   *
   * @param api    API对象
   * @param method 方法
   * @param args   参数
   * @return 返回结果
   * @throws Throwable
   */
  Object invoke(T api, Method method, Object[] args) throws Throwable;

  /**
   * 创建默认的调用
   */
  static <T> ApiInvocationHandler<T> createSimple() {
    return new SimpleApiInvocationHandler<>();
  }

  /**
   * 创建RxJava的调用
   */
  static <T> ApiInvocationHandler<T> createScheduler() {
    return new SchedulerApiInvocationHandler<>();
  }

  /**
   * 默认实现
   *
   * @param <T>
   */
  class SimpleApiInvocationHandler<T> implements ApiInvocationHandler<T> {

    @Override
    public Object invoke(T target, Method method, Object[] args) throws Throwable {
      return ReflectUtils.invoke(target, method, args);
    }

  }

  /**
   * 调度器
   *
   * @param <T>
   */
  class SchedulerApiInvocationHandler<T> implements ApiInvocationHandler<T> {

    @Override
    public Object invoke(T target, Method method, Object[] args) throws Throwable {
      Object value = ReflectUtils.invoke(target, method, args);
      if (value instanceof Observable) {
        SchedulerOn schedulerOn = method.getAnnotation(SchedulerOn.class);
        if (schedulerOn != null) {
          Scheduler observeOn = schedulerOn.observeOn().getScheduler();
          if (observeOn != null) {
            value = ((Observable<?>) value).observeOn(observeOn);
          }
          Scheduler subscribeOn = schedulerOn.subscribeOn().getScheduler();
          if (subscribeOn != null) {
            value = ((Observable<?>) value).subscribeOn(subscribeOn);
          }
        }
        return value;
      }

      if (value instanceof Flowable) {
        SchedulerOn schedulerOn = method.getAnnotation(SchedulerOn.class);
        if (schedulerOn != null) {
          Scheduler observeOn = schedulerOn.observeOn().getScheduler();
          if (observeOn != null) {
            value = ((Flowable<?>) value).observeOn(observeOn);
          }
          Scheduler subscribeOn = schedulerOn.subscribeOn().getScheduler();
          if (subscribeOn != null) {
            value = ((Flowable<?>) value).subscribeOn(subscribeOn);
          }
        }
        return value;
      }
      return value;
    }

  }
}
