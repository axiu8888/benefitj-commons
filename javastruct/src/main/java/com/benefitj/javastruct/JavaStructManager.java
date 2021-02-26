package com.benefitj.javastruct;


import com.benefitj.core.ReflectUtils;
import com.benefitj.javastruct.annotaion.JavaStructClass;
import com.benefitj.javastruct.annotaion.JavaStructField;
import com.benefitj.javastruct.convert.DateTimeFieldConverter;
import com.benefitj.javastruct.convert.FieldConverter;
import com.benefitj.javastruct.convert.DefaultPrimitiveFieldConverter;
import com.benefitj.javastruct.convert.PrimitiveFieldConverter;
import com.benefitj.javastruct.field.*;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 结构体管理
 */
public class JavaStructManager {

  /**
   * 字段转换器
   */
  private final Map<Class<?>, FieldConverter> fieldConverters = new ConcurrentHashMap<>();
  /**
   * 缓存的类
   */
  private final Map<Class, StructClass> structClasses = new WeakHashMap<>();

  public JavaStructManager() {
  }

  public void init() {
    this.fieldConverters.put(PrimitiveFieldConverter.class, new DefaultPrimitiveFieldConverter());
    this.fieldConverters.put(DateTimeFieldConverter.class, new DateTimeFieldConverter());
  }

  public Map<Class, StructClass> getStructClasses() {
    return structClasses;
  }

  public Map<Class<?>, FieldConverter> getFieldConverters() {
    return fieldConverters;
  }

  public FieldConverter getFieldConverter(Class<?> converterType) {
    return getFieldConverters().get(converterType);
  }

  public byte[] convert(Object o) {
    StructClass structClass = this.structClasses.computeIfAbsent(o.getClass(), this::parseStructClass);
    return structClass.convert(o);
  }

  protected StructClass parseStructClass(Class<?> type) {
    JavaStructClass jsc = type.getAnnotation(JavaStructClass.class);
    if (jsc == null) {
      throw new IllegalStateException("不支持的结构类[" + type + "]，请使用@ClassStruct注释！");
    }
    StructClass structClass = new StructClass(type);
    ReflectUtils.foreachField(type
        , f -> f.isAnnotationPresent(JavaStructField.class)
        , f -> {
          PrimitiveType primitiveType = PrimitiveType.getFieldType(f.getType());
          JavaStructField jsf = f.getAnnotation(JavaStructField.class);
          FieldConverter fc = null;

          if (jsf.converter() != FieldConverter.class) {
            fc = getFieldConverter(jsf.converter());
          }
          if (fc == null) {
            for (Map.Entry<Class<?>, FieldConverter> entry : getFieldConverters().entrySet()) {
              FieldConverter value = entry.getValue();
              if (value.support(f, jsf, primitiveType)) {
                fc = value;
                break;
              }
            }
          }

          if (fc == null) {
            throw new IllegalStateException("无法发现转换器: " + jsf.converter().getName());
          }


          if (!fc.support(f, jsf, primitiveType)) {
            throw new IllegalArgumentException(String.format(
                "不支持的数据类型: %s.%s [%s]", f.getDeclaringClass().getName(), f.getName(), f.getType().getName()));
          }

          if (primitiveType == PrimitiveType.STRING
              && jsf.size() <= 0) {
            throw new IllegalStateException(String.format(
                "请指定[%s.%s]的长度", f.getDeclaringClass().getName(), f.getName()));
          }

          StructField structField = new StructField(f);
          structField.setPrimitiveType(primitiveType);
          structField.setStructField(jsf);
          structField.setConverter(fc);
          structClass.getFields().add(structField);
        }, f -> false);
    // 结构体大小
    structClass.setSize(Math.max(jsc.value(), structClass.getFields().stream()
        .mapToInt(StructField::size)
        .sum()));
    return structClass;
  }


}
