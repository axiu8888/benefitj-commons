package com.benefitj.frameworks.cglib.aop;

/**
 * 通知
 */
public interface PointAdvice {

  /**
   * 执行前
   */
  void doBefore(AopPointJoint joint);

  /**
   * 执行后
   */
  void doAfter(AopPointJoint joint);

  /**
   * 抛出异常时
   */
  void doError(AopPointJoint joint, Throwable ex);

  /**
   * 调用完成，返回结果
   */
  void doAfterReturning(AopPointJoint joint);

}
