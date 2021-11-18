package com.benefitj.core.property;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

public interface PropertiesConverter {

  Function<String, Short> SHORT_FUNC = Short::valueOf;
  Function<String, Integer> INTEGER_FUNC = Integer::valueOf;
  Function<String, Long> LONG_FUNC = Long::valueOf;
  Function<String, Float> FLOAT_FUNC = Float::valueOf;
  Function<String, Double> DOUBLE_FUNC = Double::valueOf;
  Function<String, Boolean> BOOLEAN_FUNC = Boolean::valueOf;


  Properties getSource();

  /**
   * 获取转换的值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @param func         转换函数
   * @param <T>          类型
   * @return 返回转换的值
   */
  default <T> T getValue(String key, T defaultValue, Function<String, T> func) {
    String value = getSource().getProperty(key);
    if (StringUtils.isNotBlank(value)) {
      return func.apply(value);
    }
    return defaultValue;
  }

  /**
   * 获取属性值
   *
   * @param key 键
   * @return 返回值
   */
  default Object getValue(String key) {
    return getSource().getProperty(key);
  }

  /**
   * 获取Integer值
   *
   * @param key 键
   * @return 返回获取的值
   */
  default Short getShort(String key) {
    return getShort(key, null);
  }

  /**
   * 获取Short值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Short getShort(String key, Short defaultValue) {
    return getValue(key, defaultValue, SHORT_FUNC);
  }

  /**
   * 获取Integer值
   *
   * @param key 键
   * @return 返回获取的值
   */
  default Integer getInteger(String key) {
    return getInteger(key, null);
  }

  /**
   * 获取Integer值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Integer getInteger(String key, Integer defaultValue) {
    return getValue(key, defaultValue, INTEGER_FUNC);
  }

  /**
   * 获取Long值
   *
   * @param key 键
   * @return 返回获取的值
   */
  default Long getLong(String key) {
    return getLong(key, null);
  }

  /**
   * 获取Long值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Long getLong(String key, Long defaultValue) {
    return getValue(key, defaultValue, LONG_FUNC);
  }

  /**
   * 获取Float值
   *
   * @param key 键
   * @return 返回获取的值
   */
  default Float getFloat(String key) {
    return getFloat(key, null);
  }

  /**
   * 获取Float值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Float getFloat(String key, Float defaultValue) {
    return getValue(key, defaultValue, FLOAT_FUNC);
  }

  /**
   * 获取Double值
   *
   * @param key 键
   * @return 返回获取的值
   */
  default Double getDouble(String key) {
    return getDouble(key, null);
  }

  /**
   * 获取Double值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Double getDouble(String key, Double defaultValue) {
    return getValue(key, defaultValue, DOUBLE_FUNC);
  }

  /**
   * 获取Boolean值
   *
   * @param key 键
   * @return 返回获取的值
   */
  default Boolean getBoolean(String key) {
    return getBoolean(key, null);
  }

  /**
   * 获取Boolean值
   *
   * @param key          键
   * @param defaultValue 默认值
   * @return 返回获取的值
   */
  default Boolean getBoolean(String key, Boolean defaultValue) {
    return getValue(key, defaultValue, BOOLEAN_FUNC);
  }

  /**
   * 获取全部
   */
  default Map<String, Object> getAll() {
    Map<String, Object> map = new LinkedHashMap<>();
    getSource().forEach((key, value) -> map.put(String.valueOf(key), value));
    return map;
  }

//  default <T> T convert(Class<T> type) {
//    PropertiesPrefix pp = type.getAnnotation(PropertiesPrefix.class);
//    String prefix = "";
//    if (StringUtils.isNotBlank(pp.value())) {
//      String trim = pp.value().trim();
//      prefix = trim.endsWith(".") ? trim : trim + ".";
//    }
//
////    ReflectUtils.foreachField(type
////        , ReflectUtils.NOT_STATIC_FINAL
////        , f -> {
////          f.getName()
////        }
////        , f -> false
////        );
//
//  }

}
