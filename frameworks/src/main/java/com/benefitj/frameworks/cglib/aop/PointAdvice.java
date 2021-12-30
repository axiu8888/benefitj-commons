package com.benefitj.frameworks.cglib.aop;

/**
 * 通知
 */
public interface PointAdvice {

  /**
   * 执行前
   */
  default void onBefore(AopPointJoint joint) {
  }

  /**
   * 执行后
   */
  default void onAfter(AopPointJoint joint) {
  }

  /**
   * 抛出异常时
   */
  default void onError(AopPointJoint joint, Throwable ex) {
  }

  /**
   * 调用完成，返回结果
   */
  default void onAfterReturning(AopPointJoint joint) {
  }

}
