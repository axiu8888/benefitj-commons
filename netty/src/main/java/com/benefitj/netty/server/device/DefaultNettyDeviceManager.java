package com.benefitj.netty.server.device;

import com.benefitj.device.DefaultDeviceManager;
import com.benefitj.device.DeviceListener;

/**
 * 设备管理
 *
 * @param <D> 设备类型
 */
public class DefaultNettyDeviceManager<D extends NettyDevice>
    extends DefaultDeviceManager<D> implements NettyDeviceManager<D> {

  @Override
  public void setDeviceListener(DeviceListener<D> listener) {
    if (!(listener instanceof NettyDeviceListener)) {
      throw new IllegalArgumentException("不支持的监听类型");
    }
    super.setDeviceListener(listener);
  }

}
