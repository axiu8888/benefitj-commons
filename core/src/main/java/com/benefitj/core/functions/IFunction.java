package com.benefitj.core.functions;


import java.io.Serializable;

@FunctionalInterface
public interface IFunction<T, R> extends Serializable {

  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   */
  R apply(T t) throws Exception;

}
