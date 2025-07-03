package com.benefitj.core.functions;


/**
 * 3个参数的函数
 */
public interface Fun3<K1, K2, K3, V> {

  V call(K1 k1, K2 k2, K3 k3);

}
