package com.benefitj.device;


/**
 * 设备超时检测
 *
 * @param <T>
 */
public interface ExpiredChecker<T extends Device> {

  /**
   * 检测
   *
   * @param manager 设备管理器
   */
  void check(DeviceManager<T> manager);

}
