package com.benefitj.spring.aop.web;

import com.benefitj.spring.aop.AbstractAop;
import com.benefitj.spring.aop.AopHandler;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

/**
 * Web请求的切入点
 */
@Aspect
public class WebAop extends AbstractAop<WebRequestJoinPoint, WebAop.WebAopHandler> {

  private final ThreadLocal<WebRequestJoinPoint> local = new ThreadLocal<>();

  public WebAop() {
  }

  public WebAop(List<WebAopHandler> handlers) {
    super(handlers);
  }

  @Autowired(required = false)
  public void addHandlers(List<WebAopHandler> handlers) {
    for (WebAopHandler handler : handlers) {
      addHandler(handler);
    }
  }

  /**
   * 切入点表达式的语法格式: execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]))
   */
  @Pointcut(
      "!execution(@com.benefitj.spring.aop.AopIgnore * *(..))" // 没有被AopIgnore注解注释
          + " && ("
          + " (@annotation(org.springframework.web.bind.annotation.RequestMapping)"
          + " || @annotation(org.springframework.stereotype.Controller)"
          + " || @annotation(org.springframework.web.bind.annotation.RestController)"
          + " || @annotation(org.springframework.web.bind.annotation.Mapping)"
          + " || @annotation(org.springframework.web.bind.annotation.GetMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.PostMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.PutMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.DeleteMapping)"
          + " || @annotation(org.springframework.web.bind.annotation.PatchMapping)"
          + ")" // 需要被springMVC注解注释
          + " && ("
          + "(@within(com.benefitj.spring.aop.web.AopWebPointCut) && execution(public * *(..)))"// method
          + " || @annotation(com.benefitj.spring.aop.web.AopWebPointCut)"  // class
          + ")"
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
  public WebRequestJoinPoint wrap(JoinPoint original) {
    WebRequestJoinPoint joinPoint = local.get();
    if (joinPoint == null) {
      local.set(joinPoint = new WebRequestJoinPoint(original));
    } else {
      joinPoint.setOriginal(original);
    }
    ServletRequestAttributes attributes = getRequestAttributes();
    if (attributes != null) {
      joinPoint.setRequest(attributes.getRequest());
      joinPoint.setResponse(attributes.getResponse());
    }
    return joinPoint;
  }

  @Nullable
  protected ServletRequestAttributes getRequestAttributes() {
    return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
  }

  /**
   * Web请求处理器
   */
  public interface WebAopHandler extends AopHandler<WebRequestJoinPoint> {
    // ~
  }

}
