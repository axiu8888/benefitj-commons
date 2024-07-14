package com.benefitj.core;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class CountDownLatch2 extends CountDownLatch {

  public CountDownLatch2(int count) {
    super(count);
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

}
