package com.benefitj.http;


import io.reactivex.rxjava3.observers.DefaultObserver;

import javax.annotation.Nullable;

public abstract class FinishObserver<T> extends DefaultObserver<T> {

  @Override
  public abstract void onNext(T t);

  @Override
  public final void onError(Throwable e) {
    try {
      onThrowable(e);
    } finally {
      onFinish(e);
    }
  }

  @Override
  public final void onComplete() {
    onFinish(null);
  }

  public void onThrowable(Throwable e) {
    e.printStackTrace();
  }

  public void onFinish(@Nullable Throwable e) {
  }

}
