package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.SourceLocation;

/**
 * AOP的切入点
 */
public class AopJoinPoint implements JoinPoint {

  /**
   * 默认的切入点
   */
  private volatile JoinPoint original;
  /**
   * 通知类型
   */
  private AdviceType adviceType;

  public AopJoinPoint(JoinPoint original) {
    this.original = original;
  }

  public JoinPoint getOriginal() {
    return original;
  }

  public void setOriginal(JoinPoint original) {
    this.original = original;
  }

  @Override
  public String toShortString() {
    return getOriginal().toShortString();
  }

  @Override
  public String toLongString() {
    return getOriginal().toLongString();
  }

  @Override
  public Object getThis() {
    return getOriginal().getThis();
  }

  @Override
  public Object getTarget() {
    return getOriginal().getTarget();
  }

  @Override
  public Object[] getArgs() {
    return getOriginal().getArgs();
  }

  @Override
  public Signature getSignature() {
    return getOriginal().getSignature();
  }

  @Override
  public SourceLocation getSourceLocation() {
    return getOriginal().getSourceLocation();
  }

  @Override
  public String getKind() {
    return getOriginal().getKind();
  }

  @Override
  public StaticPart getStaticPart() {
    return getOriginal().getStaticPart();
  }

  /**
   * 是否为某个JoinPoint的实例
   *
   * @param type 检查的类型
   * @return 返回是否是实例
   */
  public boolean isInstance(Class<? extends JoinPoint> type) {
    return type.isInstance(this);
  }

  /**
   * 获取切入点对象
   */
  public <T extends JoinPoint> T getJoinPoint() {
    return (T) getOriginal();
  }

  /**
   * @return 获取通知类型
   */
  public AdviceType getAdviceType() {
    return adviceType;
  }

  /**
   * 设置通知类型
   *
   * @param adviceType 通知类型
   */
  public void setAdviceType(AdviceType adviceType) {
    this.adviceType = adviceType;
  }

  /**
   * 是否为某种通知类型
   *
   * @param type 通知类型
   * @return 返回是否为指定的通知类型
   */
  public boolean isAdviceType(AdviceType type) {
    return this.adviceType == type;
  }

}
