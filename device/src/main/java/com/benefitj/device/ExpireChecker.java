package com.benefitj.device;


/**
 * 设备超时检测
 *
 * @param <T>
 */
public interface ExpireChecker<Id, T extends Device<Id>> {

  /**
   * 检测
   *
   * @param manager 设备管理器
   */
  void check(DeviceManager<Id, T> manager);

}
