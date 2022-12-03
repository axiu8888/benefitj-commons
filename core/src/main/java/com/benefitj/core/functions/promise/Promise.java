package com.benefitj.core.functions.promise;

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


  class PromiseImpl<T> implements Promise<T> {


    @Override
    public void onNext(T t) {

    }

    @Override
    public void onError(Throwable error) {

    }
  }


}
