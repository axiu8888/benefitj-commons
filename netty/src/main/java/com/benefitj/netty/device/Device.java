package com.benefitj.netty.device;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 设备
 */
public interface Device<Id> {

  /**
   * 获取ID
   */
  Id getId();

  /**
   * 设置ID
   *
   * @param id 设备唯一主键
   */
  void setId(Id id);

  /**
   * 获取名称
   */
  String getName();

  /**
   * 设置名称
   *
   * @param name 设备名称
   */
  void setName(String name);

  /**
   * 获取类型
   */
  String getType();

  /**
   * 设置类型
   *
   * @param type 设备类型
   */
  void setType(String type);

  /**
   * 是否可用
   */
  boolean isActive();

  /**
   * 设置是否可用
   *
   * @param active 可用状态
   */
  void setActive(boolean active);

  /**
   * 获取在线时间
   */
  long getOnlineTime();

  /**
   * 设置在线时间
   *
   * @param onlineTime 在线时间
   */
  void setOnlineTime(long onlineTime);

  /**
   * 获取Active时间
   */
  long getActiveAt();

  /**
   * 设置Active时间
   *
   * @param activeAt
   */
  void setActiveAt(long activeAt);

  /**
   * 设置Active时间
   */
  default void setActiveAtNow() {
    setActiveAt(System.currentTimeMillis());
  }

  /**
   * 可选属性
   */
  Map<String, Object> attrs();

  /**
   * 获取属性的数量
   */
  default int attrsSize() {
    return attrs().size();
  }

  /**
   * 设置属性
   *
   * @param name  属性名称
   * @param value 属性值
   * @param <T>   值类型
   * @return 返回被替换的属性值
   */
  default <T> T setAttr(String name, T value) {
    return (T) attrs().put(name, value);
  }

  /**
   * 获取属性值
   *
   * @param name 属性名称
   * @param <T>  属性值类型
   * @return 返回属性值
   */
  default <T> T getAttr(String name) {
    return (T) attrs().get(name);
  }

  /**
   * 获取属性值
   *
   * @param name         属性名称
   * @param defaultValue 默认值
   * @param <T>          属性值类型
   * @return 返回属性值
   */
  default <T> T getAttr(String name, T defaultValue) {
    return (T) attrs().getOrDefault(name, defaultValue);
  }

  /**
   * 检查是否保存有个属性
   *
   * @param name 属性名称
   * @return 返回是否存在
   */
  default boolean hasAttr(String name) {
    return attrs().containsKey(name);
  }

  /**
   * 获取全部的属性名
   */
  default Set<String> getAttrNames() {
    return attrs().keySet();
  }

  /**
   * 获取全部的属性值
   */
  default Collection<Object> getAttrValues() {
    return attrs().values();
  }

  /**
   * 移除属性值
   *
   * @param key 属性键
   * @param <T> 属性值类型
   * @return 返回被移除的属性值，如果没有，返回 NULL
   */
  default <T> T removeAttr(String key) {
    return (T) attrs().remove(key);
  }

  /**
   * 清空所有属性值
   */
  default void clearAttrs() {
    attrs().clear();
  }

}
