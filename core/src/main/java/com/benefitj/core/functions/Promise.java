package com.benefitj.core.functions;

import java.util.function.Consumer;


public interface Promise<T> {

  /**
   * 下一步
   *
   * @param t 结果
   */
  void onNext(T t);

  /**
   * 错误
   *
   * @param error 错误信息
   */
  void onError(Throwable error);


  static <T> Promise<T> newPromise(Consumer<T> next,
                                   Consumer<Throwable> error) {
    return new DelegatePromise<>(next, error);
  }


  abstract class Impl<T> implements Promise<T> {

    @Override
    public void onError(Throwable e) {
      e.printStackTrace();
    }
  }

  class DelegatePromise<T> extends Impl<T> {

    private Consumer<T> next;
    private Consumer<Throwable> error;

    public DelegatePromise(Consumer<T> next, Consumer<Throwable> error) {
      this.next = next;
      this.error = error;
    }

    @Override
    public void onNext(T t) {
      next.accept(t);
    }

    @Override
    public void onError(Throwable e) {
      error.accept(e);
    }
  }

}
