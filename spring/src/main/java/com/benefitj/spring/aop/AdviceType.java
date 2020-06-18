package com.benefitj.spring.aop;

/**
 * 类型
 */
public enum AdviceType {
  /**
   * 前置通知
   */
  BEFORE,
  /**
   * 后置通知
   */
  AFTER_RETURNING,
  /**
   * 异常通知
   */
  AFTER_THROWING,
  /**
   * 最终通知
   */
  AFTER,
//  /**
//   * 环绕
//   */
//  AROUND; // 环绕通知比较麻烦，暂不支持

}
