package com.benefitj.spring.aop;

import org.aspectj.lang.JoinPoint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 默认的切面
 */
public abstract class AbstractAop<JP extends AopJoinPoint, H extends AopHandler<JP>> {

  private final List<AopHandler<JP>> handlers = Collections.synchronizedList(new ArrayList<>());

  protected final ParameterBiConsumer<AopHandler<JP>, JP, Object> doBeforeConsumer = (handler, jp, args) -> handler.doBefore(jp);
  protected final ParameterBiConsumer<AopHandler<JP>, JP, Object> doAfterReturningConsumer = AopHandler::doAfterReturning;
  protected final ParameterBiConsumer<AopHandler<JP>, JP, Throwable> doAfterThrowingConsumer = AopHandler::doAfterThrowing;
  protected final ParameterBiConsumer<AopHandler<JP>, JP, Object> doAfterConsumer = (handler, jp, args) -> handler.doAfter(jp);

  public AbstractAop() {
  }

  public AbstractAop(List<H> handlers) {
    this.handlers.addAll(handlers);
  }

  public List<AopHandler<JP>> getHandlers() {
    return handlers;
  }

  public void addHandler(H handler) {
    List<AopHandler<JP>> l = getHandlers();
    if (!l.contains(handler)) {
      l.add(handler);
    }
  }

  /**
   * 切入点表达式的语法格式: execution([权限修饰符] [返回值类型] [简单类名/全类名] [方法名]([参数列表]))
   *
   * <p>被注解修饰的所有类的public方法
   */
  public abstract void pointcut();

  /**
   * 前置通知
   *
   * @param joinPoint 连接点
   */
  public void doBefore(JoinPoint joinPoint) {
    apply(joinPoint, doBeforeConsumer, null, AdviceType.BEFORE);
  }

  /**
   * 后置通知
   *
   * @param joinPoint 连接点
   */
  public void doAfterReturning(JoinPoint joinPoint, Object returnValue) {
    apply(joinPoint, doAfterReturningConsumer, returnValue, AdviceType.AFTER_RETURNING);
  }

  /**
   * 异常通知: 方法抛出异常退出时执行的通知
   *
   * @param joinPoint 连接点
   * @param ex
   */
  public void doAfterThrowing(JoinPoint joinPoint, Throwable ex) {
    apply(joinPoint, doAfterThrowingConsumer, ex, AdviceType.AFTER_THROWING);
  }

  /**
   * 最终通知: 当某连接点退出的时候执行的通知（不论是正常返回还是异常退出）
   *
   * @param joinPoint 连接点
   */
  public void doAfter(JoinPoint joinPoint) {
    apply(joinPoint, doAfterConsumer, null, AdviceType.AFTER);
  }

  /**
   * 处理
   *
   * @param joinPoint 连接点
   * @param consumer  消费者
   */
  public <P> void apply(JoinPoint joinPoint, ParameterBiConsumer<AopHandler<JP>, JP, P> consumer, P param, AdviceType type) {
    final List<AopHandler<JP>> handlers = getHandlers();
    if (!handlers.isEmpty()) {
      final JP point = wrap(joinPoint);
      point.setAdviceType(type);
      for (AopHandler<JP> handler : handlers) {
        try {
          if (handler.support(point)) {
            consumer.accept(handler, point, param);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }

  /**
   * 包装JoinPoint
   *
   * @param joinPoint 原始的JoinPoint
   * @return 返回包装后的JoinPoint
   */
  public abstract JP wrap(JoinPoint joinPoint);

  public interface ParameterBiConsumer<T, U, P> {

    /**
     * 处理
     */
    void accept(T t, U u, P param);

  }

}
