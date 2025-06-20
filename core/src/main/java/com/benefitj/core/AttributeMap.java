package com.benefitj.core;

import java.util.Map;
import java.util.function.Function;

/**
 * 属性值
 */
public interface AttributeMap {

  /**
   * 属性集合
   */
  Map<String, Object> attrs();

  /**
   * 获取属性值
   *
   * @param key 键
   * @param <T> 值类型
   * @return 返回获取的值或默认值
   */
  default <T> T getAttr(String key) {
    return getAttr(key, null);
  }

  /**
   * 获取属性值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @param <T>          值类型
   * @return 返回获取的值或默认值
   */
  default <T> T getAttr(String key, T defaultValue) {
    return (T) attrs().getOrDefault(key, defaultValue);
  }

  /**
   * 设置属性值
   *
   * @param key   键
   * @param value 值
   * @return 返回旧的值，如果没有就是NULL
   */
  default Object setAttr(String key, Object value) {
    return attrs().put(key, value);
  }

  /**
   * 设置属性值
   *
   * @param key   键
   * @param value 值
   * @return 返回旧的值，如果没有就是NULL
   */
  default Object setAttrIfAbsent(String key, Object value) {
    return attrs().putIfAbsent(key, value);
  }

  /**
   * 设置属性值
   *
   * @param key             键
   * @param mappingFunction 自定义函数
   * @return 返回旧的值，如果没有就是NULL
   */
  default Object setAttrIfAbsent(String key, Function<String, Object> mappingFunction) {
    return attrs().computeIfAbsent(key, mappingFunction);
  }

  /**
   * 保存全部的属性
   *
   * @param map 属性
   */
  default void putAttrAll(Map<String, Object> map) {
    attrs().putAll(map);
  }

  /**
   * 移除属性
   *
   * @param key 键
   * @param <T> 值类型
   * @return 返回被移除的值
   */
  default <T> T removeAttr(String key) {
    return (T) attrs().remove(key);
  }

  /**
   * 获取属性数量
   */
  default int attrSize() {
    return attrs().size();
  }

  /**
   * 判断是否包含属性键
   *
   * @param key 值
   * @return 返回是否包含键
   */
  default boolean containsAttr(String key) {
    return attrs().containsKey(key);
  }

  /**
   * 判断是否包含属性值
   *
   * @param value 值
   * @return 返回是否包含键
   */
  default boolean containsAttrValue(Object value) {
    return attrs().containsValue(value);
  }

  /**
   * 清空属性
   */
  default void clearAttrs() {
    attrs().clear();
  }

  /**
   * 获取字节
   *
   * @param key 键
   * @return 值
   */
  default Byte getByteAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取字节数组
   *
   * @param key 键
   * @return 值
   */
  default byte[] getByteArrayAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取整型值
   *
   * @param key 键
   * @return 值
   */
  default Short getShortAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取整型值
   *
   * @param key 键
   * @return 值
   */
  default Integer getIntegerAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取整型值
   *
   * @param key 键
   * @return 值
   */
  default Long getLongAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取单精度浮点值
   *
   * @param key 键
   * @return 值
   */
  default Float getFloatAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取双精度浮点值
   *
   * @param key 键
   * @return 值
   */
  default Double getDoubleAttr(String key) {
    return getAttr(key);
  }

  /**
   * 获取布尔值
   *
   * @param key 键
   * @return 值
   */
  default Boolean getBooleanAttr(String key) {
    return getAttr(key);
  }

  static AttributeMap wrap(Map<String, Object> map) {
    return () -> map;
  }

}
