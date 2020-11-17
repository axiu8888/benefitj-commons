package com.benefitj.core.functions;

@FunctionalInterface
public interface IConsumer<T> {

  /**
   * Performs this operation on the given argument.
   *
   * @param t the input argument
   */
  void accept(T t) throws Exception;
}
