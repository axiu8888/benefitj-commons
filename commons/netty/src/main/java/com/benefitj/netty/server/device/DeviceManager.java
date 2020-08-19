package com.benefitj.netty.server.device;

import java.util.Map;

/**
 * 设备管理
 *
 * @param <D> 设备类型
 */
public interface DeviceManager<D extends Device> extends Map<String, D> {
}
