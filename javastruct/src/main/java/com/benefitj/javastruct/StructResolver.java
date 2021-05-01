package com.benefitj.javastruct;

/**
 * 类结构解析器
 */
public interface StructResolver {

  /**
   * 解析
   *
   * @param manager 管理类
   * @param type    类型
   * @return 返回解析后的结构对象
   */
  StructClass resolve(JavaStructManager manager, Class<?> type);

}
