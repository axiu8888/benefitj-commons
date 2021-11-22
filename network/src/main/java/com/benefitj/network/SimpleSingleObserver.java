package com.benefitj.network;

import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import org.jetbrains.annotations.NotNull;

public abstract class SimpleSingleObserver<T> implements SingleObserver<T> {
  @Override
  public void onSubscribe(@NotNull Disposable d) {
    d.dispose();
  }

  @Override
  public abstract void onSuccess(@NotNull T t);

  @Override
  public void onError(@NotNull Throwable e) {
    e.printStackTrace();
  }
}
