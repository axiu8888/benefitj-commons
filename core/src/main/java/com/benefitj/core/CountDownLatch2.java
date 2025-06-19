package com.benefitj.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


public class CountDownLatch2 extends CountDownLatch {

  static final Consumer<InterruptedException> THROW_HANDLER = e -> {
    throw new IllegalStateException(e);
  };
  static final Consumer<InterruptedException> PRINT_HANDLER = Throwable::printStackTrace;
  static final Consumer<InterruptedException> IGNORE_HANDLER = e -> {/*^_^*/};

  final Consumer<InterruptedException> errorHandler;

  public CountDownLatch2(int count) {
    this(count, THROW_HANDLER);
  }

  public CountDownLatch2(int count, Consumer<InterruptedException> errorHandler) {
    super(count);
    this.errorHandler = errorHandler;
  }

  @Override
  public void await() {
    try {
      super.await();
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  @Override
  public boolean await(long timeout, TimeUnit unit) {
    try {
      return super.await(timeout, unit);
    } catch (InterruptedException e) {
      throw new IllegalStateException(e);
    }
  }

  public static CountDownLatch2 ignore(int count) {
    return new CountDownLatch2(count, IGNORE_HANDLER);
  }

  public static CountDownLatch2 print(int count) {
    return new CountDownLatch2(count, PRINT_HANDLER);
  }

  public static CountDownLatch2 raise(int count) {
    return new CountDownLatch2(count, THROW_HANDLER);
  }

}
