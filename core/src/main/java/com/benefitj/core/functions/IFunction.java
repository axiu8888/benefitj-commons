package com.benefitj.core.functions;


@FunctionalInterface
public interface IFunction<T, R> {

  /**
   * Applies this function to the given argument.
   *
   * @param t the function argument
   * @return the function result
   */
  R apply(T t) throws Exception;

}
