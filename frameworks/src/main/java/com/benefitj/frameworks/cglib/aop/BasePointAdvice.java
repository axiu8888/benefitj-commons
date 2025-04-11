package com.benefitj.frameworks.cglib.aop;

public abstract class BasePointAdvice implements PointAdvice {

  @Override
  public void doBefore(AopPointJoint joint) {
  }

  @Override
  public void doAfter(AopPointJoint joint) {
  }

  @Override
  public void doError(AopPointJoint joint, Throwable ex) {
  }

  @Override
  public void doAfterReturning(AopPointJoint joint) {
  }
}
