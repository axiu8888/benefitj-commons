package com.benefitj.core.functions;

@FunctionalInterface
public interface IBiConsumer<T, U> {

  /**
   * Performs this operation on the given arguments.
   *
   * @param t the first input argument
   * @param u the second input argument
   */
  void accept(T t, U u) throws Exception;

}
