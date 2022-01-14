package com.benefitj.core.functions;

import java.io.Serializable;

@FunctionalInterface
public interface IBiFunction<T, U, R> extends Serializable {

  /**
   * Applies this function to the given arguments.
   *
   * @param t the first function argument
   * @param u the second function argument
   * @return the function result
   */
  R apply(T t, U u) throws Exception;
}
