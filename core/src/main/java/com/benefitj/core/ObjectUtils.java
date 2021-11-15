package com.benefitj.core;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 对象工具
 */
public class ObjectUtils {

  /**
   * 类以及字段信息
   */
  private static final Map<Class<?>, ClassInfo> classInfos = new WeakHashMap<>();
  /**
   * 默认的转换器
   */
  private static final Map<Class<?>, BiFunction<Field, Object, String>> converters;

  static {
    Map<Class<?>, BiFunction<Field, Object, String>> map = new HashMap<>();

    map.put(byte[].class, (field, value) -> Arrays.toString((byte[]) value));
    map.put(short[].class, (field, value) -> Arrays.toString((short[]) value));
    map.put(int[].class, (field, value) -> Arrays.toString((int[]) value));
    map.put(long[].class, (field, value) -> Arrays.toString((long[]) value));
    map.put(float[].class, (field, value) -> Arrays.toString((float[]) value));
    map.put(double[].class, (field, value) -> Arrays.toString((double[]) value));
    map.put(boolean[].class, (field, value) -> Arrays.toString((boolean[]) value));
    map.put(String[].class, (field, value) -> Arrays.toString((String[]) value));
    map.put(char[].class, (field, value) -> Arrays.toString((char[]) value));

    map.put(Byte[].class, (field, value) -> Arrays.toString((Byte[]) value));
    map.put(Short[].class, (field, value) -> Arrays.toString((Short[]) value));
    map.put(Integer[].class, (field, value) -> Arrays.toString((Integer[]) value));
    map.put(Long[].class, (field, value) -> Arrays.toString((Long[]) value));
    map.put(Float[].class, (field, value) -> Arrays.toString((Float[]) value));
    map.put(Double[].class, (field, value) -> Arrays.toString((Double[]) value));
    map.put(Boolean[].class, (field, value) -> Arrays.toString((Boolean[]) value));
    map.put(Character[].class, (field, value) -> Arrays.toString((Character[]) value));

    map.put(Date.class, (field, value) -> DateFmtter.fmt(value));
    map.put(java.sql.Date.class, (field, value) -> DateFmtter.fmt(value));
    map.put(Timestamp.class, (field, value) -> DateFmtter.fmtS(value));
    map.put(LocalDate.class, (field, value) -> ((LocalDate) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    map.put(LocalTime.class, (field, value) -> ((LocalTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
    map.put(LocalDateTime.class, (field, value) -> ((LocalDateTime) value).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

    converters = Collections.unmodifiableMap(map);
  }

  private static final BiFunction<Field, Object, String> DEFAULT_CONVERTER = ObjectUtils::defaultToString;

  /**
   * 获取类型信息
   *
   * @param type 类型
   * @return 返回类型信息
   */
  public static ClassInfo getClassInfo(Class<?> type) {
    return classInfos.get(type);
  }

  /**
   * 获取对应的转换器
   */
  public static BiFunction<Field, Object, String> getConverter(Class<?> type) {
    return getConverter(type, false);
  }

  /**
   * 获取对应的转换器
   */
  public static BiFunction<Field, Object, String> getConverter(Class<?> type, boolean assignable) {
    BiFunction<Field, Object, String> converter = converters.get(type);
    if (converter == null && assignable) {
      for (Map.Entry<Class<?>, BiFunction<Field, Object, String>> entry : converters.entrySet()) {
        if (type.isAssignableFrom(entry.getKey())) {
          return entry.getValue();
        }
      }
    }
    return converter;
  }

  /**
   * 解析
   *
   * @param type 类型
   * @return 返回类型信息
   */
  public static ClassInfo parseClassInfo(Class<?> type) {
    ClassInfo classInfo = new ClassInfo(type);
    // 字段
    Map<Field, String> fields = classInfo.getFields();
    ReflectUtils.foreachField(type
        , f -> isNotStaticOrFinal(f.getModifiers())
        , f -> fields.put(f, f.getName())
        , f -> false
    );
    // setter方法
    Map<String, Method> setterMethods = classInfo.getSetterMethods();
    ReflectUtils.foreachMethod(type
        , m -> isNotStaticOrFinal(m.getModifiers()) && ReflectUtils.isSetterMethod(m)
        , m -> setterMethods.putIfAbsent(m.getName(), m)
        , m -> false
    );
    // getter方法
    Map<String, Method> getterMethods = classInfo.getGetterMethods();
    ReflectUtils.foreachMethod(type
        , m -> isNotStaticOrFinal(m.getModifiers()) && ReflectUtils.isGetterMethod(m)
        , m -> getterMethods.putIfAbsent(m.getName(), m)
        , m -> false
    );
    return classInfo;
  }

  /**
   * 默认的toString
   *
   * @param field 字段
   * @param value 值或对象
   * @return 返回toString的字符串，或null值
   */
  public static String defaultToString(@Nullable Field field, Object value) {
    if (value != null) {
      BiFunction<Field, Object, String> converter = getConverter(value.getClass());
      return converter != null ? converter.apply(field, value) : value.toString();
    }
    return null;
  }

  /**
   * 转换 toString
   *
   * @param o 对象
   * @return 返回 toString
   */
  public static String toString(Object o) {
    return toString(o, null, DEFAULT_CONVERTER);
  }

  /**
   * 转换 toString
   *
   * @param o 对象
   * @return 返回 toString
   */
  public static String toString(Object o, BiFunction<Field, Object, String> converter) {
    return toString(o, f -> true, converter);
  }

  /**
   * 转换 toString
   *
   * @param o      对象
   * @param filter 过滤器
   * @return 返回 toString
   */
  public static String toString(Object o, Predicate<Field> filter, BiFunction<Field, Object, String> converter) {
    final Class<?> type = o.getClass();
    ClassInfo classInfo = classInfos.computeIfAbsent(type, s -> parseClassInfo(type));
    final StringBuilder sb = new StringBuilder();
    String name = type.getSimpleName();
    sb.append(name);
    sb.append("(");
    classInfo.getFields().forEach((field, s) -> {
          if (filter == null || filter.test(field)) {
            Object value = ReflectUtils.getFieldValue(field, o);
            String show = converter.apply(field, value);
            sb.append(", ").append(field.getName())
                .append("=")
                .append(show);
          }
        }
    );
    sb.append(")");
    sb.replace(name.length() + 1, name.length() + 3, "");
    return sb.toString();
  }


  private static boolean isNotStaticOrFinal(int modifiers) {
    return !ReflectUtils.isStaticOrFinal(modifiers);
  }


  public static class ClassInfo {
    /**
     * 类型
     */
    private final Class<?> type;
    /**
     * 字段，字段名可能会重复
     */
    private final Map<Field, String> fields = new LinkedHashMap<>();
    /**
     * getter方法，方法不重复
     */
    private final Map<String, Method> getterMethods = new LinkedHashMap<>();
    /**
     * setter方法，不重复
     */
    private final Map<String, Method> setterMethods = new LinkedHashMap<>();

    public ClassInfo(Class<?> type) {
      this.type = type;
    }

    public Class<?> getType() {
      return type;
    }

    public Map<Field, String> getFields() {
      return fields;
    }

    public Map<String, Method> getGetterMethods() {
      return getterMethods;
    }

    public Map<String, Method> getSetterMethods() {
      return setterMethods;
    }

    public Method getGetterMethod(String method) {
      return getGetterMethods().get(method);
    }

    public Method getSetterMethod(String method) {
      return getSetterMethods().get(method);
    }

  }

}
