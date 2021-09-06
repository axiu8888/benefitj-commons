package com.benefitj.device;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 设备
 */
public class DeviceImpl implements Device {

  /**
   * ID
   */
  private String id;
  /**
   * 名称
   */
  private String name;
  /**
   * 类型
   */
  private String type;
  /**
   * 可用状态
   */
  private boolean active;
  /**
   * 属性
   */
  private final Map<String, Object> attrs = new ConcurrentHashMap<>();
  /**
   * 在线时间
   */
  private long onlineTime = System.currentTimeMillis();
  /**
   * 最近一次有效时间
   */
  private long activeTime = -1;

  public DeviceImpl() {
  }

  public DeviceImpl(String id) {
    this.id = id;
  }

  public DeviceImpl(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public DeviceImpl(String id, String name, String type) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  @Override
  public void setActive(boolean active) {
    this.active = active;
  }

  @Override
  public Map<String, Object> attrs() {
    return attrs;
  }

  @Override
  public long getOnlineTime() {
    return onlineTime;
  }

  @Override
  public void setOnlineTime(long onlineTime) {
    this.onlineTime = onlineTime;
  }

  @Override
  public long getActiveTime() {
    return activeTime;
  }

  @Override
  public void setActiveTime(long activeTime) {
    this.activeTime = activeTime;
  }

}
