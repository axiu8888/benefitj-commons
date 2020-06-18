package com.benefitj.core.local;

public interface InitialCallback<T> {
  /**
   * 初始化值
   *
   * @return 返回初始化值
   */
  T initialValue();
}
