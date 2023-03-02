package com.benefitj.http;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

import java.lang.annotation.*;

/**
 * 调度对象
 *
 * @author dxa
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SchedulerOn {

  Type subscribeOn() default Type.NONE;

  Type observeOn() default Type.NONE;

  enum Type {
    SINGLE,
    COMPUTATION,
    IO,
    TRAMPOLINE,
    NEW_THREAD,
    CUSTOM,
    NONE;

    public Scheduler getScheduler() {
      return getScheduler(this);
    }

    public static Scheduler getScheduler(Type type) {
      switch (type) {
        case SINGLE:
          return Schedulers.single();
        case COMPUTATION:
          return Schedulers.computation();
        case IO:
          return Schedulers.io();
        case TRAMPOLINE:
          return Schedulers.trampoline();
        case NEW_THREAD:
          return Schedulers.newThread();
        case CUSTOM:
        case NONE:
        default:
          return null;
      }
    }
  }

}
