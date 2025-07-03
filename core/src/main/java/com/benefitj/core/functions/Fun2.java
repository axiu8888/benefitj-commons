package com.benefitj.core.functions;


/**
 * 2个参数的函数
 */
public interface Fun2<K1, K2, V> {

  V call(K1 k1, K2 k2);

}
