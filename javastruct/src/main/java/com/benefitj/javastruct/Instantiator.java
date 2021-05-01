package com.benefitj.javastruct;

/**
 * 实例化器
 */
public interface Instantiator {

  /**
   * 创建对象
   *
   * @param type 对象类型
   * @return 返回创建的对象
   */
  Object create(Class<?> type);

}
