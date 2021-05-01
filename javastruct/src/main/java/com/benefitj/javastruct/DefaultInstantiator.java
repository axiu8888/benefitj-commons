package com.benefitj.javastruct;

/**
 * 默认的实例化器
 */
public class DefaultInstantiator implements Instantiator {
  /**
   * 创建对象
   *
   * @param type 对象类型
   * @return 返回创建的对象
   */
  @Override
  public Object create(Class<?> type) {
    try {
      return type.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }
}
