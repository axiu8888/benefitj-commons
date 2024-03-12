package com.benefitj.http;

import io.reactivex.rxjava3.observers.DefaultObserver;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

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

  public static <T> SimpleObserver<T> create(Consumer<T> onNext) {
    return create(onNext, Throwable::printStackTrace);
  }

  public static <T> SimpleObserver<T> create(Consumer<T> onNext,
                                             Consumer<Throwable> onError) {
    return create(() -> {/*^_^*/}, onNext, onError);
  }

  public static <T> SimpleObserver<T> create(Runnable onStart,
                                             Consumer<T> onNext,
                                             Consumer<Throwable> onError) {
    return new SimpleObserver<T>() {
      @Override
      protected void onStart() {
        super.onStart();
        if (onStart != null) {
          onStart.run();
        }
      }

      @Override
      public void onNext(@NotNull T t) {
        onNext.accept(t);
      }

      @Override
      public void onError(Throwable e) {
        if (onError != null) {
          onError.accept(e);
        } else {
          e.printStackTrace();
        }
      }
    };
  }

}
