package com.benefitj.core.device;

import com.benefitj.core.executable.Instantiator;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 设备工厂
 *
 * @param <K> 键
 * @param <V> 值
 */
public interface DeviceFactory<K, V extends Device<K>> {

  /**
   * 创建设备
   *
   * @param id    设备ID
   * @param attrs 附加属性
   * @return 返回新创建的设备对象
   */
  V create(K id, @Nullable Map<String, Object> attrs);

  /**
   * 创建设备工厂
   *
   * @param deviceType 设备类型
   * @return 返回对应设备
   */
  static <K, V extends Device<K>> ReflectiveDeviceFactory<K, V> newFactory(Class<V> deviceType) {
    return new ReflectiveDeviceFactory<>(deviceType);
  }

  class ReflectiveDeviceFactory<K, T extends Device<K>> implements DeviceFactory<K, T> {

    final Class<? extends T> deviceType;

    public ReflectiveDeviceFactory(Class<? extends T> deviceType) {
      this.deviceType = deviceType;
    }

    @Override
    public T create(K id, @Nullable Map<String, Object> attrs) {
      Object[] args;
      if (attrs == null || attrs.isEmpty()) {
        args = new Object[]{id};
      } else {
        List<Object> values = new LinkedList<>();
        values.add(id);
        attrs.forEach((k, v) -> values.add(v));
        args = values.toArray();
      }
      return Instantiator.get().create(deviceType, args);
    }

  }

}
