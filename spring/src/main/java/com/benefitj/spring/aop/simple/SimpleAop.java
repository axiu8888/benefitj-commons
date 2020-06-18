package com.benefitj.spring.aop.simple;

import com.benefitj.spring.aop.AbstractAop;
import com.benefitj.spring.aop.AopHandler;
import com.benefitj.spring.aop.AopJoinPoint;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 默认的切入点
 */
@Aspect
public class SimpleAop extends AbstractAop<AopJoinPoint, SimpleAop.SimpleAopHandler> {

  public SimpleAop() {
  }

  public SimpleAop(List<SimpleAopHandler> handlers) {
    super(handlers);
  }

  @Autowired(required = false)
  public void addHandlers(List<SimpleAopHandler> handlers) {
    for (SimpleAopHandler handler : handlers) {
      addHandler(handler);
    }
  }

  /**
   * 切入点表达式的语法格式: execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]))
   *
   * <p>被注解修饰的所有类的public方法
   */
  @Pointcut(
      "!execution(@com.benefitj.spring.aop.AopIgnore * *(..))"
          + " && ("
          + "(@within(com.benefitj.spring.aop.AopPointCut) && execution(public * *(..)))" // method
          + " || @annotation(com.benefitj.spring.aop.AopPointCut)" // class
          + ")"
  )
  @Override
  public void pointcut() {
    // ~
  }

  @Before("pointcut()")
  @Override
  public void doBefore(JoinPoint joinPoint) {
    super.doBefore(joinPoint);
  }

  @AfterReturning(value = "pointcut()", returning = "returnValue")
  @Override
  public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
    super.doAfterReturning(joinPoint, returnValue);
  }

  @AfterThrowing(value = "pointcut()", throwing = "ex")
  @Override
  public void doAfterThrowing(JoinPoint joinPoint, Throwable ex) {
    super.doAfterThrowing(joinPoint, ex);
  }

  @After("pointcut()")
  @Override
  public void doAfter(JoinPoint joinPoint) {
    super.doAfter(joinPoint);
  }

  @Override
  public AopJoinPoint wrap(JoinPoint original) {
    return new AopJoinPoint(original);
  }

  /**
   * 默认的AOP处理器
   */
  public interface SimpleAopHandler extends AopHandler<AopJoinPoint> {
    // ~
  }

}
