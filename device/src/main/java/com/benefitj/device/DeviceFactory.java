package com.benefitj.device;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * 设备工厂
 *
 * @param <T>
 */
public interface DeviceFactory<T extends Device> {

  /**
   * 创建设备
   *
   * @param id    设备ID
   * @param attrs 附加属性
   * @return 返回新创建的设备对象
   */
  T create(String id, @Nullable Map<String, Object> attrs);

}
