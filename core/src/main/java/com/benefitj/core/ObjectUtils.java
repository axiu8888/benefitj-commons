package com.benefitj.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;

/**
 * 对象工具
 */
public class ObjectUtils {

  /**
   * 类以及字段信息
   */
  private static final Map<Class<?>, ClassInfo> classInfoMap = new WeakHashMap<>();

  /**
   * 获取类型信息
   *
   * @param type 类型
   * @return 返回类型信息
   */
  public static ClassInfo getClassInfo(Class<?> type) {
    return classInfoMap.get(type);
  }

  /**
   * 转换 toString
   *
   * @param o 对象
   * @return 返回 toString
   */
  public static String toString(Object o) {
    return toString(o, null, (field, value) -> value != null ? String.valueOf(value) : "");
  }

  /**
   * 转换 toString
   *
   * @param o      对象
   * @param filter 过滤器
   * @return 返回 toString
   */
  public static String toString(Object o, Predicate<Field> filter, BiFunction<Field, Object, String> func) {
    final Class<?> type = o.getClass();
    ClassInfo classInfo = classInfoMap.computeIfAbsent(type, s -> parseClassInfo(type));
    final StringBuilder sb = new StringBuilder();
    String name = type.getSimpleName();
    sb.append(name);
    sb.append("(");
    classInfo.getFields().forEach((field, s) -> {
          if (filter == null || filter.test(field)) {
            Object value = ReflectUtils.getFieldValue(field, o);
            String show = func.apply(field, value);
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
