package com.benefitj.netty.device;

import com.benefitj.core.TimeUtils;
import com.benefitj.core.Utils;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDevice<Id extends Serializable> implements Device<Id> {
  /**
   * ID
   */
  private Id id;
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
  private long activeAt = -1;

  public AbstractDevice() {
  }

  @Override
  public Id getId() {
    return id;
  }

  @Override
  public void setId(Id id) {
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
  public long getActiveAt() {
    return activeAt;
  }

  @Override
  public void setActiveAt(long activeAt) {
    this.activeAt = activeAt;
  }

  @Override
  public String toString() {
    return String.format("%s(%s, %s, %s, %s)"
        , getClass().getSimpleName()
        , getId()
        , getType()
        , getName()
        , Utils.fmt(TimeUtils.diffNow(getOnlineTime()) / 1000.0, ".0s")
    );
  }
}
