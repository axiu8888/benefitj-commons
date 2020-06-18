package com.benefitj.network;

import io.reactivex.observers.DefaultObserver;

public abstract class SimpleObserver<T> extends DefaultObserver<T> {

  @Override
  protected void onStart() {
  }

  @Override
  public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override
  public void onComplete() {
  }

}
