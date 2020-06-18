package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;

/**
 * Aop的处理接口，对简单的方法和类进行处理
 */
public interface AopHandler<JP extends JoinPoint> {

  /**
   * 是否支持，默认不支持
   *
   * @param joinPoint 切入点
   * @return 返回是否支持操作
   */
  default boolean support(JP joinPoint) {
    return false;
  }

  /**
   * 方法执行之前
   *
   * @param joinPoint 方法切入点
   */
  default void doBefore(JP joinPoint) {
    // ~
  }

  /**
   * 方法返回时
   *
   * @param joinPoint   方法切入点
   * @param returnValue 返回结果
   */
  default Object doAfterReturning(JP joinPoint, Object returnValue) {
    return returnValue;
  }

  /**
   * 方法抛出异常
   *
   * @param joinPoint 方法切入点
   * @param ex        抛出的异常
   */
  default void doAfterThrowing(JP joinPoint, Throwable ex) {
    // ~
  }

  /**
   * 方法执行结束
   *
   * @param joinPoint 方法切入点
   */
  default void doAfter(JP joinPoint) {
    // ~
  }

}
