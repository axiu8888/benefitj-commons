package com.benefitj.network;

import io.reactivex.observers.DefaultObserver;
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

  public static <T> SimpleObserver<T> create(Consumer<T> consumer) {
    return create(consumer, Throwable::printStackTrace);
  }

  public static <T> SimpleObserver<T> create(Consumer<T> consumer, Consumer<Throwable> errorConsumer) {
    return new SimpleObserver<T>() {
      @Override
      public void onNext(@NotNull T t) {
        consumer.accept(t);
      }

      @Override
      public void onError(Throwable e) {
        errorConsumer.accept(e);
      }
    };
  }

}
