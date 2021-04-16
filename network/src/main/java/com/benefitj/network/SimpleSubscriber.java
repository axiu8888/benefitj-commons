package com.benefitj.network;

import io.reactivex.FlowableSubscriber;
import org.jetbrains.annotations.NotNull;
import org.reactivestreams.Subscription;

public abstract class SimpleSubscriber<T> implements FlowableSubscriber<T> {

  protected Subscription s;

  @Override
  public void onSubscribe(@NotNull Subscription s) {
    this.s = s;
    s.request(Integer.MAX_VALUE);
  }

  @Override
  public abstract void onNext(T t);

  @Override
  public void onError(Throwable t) {
    onFinish(t);
  }

  @Override
  public void onComplete() {
    onFinish(null);
  }

  public void onFinish(Throwable e) {
    if (e != null) {
      e.printStackTrace();
    }
  }

}
